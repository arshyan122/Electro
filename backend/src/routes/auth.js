const express = require('express');
const bcrypt = require('bcryptjs');
const db = require('../db');
const { signToken, authRequired } = require('../auth');

const router = express.Router();

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/**
 * Wraps an async Express handler so rejected promises are forwarded to the
 * Express error middleware instead of becoming unhandled promise rejections
 * (which crash the process on Node 18+).
 */
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

    const password_hash = await bcrypt.hash(password, 10);

    let result;
    try {
      result = db
        .prepare('INSERT INTO users (email, name, password_hash) VALUES (?, ?, ?)')
        .run(email, name.trim(), password_hash);
    } catch (err) {
      // Rely on the UNIQUE constraint to settle races between concurrent signups
      // with the same email rather than a check-then-insert pattern.
      if (err && err.code === 'SQLITE_CONSTRAINT_UNIQUE') {
        return res.status(409).json({ error: 'An account with that email already exists.' });
      }
      throw err;
    }

    const user = { id: result.lastInsertRowid, email, name: name.trim() };
    return res.status(201).json({ token: signToken(user), user });
  })
);

router.post(
  '/login',
  asyncHandler(async (req, res) => {
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
  })
);

router.get('/me', authRequired, (req, res) => {
  const row = db.prepare('SELECT id, email, name, created_at FROM users WHERE id = ?').get(req.user.sub);
  if (!row) return res.status(404).json({ error: 'User not found.' });
  return res.json({ user: row });
});

module.exports = router;
