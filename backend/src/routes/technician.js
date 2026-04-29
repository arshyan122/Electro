const express = require('express');
const bcrypt = require('bcryptjs');
const User = require('../models/User');
const Technician = require('../models/Technician');
const TechnicianService = require('../models/TechnicianService');
const { Request: ServiceRequest } = require('../models/Request');
const { signToken, authRequired, requireRole } = require('../auth');
const { signUpload, isConfigured: cloudinaryConfigured } = require('../cloudinary');

const router = express.Router();
const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const asyncHandler = (fn) => (req, res, next) =>
  Promise.resolve(fn(req, res, next)).catch(next);

// -------------------- Auth: register / login --------------------

router.post(
  '/register',
  asyncHandler(async (req, res) => {
    const {
      email,
      password,
      name,
      phone,
      specialization,
      experienceYears,
      bio
    } = req.body || {};

    if (!email || !EMAIL_RE.test(email)) {
      return res.status(400).json({ error: 'A valid email is required.' });
    }
    if (!password || password.length < 6) {
      return res.status(400).json({ error: 'Password must be at least 6 characters.' });
    }
    if (!name || name.trim().length < 1) {
      return res.status(400).json({ error: 'Name is required.' });
    }

    const passwordHash = await bcrypt.hash(password, 10);

    let user;
    try {
      user = await User.create({
        email: email.trim().toLowerCase(),
        name: name.trim(),
        passwordHash,
        role: 'technician'
      });
    } catch (err) {
      if (err && err.code === 11000) {
        return res
          .status(409)
          .json({ error: 'An account with that email already exists.' });
      }
      throw err;
    }

    await Technician.create({
      userId: user._id,
      phone: (phone || '').trim(),
      specialization: Array.isArray(specialization)
        ? specialization
        : typeof specialization === 'string' && specialization.length > 0
        ? specialization.split(',').map((s) => s.trim()).filter(Boolean)
        : [],
      experienceYears: Number.isFinite(Number(experienceYears))
        ? Number(experienceYears)
        : 0,
      bio: (bio || '').toString().slice(0, 500),
      available: true
    });

    const safe = user.toJSON();
    return res.status(201).json({ token: signToken(safe), user: safe });
  })
);

router.post(
  '/login',
  asyncHandler(async (req, res) => {
    const { email, password } = req.body || {};
    if (!email || !password) {
      return res.status(400).json({ error: 'Email and password are required.' });
    }
    const user = await User.findOne({ email: email.trim().toLowerCase() });
    if (!user || user.role !== 'technician') {
      return res.status(401).json({ error: 'Invalid email or password.' });
    }
    const ok = await bcrypt.compare(password, user.passwordHash);
    if (!ok) return res.status(401).json({ error: 'Invalid email or password.' });

    const safe = user.toJSON();
    return res.json({ token: signToken(safe), user: safe });
  })
);

// -------------------- Profile --------------------

router.get(
  '/me',
  authRequired,
  requireRole('technician'),
  asyncHandler(async (req, res) => {
    const user = await User.findById(req.user.sub);
    if (!user) return res.status(404).json({ error: 'User not found.' });
    const profile = await Technician.findOne({ userId: user._id });
    return res.json({
      user: user.toJSON(),
      profile: profile ? profile.toJSON() : null
    });
  })
);

router.patch(
  '/me',
  authRequired,
  requireRole('technician'),
  asyncHandler(async (req, res) => {
    const { name, phone, specialization, experienceYears, bio, profileImageUrl, available } =
      req.body || {};

    if (typeof name === 'string' && name.trim().length > 0) {
      await User.updateOne({ _id: req.user.sub }, { $set: { name: name.trim() } });
    }

    const profileUpdate = {};
    if (typeof phone === 'string') profileUpdate.phone = phone.trim();
    if (Array.isArray(specialization)) profileUpdate.specialization = specialization;
    else if (typeof specialization === 'string') {
      profileUpdate.specialization = specialization
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean);
    }
    if (Number.isFinite(Number(experienceYears))) {
      profileUpdate.experienceYears = Math.max(0, Number(experienceYears));
    }
    if (typeof bio === 'string') profileUpdate.bio = bio.slice(0, 500);
    if (typeof profileImageUrl === 'string') profileUpdate.profileImageUrl = profileImageUrl;
    if (typeof available === 'boolean') profileUpdate.available = available;

    if (Object.keys(profileUpdate).length > 0) {
      await Technician.updateOne(
        { userId: req.user.sub },
        { $set: profileUpdate },
        { upsert: true }
      );
    }

    const user = await User.findById(req.user.sub);
    const profile = await Technician.findOne({ userId: req.user.sub });
    return res.json({
      user: user.toJSON(),
      profile: profile ? profile.toJSON() : null
    });
  })
);

