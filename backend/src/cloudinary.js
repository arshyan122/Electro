/**
 * Minimal Cloudinary helper. Provides a signature endpoint for the technician
 * Android app to perform signed uploads directly from the client. Avoids
 * proxying file bytes through our backend.
 */
const crypto = require('crypto');

function getConfig() {
  return {
    cloudName: process.env.CLOUDINARY_CLOUD_NAME || '',
    apiKey: process.env.CLOUDINARY_API_KEY || '',
    apiSecret: process.env.CLOUDINARY_API_SECRET || '',
    uploadPreset: process.env.CLOUDINARY_UPLOAD_PRESET || ''
  };
}

function isConfigured() {
  const c = getConfig();
  return Boolean(c.cloudName && c.apiKey && c.apiSecret);
}

/**
 * Builds a Cloudinary upload signature.
 * params: { folder, public_id, ... } — extra parameters to include in the signature.
 */
function signUpload(params = {}) {
  const cfg = getConfig();
  if (!isConfigured()) {
    throw new Error('Cloudinary is not configured.');
  }
  const timestamp = Math.floor(Date.now() / 1000);
  const toSign = { ...params, timestamp };

  // Cloudinary signing rules: alphabetical key order, key=value joined with &,
  // then SHA-1 with api_secret appended.
  const sortedKeys = Object.keys(toSign).sort();
  const stringToSign = sortedKeys
    .map((k) => `${k}=${toSign[k]}`)
    .join('&');

  const signature = crypto
    .createHash('sha1')
    .update(stringToSign + cfg.apiSecret)
    .digest('hex');

  return {
    timestamp,
    signature,
    apiKey: cfg.apiKey,
    cloudName: cfg.cloudName,
    uploadPreset: cfg.uploadPreset
  };
}

module.exports = { isConfigured, signUpload, getConfig };
