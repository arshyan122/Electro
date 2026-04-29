const express = require('express');
const Product = require('../models/Product');

const router = express.Router();

const asyncHandler = (fn) => (req, res, next) =>
  Promise.resolve(fn(req, res, next)).catch(next);

router.get(
  '/',
  asyncHandler(async (_req, res) => {
    const rows = await Product.find().sort({ legacyId: 1, _id: 1 });
    res.json(rows.map((r) => r.toJSON()));
  })
);

router.get(
  '/category/:category',
  asyncHandler(async (req, res) => {
    const rows = await Product.find({ category: req.params.category }).sort({ legacyId: 1, _id: 1 });
    res.json(rows.map((r) => r.toJSON()));
  })
);

router.get(
  '/:id',
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    const numericId = Number(id);
    const filter = Number.isFinite(numericId) ? { legacyId: numericId } : { _id: id };
    const row = await Product.findOne(filter);
    if (!row) return res.status(404).json({ error: 'Product not found.' });
    res.json(row.toJSON());
  })
);

module.exports = router;
