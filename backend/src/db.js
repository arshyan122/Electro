const mongoose = require('mongoose');

/**
 * Connects mongoose to the MongoDB instance configured via $MONGODB_URI.
 * Resolves the existing connection on subsequent calls so routes can `await`
 * this lazily without juggling startup ordering.
 */
let connecting = null;

async function connect() {
  if (mongoose.connection.readyState === 1) return mongoose.connection;
  if (connecting) return connecting;

  const uri = process.env.MONGODB_URI;
  if (!uri) {
    throw new Error('MONGODB_URI is not set. Copy backend/.env.example to backend/.env and fill it in.');
  }

  mongoose.set('strictQuery', true);
  connecting = mongoose
    .connect(uri, {
      serverSelectionTimeoutMS: 10_000
    })
    .then((m) => {
      console.log(`[db] connected to ${m.connection.name}`);
      return m.connection;
    })
    .catch((err) => {
      connecting = null;
      throw err;
    });

  return connecting;
}

module.exports = { connect, mongoose };
