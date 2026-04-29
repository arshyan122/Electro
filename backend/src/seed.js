require('dotenv').config();
const { connect, mongoose } = require('./db');
const Product = require('./models/Product');
const Service = require('./models/Service');

const products = [
  {
    legacyId: 1,
    title: 'WD 2TB Elements Portable External Hard Drive',
    price: 64.0,
    description: 'USB 3.0 and USB 2.0 compatibility. Fast data transfers. Improve PC performance.',
    category: 'electronics',
    image: 'https://fakestoreapi.com/img/61IBBVJvSDL._AC_SY879_.jpg',
    rating: { rate: 3.3, count: 203 }
  },
  {
    legacyId: 2,
    title: 'SanDisk SSD PLUS 1TB Internal SSD',
    price: 109.0,
    description: 'Easy upgrade for faster boot up, shutdown, application load and response.',
    category: 'electronics',
    image: 'https://fakestoreapi.com/img/61U7T1koQqL._AC_SX679_.jpg',
    rating: { rate: 2.9, count: 470 }
  },
  {
    legacyId: 3,
    title: 'Silicon Power 256GB SSD 3D NAND A55 SLC Cache',
    price: 109.0,
    description: '3D NAND flash for high transfer speeds and superior endurance.',
    category: 'electronics',
    image: 'https://fakestoreapi.com/img/71kWymZ+c+L._AC_SX679_.jpg',
    rating: { rate: 4.8, count: 319 }
  },
  {
    legacyId: 4,
    title: 'WD 4TB Gaming Drive Works with Playstation 4',
    price: 114.0,
    description: 'Expand your PS4 gaming experience, Play anywhere. Fast and easy, setup.',
    category: 'electronics',
    image: 'https://fakestoreapi.com/img/61mtL65D4cL._AC_SX679_.jpg',
    rating: { rate: 4.8, count: 400 }
  },
  {
    legacyId: 5,
    title: 'Acer SB220Q bi 21.5 inches Full HD IPS Monitor',
    price: 599.0,
    description: '21. 5 inches Full HD (1920 x 1080) widescreen IPS display.',
    category: 'electronics',
    image: 'https://fakestoreapi.com/img/81QpkIctqPL._AC_SX679_.jpg',
    rating: { rate: 2.9, count: 250 }
  },
  {
    legacyId: 6,
    title: 'Samsung 49-Inch CHG90 144Hz Curved Gaming Monitor',
    price: 999.99,
    description: '49 inch super ultrawide 32:9 curved gaming monitor with dual QHD resolution.',
    category: 'electronics',
    image: 'https://fakestoreapi.com/img/81Zt42ioCgL._AC_SX679_.jpg',
    rating: { rate: 2.2, count: 140 }
  }
];

const services = [
  {
    legacyId: 1,
    name: 'Electrical Wiring',
    description: 'Full home electrical wiring installation and repair by certified electricians.',
    price: 1500.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/1014/1014944.png',
    category: 'wiring'
  },
  {
    legacyId: 2,
    name: 'AC Installation & Repair',
    description: 'Split / window AC installation, gas top-up, deep cleaning and breakdown repair.',
    price: 999.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/2956/2956744.png',
    category: 'ac'
  },
  {
    legacyId: 3,
    name: 'Ceiling Fan Service',
    description: 'Installation, regulator replacement, oiling and noise diagnosis for any ceiling fan.',
    price: 299.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/2843/2843281.png',
    category: 'fan'
  },
  {
    legacyId: 4,
    name: 'Refrigerator Repair',
    description: 'Cooling diagnosis, compressor service, gas refill and door-seal replacement.',
    price: 599.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/3082/3082031.png',
    category: 'refrigerator'
  },
  {
    legacyId: 5,
    name: 'Chimney Cleaning',
    description: 'Deep degreasing of kitchen chimney filters, motor service and duct check.',
    price: 799.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/2729/2729007.png',
    category: 'chimney'
  },
  {
    legacyId: 6,
    name: 'Geyser Installation',
    description: 'Wall-mounting, plumbing connection and safety check for instant or storage geysers.',
    price: 499.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/3658/3658829.png',
    category: 'geyser'
  },
  {
    legacyId: 7,
    name: 'Inverter & Battery Service',
    description: 'Battery health check, water top-up, inverter wiring and load balancing.',
    price: 399.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/3296/3296861.png',
    category: 'inverter'
  },
  {
    legacyId: 8,
    name: 'Switchboard Repair',
    description: 'MCB replacement, socket / switch fitting and earthing check.',
    price: 249.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/2911/2911260.png',
    category: 'switchboard'
  }
];

(async () => {
  try {
    await connect();

    // Idempotent upsert keyed on legacyId so re-running the seeder is safe
    // and doesn't duplicate documents on rerun.
    const productOps = products.map((p) => ({
      updateOne: { filter: { legacyId: p.legacyId }, update: { $set: p }, upsert: true }
    }));
    const serviceOps = services.map((s) => ({
      updateOne: { filter: { legacyId: s.legacyId }, update: { $set: s }, upsert: true }
    }));

    const productRes = await Product.bulkWrite(productOps);
    const serviceRes = await Service.bulkWrite(serviceOps);

    console.log(
      `Seeded products (upserted: ${productRes.upsertedCount}, modified: ${productRes.modifiedCount})`
    );
    console.log(
      `Seeded services (upserted: ${serviceRes.upsertedCount}, modified: ${serviceRes.modifiedCount})`
    );
  } catch (err) {
    console.error('Seed failed:', err);
    process.exitCode = 1;
  } finally {
    await mongoose.disconnect();
  }
})();
