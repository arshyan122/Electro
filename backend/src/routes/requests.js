const express = require('express');
const mongoose = require('mongoose');
const User = require('../models/User');
const { Request: ServiceRequest, REQUEST_STATUSES } = require('../models/Request');
const { authRequired, requireRole } = require('../auth');
const { notifyMany } = require('../fcm');

const router = express.Router();

const asyncHandler = (fn) => (req, res, next) =>
  Promise.resolve(fn(req, res, next)).catch(next);

const isValidId = (s) => mongoose.isValidObjectId(s);

// -------------------- Customer creates a service request --------------------

router.post(
  '/',
  authRequired,
  requireRole('user'),
  asyncHandler(async (req, res) => {
    const { category, title, description, address, price } = req.body || {};
    if (!category || !title) {
      return res.status(400).json({ error: 'category and title are required.' });
    }
    const created = await ServiceRequest.create({
      customerId: req.user.sub,
      category: category.trim(),
      title: title.trim(),
      description: (description || '').toString().slice(0, 1000),
      address: (address || '').toString().trim(),
      price: Number.isFinite(Number(price)) ? Math.max(0, Number(price)) : 0,
      status: 'pending'
    });

    // Best-effort fan-out to available technicians.
    try {
      const technicians = await User.find({ role: 'technician', fcmToken: { $ne: null } })
        .select('fcmToken')
        .lean();
      const tokens = technicians.map((t) => t.fcmToken).filter(Boolean);
      if (tokens.length > 0) {
        notifyMany(tokens, {
          title: 'New service request',
          body: `${created.category}: ${created.title}`,
          data: { requestId: created._id.toString(), type: 'NEW_REQUEST' }
        }).catch((e) => console.error('[requests] notify error:', e.message));
      }
    } catch (e) {
      console.error('[requests] fan-out error:', e.message);
    }

    return res.status(201).json(created.toJSON());
  })
);

// -------------------- Customer lists their own requests --------------------

router.get(
  '/mine',
  authRequired,
  requireRole('user'),
  asyncHandler(async (req, res) => {
    const list = await ServiceRequest.find({ customerId: req.user.sub })
      .sort({ createdAt: -1 })
      .limit(100);
    return res.json(list.map((r) => r.toJSON()));
  })
);

// -------------------- Technician lists requests --------------------

/**
 * For technicians:
 *  - status=pending     -> requests still pending and not rejected by this technician
 *  - status=mine        -> requests assigned to this technician (accepted / in_progress / completed)
 *  - default            -> mine + open pending
 */
router.get(
  '/',
  authRequired,
  requireRole('technician'),
  asyncHandler(async (req, res) => {
    const tid = req.user.sub;
    const filter = (req.query.status || '').toString();

    let query;
    if (filter === 'pending') {
      query = { status: 'pending', rejectedBy: { $ne: tid } };
    } else if (filter === 'mine') {
      query = { technicianId: tid };
    } else if (REQUEST_STATUSES.includes(filter)) {
      query = { technicianId: tid, status: filter };
    } else {
      query = {
        $or: [
          { technicianId: tid },
          { status: 'pending', rejectedBy: { $ne: tid } }
        ]
      };
    }

    const list = await ServiceRequest.find(query)
      .sort({ createdAt: -1 })
      .limit(200);
    return res.json(list.map((r) => r.toJSON()));
  })
);

// -------------------- Single request fetch (auth check inside) --------------------

router.get(
  '/:id',
  authRequired,
  asyncHandler(async (req, res) => {
    if (!isValidId(req.params.id)) {
      return res.status(400).json({ error: 'Invalid request id.' });
    }
    const r = await ServiceRequest.findById(req.params.id);
    if (!r) return res.status(404).json({ error: 'Request not found.' });

    const isCustomer = r.customerId.toString() === req.user.sub;
    const isAssignedTech =
      r.technicianId && r.technicianId.toString() === req.user.sub;
    const isPendingTech = req.user.role === 'technician' && r.status === 'pending';

    if (!isCustomer && !isAssignedTech && !isPendingTech) {
      return res.status(403).json({ error: 'Not allowed.' });
    }
    return res.json(r.toJSON());
  })
);

// -------------------- Technician accepts a request --------------------

