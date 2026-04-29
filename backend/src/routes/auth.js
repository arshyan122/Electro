const express = require('express');
const bcrypt = require('bcryptjs');
const User = require('../models/User');
const { signToken, authRequired } = require('../auth');

const router = express.Router();

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const asyncHandler = (fn) => (req, res, next) =>
  Promise.resolve(fn(req, res, next)).catch(next);

router.post(
  '/signup',
  asyncHandler(async (req, res) => {
    const { email, password, name } = req.body || {};
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
        passwordHash
      });
    } catch (err) {
      // Mongo duplicate-key error code from the unique index on `email`.
      if (err && err.code === 11000) {
        return res.status(409).json({ error: 'An account with that email already exists.' });
      }
      throw err;
    }

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
    if (!user) return res.status(401).json({ error: 'Invalid email or password.' });

    const ok = await bcrypt.compare(password, user.passwordHash);
    if (!ok) return res.status(401).json({ error: 'Invalid email or password.' });

    const safe = user.toJSON();
    return res.json({ token: signToken(safe), user: safe });
  })
);

router.get(
  '/me',
  authRequired,
  asyncHandler(async (req, res) => {
    const user = await User.findById(req.user.sub);
    if (!user) return res.status(404).json({ error: 'User not found.' });
    return res.json({ user: user.toJSON() });
  })
);

module.exports = router;
