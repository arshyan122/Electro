const express = require('express');
const db = require('../db');

const router = express.Router();

router.get('/', (req, res) => {
  const rows = db.prepare('SELECT * FROM services ORDER BY id').all();
  res.json(rows);
});

router.get('/category/:category', (req, res) => {
  const rows = db
    .prepare('SELECT * FROM services WHERE category = ? ORDER BY id')
    .all(req.params.category);
  res.json(rows);
});

router.get('/:id', (req, res) => {
  const row = db.prepare('SELECT * FROM services WHERE id = ?').get(req.params.id);
  if (!row) return res.status(404).json({ error: 'Service not found.' });
  res.json(row);
});

module.exports = router;
