/**
 * Thin wrapper around the Firebase Admin SDK for FCM push notifications.
 *
 * The Admin credentials live in the FIREBASE_ADMIN_SDK_JSON env var as the raw
 * JSON of a service-account key. If the variable is not set (e.g. in local
 * development without push notifications), notifyMany is a no-op so the rest of
 * the backend continues to work.
 */
let admin = null;
let initialized = false;

function init() {
  if (initialized) return admin;
  initialized = true;

  const raw = process.env.FIREBASE_ADMIN_SDK_JSON;
  if (!raw) {
    console.warn(
      '[fcm] FIREBASE_ADMIN_SDK_JSON not set; push notifications disabled.'
    );
    return null;
  }
  try {
    const creds = JSON.parse(raw);
    // eslint-disable-next-line global-require
    admin = require('firebase-admin');
    if (!admin.apps.length) {
      admin.initializeApp({
        credential: admin.credential.cert(creds)
      });
    }
    console.log('[fcm] initialized as project', creds.project_id);
    return admin;
  } catch (err) {
    console.error('[fcm] failed to initialize:', err.message);
    admin = null;
    return null;
  }
}

async function notifyMany(tokens, { title, body, data }) {
  const cleanTokens = (tokens || []).filter((t) => typeof t === 'string' && t.length > 0);
  if (cleanTokens.length === 0) return { sent: 0, failed: 0 };

  const sdk = init();
  if (!sdk) return { sent: 0, failed: 0, skipped: true };

  try {
    const response = await sdk.messaging().sendEachForMulticast({
      tokens: cleanTokens,
      notification: { title, body },
      data: Object.fromEntries(
        Object.entries(data || {}).map(([k, v]) => [k, String(v)])
      )
    });
    return {
      sent: response.successCount,
      failed: response.failureCount
    };
  } catch (err) {
    console.error('[fcm] notifyMany error:', err.message);
    return { sent: 0, failed: cleanTokens.length, error: err.message };
  }
}

module.exports = { init, notifyMany };