router.post(
  '/:id/accept',
  authRequired,
  requireRole('technician'),
  asyncHandler(async (req, res) => {
    if (!isValidId(req.params.id)) {
      return res.status(400).json({ error: 'Invalid request id.' });
    }
    const updated = await ServiceRequest.findOneAndUpdate(
      { _id: req.params.id, status: 'pending', technicianId: null },
      { $set: { status: 'accepted', technicianId: req.user.sub } },
      { new: true }
    );
    if (!updated) {
      return res.status(409).json({
        error: 'This request is no longer pending or has already been assigned.'
      });
    }

    // Notify customer.
    try {
      const customer = await User.findById(updated.customerId).select('fcmToken').lean();
      if (customer && customer.fcmToken) {
        notifyMany([customer.fcmToken], {
          title: 'Technician assigned',
          body: `Your request "${updated.title}" has been accepted.`,
          data: { requestId: updated._id.toString(), type: 'REQUEST_ACCEPTED' }
        }).catch((e) => console.error('[requests] notify error:', e.message));
      }
    } catch (e) {
      console.error('[requests] notify customer error:', e.message);
    }

    return res.json(updated.toJSON());
  })
);

// -------------------- Technician rejects a request (only for them) --------------------

router.post(
  '/:id/reject',
  authRequired,
  requireRole('technician'),
  asyncHandler(async (req, res) => {
    if (!isValidId(req.params.id)) {
      return res.status(400).json({ error: 'Invalid request id.' });
    }
    const updated = await ServiceRequest.findOneAndUpdate(
      { _id: req.params.id, status: 'pending' },
      { $addToSet: { rejectedBy: req.user.sub } },
      { new: true }
    );
    if (!updated) {
      return res.status(409).json({ error: 'Request is not pending.' });
    }
    return res.json(updated.toJSON());
  })
);

// -------------------- Technician updates status of an accepted request -------

router.patch(
  '/:id/status',
  authRequired,
  requireRole('technician'),
  asyncHandler(async (req, res) => {
    if (!isValidId(req.params.id)) {
      return res.status(400).json({ error: 'Invalid request id.' });
    }
    const { status } = req.body || {};
    // Allowed transitions: accepted -> in_progress -> completed.
    // Each target status has a specific set of legal predecessors.
    const TRANSITIONS = {
      in_progress: ['accepted'],
      completed: ['accepted', 'in_progress']
    };
    if (!Object.prototype.hasOwnProperty.call(TRANSITIONS, status)) {
      return res.status(400).json({
        error: `status must be one of: ${Object.keys(TRANSITIONS).join(', ')}.`
      });
    }
    const updated = await ServiceRequest.findOneAndUpdate(
      {
        _id: req.params.id,
        technicianId: req.user.sub,
        status: { $in: TRANSITIONS[status] }
      },
      { $set: { status } },
      { new: true }
    );
    if (!updated) {
      // Either the request isn't ours, doesn't exist, or its current status
      // doesn't allow the requested transition (e.g. cancelled, completed).
      return res.status(409).json({
        error: 'Cannot transition this request to the requested status.'
      });
    }

    // Notify customer.
    try {
      const customer = await User.findById(updated.customerId).select('fcmToken').lean();
      if (customer && customer.fcmToken) {
        const verb = status === 'completed' ? 'completed' : 'started';
        notifyMany([customer.fcmToken], {
          title: `Request ${verb}`,
          body: `"${updated.title}" was ${verb}.`,
          data: { requestId: updated._id.toString(), type: 'REQUEST_STATUS' }
        }).catch((e) => console.error('[requests] notify error:', e.message));
      }
    } catch (e) {
      console.error('[requests] notify customer error:', e.message);
    }
    return res.json(updated.toJSON());
  })
);

// -------------------- Customer cancels their own pending request -----------

router.post(
  '/:id/cancel',
  authRequired,
  requireRole('user'),
  asyncHandler(async (req, res) => {
    if (!isValidId(req.params.id)) {
      return res.status(400).json({ error: 'Invalid request id.' });
    }
    const updated = await ServiceRequest.findOneAndUpdate(
      {
        _id: req.params.id,
        customerId: req.user.sub,
        status: { $in: ['pending', 'accepted'] }
      },
      { $set: { status: 'cancelled' } },
      { new: true }
    );
    if (!updated) {
      return res
        .status(409)
        .json({ error: 'Cannot cancel this request in its current state.' });
    }
    return res.json(updated.toJSON());
  })
);

module.exports = router;
