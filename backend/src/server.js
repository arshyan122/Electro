require('dotenv').config();
const express = require('express');
const cors = require('cors');

const { connect } = require('./db');
const { init: initFcm } = require('./fcm');
const authRoutes = require('./routes/auth');
const productRoutes = require('./routes/products');
const serviceRoutes = require('./routes/services');
const technicianRoutes = require('./routes/technician');
const requestRoutes = require('./routes/requests');

const app = express();
app.use(cors());
app.use(express.json({ limit: '1mb' }));

app.get('/', (_req, res) => {
  res.json({
    name: 'electro-backend',
    endpoints: [
      'POST /auth/signup',
      'POST /auth/login',
      'GET  /auth/me  (Bearer)',
      'POST /auth/fcm-token  (Bearer)',
      'GET  /products',
      'GET  /products/:id',
      'GET  /products/category/:category',
      'GET  /services',
      'GET  /services/:id',
      'GET  /services/category/:category',
      'POST /technician/register',
      'POST /technician/login',
      'GET  /technician/me  (Bearer technician)',
      'PATCH /technician/me  (Bearer technician)',
      'GET  /technician/services  (Bearer technician)',
      'POST /technician/services  (Bearer technician)',
      'PATCH /technician/services/:id  (Bearer technician)',
      'DELETE /technician/services/:id  (Bearer technician)',
      'GET  /technician/dashboard  (Bearer technician)',
      'POST /technician/uploads/sign  (Bearer)',
      'POST /requests  (Bearer user)',
      'GET  /requests/mine  (Bearer user)',
      'GET  /requests  (Bearer technician)',
      'GET  /requests/:id  (Bearer)',
      'POST /requests/:id/accept  (Bearer technician)',
      'POST /requests/:id/reject  (Bearer technician)',
      'PATCH /requests/:id/status  (Bearer technician)',
      'POST /requests/:id/cancel  (Bearer user)'
    ]
  });
});

app.get('/health', (_req, res) => res.json({ ok: true }));

app.use('/auth', authRoutes);
app.use('/products', productRoutes);
app.use('/services', serviceRoutes);
app.use('/technician', technicianRoutes);
app.use('/requests', requestRoutes);

app.use((_req, res) => res.status(404).json({ error: 'Not found.' }));

app.use((err, _req, res, _next) => {
  console.error(err);
  res.status(500).json({ error: 'Internal server error.' });
});

const PORT = Number(process.env.PORT) || 8080;

(async () => {
  try {
    await connect();
  } catch (err) {
    console.error('[startup] failed to connect to MongoDB:', err.message);
    process.exit(1);
  }
  // Best-effort FCM init at boot so credential errors surface early.
  initFcm();
  app.listen(PORT, '0.0.0.0', () => {
    console.log(`electro-backend listening on :${PORT}`);
  });
})();
