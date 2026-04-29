const express = require('express');
const bcrypt = require('bcryptjs');
const db = require('../db');
const { signToken, authRequired } = require('../auth');

const router = express.Router();

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

router.post('/signup', async (req, res) => {
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

  const existing = db.prepare('SELECT id FROM users WHERE email = ?').get(email);
  if (existing) {
    return res.status(409).json({ error: 'An account with that email already exists.' });
  }

  const password_hash = await bcrypt.hash(password, 10);
  const result = db
    .prepare('INSERT INTO users (email, name, password_hash) VALUES (?, ?, ?)')
    .run(email, name.trim(), password_hash);

  const user = { id: result.lastInsertRowid, email, name: name.trim() };
  return res.status(201).json({ token: signToken(user), user });
});

router.post('/login', async (req, res) => {
  const { email, password } = req.body || {};
  if (!email || !password) {
    return res.status(400).json({ error: 'Email and password are required.' });
  }
  const row = db.prepare('SELECT * FROM users WHERE email = ?').get(email);
  if (!row) return res.status(401).json({ error: 'Invalid email or password.' });

  const ok = await bcrypt.compare(password, row.password_hash);
  if (!ok) return res.status(401).json({ error: 'Invalid email or password.' });

  const user = { id: row.id, email: row.email, name: row.name };
  return res.json({ token: signToken(user), user });
});

router.get('/me', authRequired, (req, res) => {
  const row = db.prepare('SELECT id, email, name, created_at FROM users WHERE id = ?').get(req.user.sub);
  if (!row) return res.status(404).json({ error: 'User not found.' });
  return res.json({ user: row });
});

module.exports = router;