// -------------------- Technician's own service catalogue --------------------

router.get(
  '/services',
  authRequired,
  requireRole('technician'),
  asyncHandler(async (req, res) => {
    const list = await TechnicianService.find({ technicianId: req.user.sub }).sort({
      createdAt: -1
    });
    return res.json(list.map((s) => s.toJSON()));
  })
);

router.post(
  '/services',
  authRequired,
  requireRole('technician'),
  asyncHandler(async (req, res) => {
    const { name, description, category, price } = req.body || {};
    if (!name || !category || !Number.isFinite(Number(price))) {
      return res
        .status(400)
        .json({ error: 'name, category, and numeric price are required.' });
    }
    const created = await TechnicianService.create({
      technicianId: req.user.sub,
      name: name.trim(),
      description: (description || '').toString().slice(0, 500),
      category: category.trim(),
      price: Math.max(0, Number(price))
    });
    return res.status(201).json(created.toJSON());
  })
);

router.patch(
  '/services/:id',
  authRequired,
  requireRole('technician'),
  asyncHandler(async (req, res) => {
    const { name, description, category, price, active } = req.body || {};
    const update = {};
    if (typeof name === 'string') update.name = name.trim();
    if (typeof description === 'string') update.description = description.slice(0, 500);
    if (typeof category === 'string') update.category = category.trim();
    if (Number.isFinite(Number(price))) update.price = Math.max(0, Number(price));
    if (typeof active === 'boolean') update.active = active;

    const result = await TechnicianService.findOneAndUpdate(
      { _id: req.params.id, technicianId: req.user.sub },
      { $set: update },
      { new: true }
    );
    if (!result) return res.status(404).json({ error: 'Service not found.' });
    return res.json(result.toJSON());
  })
);

router.delete(
  '/services/:id',
  authRequired,
  requireRole('technician'),
  asyncHandler(async (req, res) => {
    const result = await TechnicianService.findOneAndDelete({
      _id: req.params.id,
      technicianId: req.user.sub
    });
    if (!result) return res.status(404).json({ error: 'Service not found.' });
    return res.json({ ok: true });
  })
);

// -------------------- Dashboard counts --------------------

router.get(
  '/dashboard',
  authRequired,
  requireRole('technician'),
  asyncHandler(async (req, res) => {
    const tid = req.user.sub;
    const [pending, accepted, inProgress, completed, totalAssigned] = await Promise.all([
      ServiceRequest.countDocuments({
        status: 'pending',
        rejectedBy: { $ne: tid }
      }),
      ServiceRequest.countDocuments({ technicianId: tid, status: 'accepted' }),
      ServiceRequest.countDocuments({ technicianId: tid, status: 'in_progress' }),
      ServiceRequest.countDocuments({ technicianId: tid, status: 'completed' }),
      ServiceRequest.countDocuments({ technicianId: tid })
    ]);
    return res.json({
      openPending: pending,
      activeAccepted: accepted,
      activeInProgress: inProgress,
      completed,
      totalAssigned
    });
  })
);

// -------------------- Cloudinary signed-upload helper --------------------

router.post(
  '/uploads/sign',
  authRequired,
  asyncHandler(async (req, res) => {
    if (!cloudinaryConfigured()) {
      return res.status(503).json({ error: 'Image upload is not configured.' });
    }
    const folder = `electro/technicians/${req.user.sub}`;
    const sig = signUpload({ folder });
    return res.json({ ...sig, folder });
  })
);

module.exports = router;
