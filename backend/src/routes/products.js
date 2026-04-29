const express = require('express');
const db = require('../db');

const router = express.Router();

function rowToProduct(row) {
  return {
    id: row.id,
    title: row.title,
    price: row.price,
    description: row.description,
    category: row.category,
    image: row.image,
    rating: { rate: row.rating_rate, count: row.rating_count }
  };
}

router.get('/', (req, res) => {
  const rows = db.prepare('SELECT * FROM products ORDER BY id').all();
  res.json(rows.map(rowToProduct));
});

router.get('/category/:category', (req, res) => {
  const rows = db
    .prepare('SELECT * FROM products WHERE category = ? ORDER BY id')
    .all(req.params.category);
  res.json(rows.map(rowToProduct));
});

router.get('/:id', (req, res) => {
  const row = db.prepare('SELECT * FROM products WHERE id = ?').get(req.params.id);
  if (!row) return res.status(404).json({ error: 'Product not found.' });
  res.json(rowToProduct(row));
});

module.exports = router;
