const express = require('express');
const Service = require('../models/Service');

const router = express.Router();

const asyncHandler = (fn) => (req, res, next) =>
  Promise.resolve(fn(req, res, next)).catch(next);

router.get(
  '/',
  asyncHandler(async (_req, res) => {
    const rows = await Service.find().sort({ legacyId: 1, _id: 1 });
    res.json(rows.map((r) => r.toJSON()));
  })
);

router.get(
  '/category/:category',
  asyncHandler(async (req, res) => {
    const rows = await Service.find({ category: req.params.category }).sort({ legacyId: 1, _id: 1 });
    res.json(rows.map((r) => r.toJSON()));
  })
);

router.get(
  '/:id',
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    const numericId = Number(id);
    const filter = Number.isFinite(numericId) ? { legacyId: numericId } : { _id: id };
    const row = await Service.findOne(filter);
    if (!row) return res.status(404).json({ error: 'Service not found.' });
    res.json(row.toJSON());
  })
);

module.exports = router;
