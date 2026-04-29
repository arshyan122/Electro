require('dotenv').config();
const express = require('express');
const cors = require('cors');

const authRoutes = require('./routes/auth');
const productRoutes = require('./routes/products');
const serviceRoutes = require('./routes/services');

const app = express();
app.use(cors());
app.use(express.json({ limit: '1mb' }));

app.get('/', (req, res) => {
  res.json({
    name: 'electro-backend',
    endpoints: [
      'POST /auth/signup',
      'POST /auth/login',
      'GET  /auth/me  (Bearer)',
      'GET  /products',
      'GET  /products/:id',
      'GET  /products/category/:category',
      'GET  /services',
      'GET  /services/:id',
      'GET  /services/category/:category'
    ]
  });
});

app.get('/health', (req, res) => res.json({ ok: true }));

app.use('/auth', authRoutes);
app.use('/products', productRoutes);
app.use('/services', serviceRoutes);

app.use((req, res) => res.status(404).json({ error: 'Not found.' }));

app.use((err, req, res, _next) => {
  console.error(err);
  res.status(500).json({ error: 'Internal server error.' });
});

const PORT = Number(process.env.PORT) || 8080;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`electro-backend listening on :${PORT}`);
});
