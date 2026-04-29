require('dotenv').config();
const db = require('./db');

const products = [
  {
    title: 'WD 2TB Elements Portable External Hard Drive',
    price: 64.0,
    description: 'USB 3.0 and USB 2.0 compatibility. Fast data transfers. Improve PC performance.',
    category: 'electronics',
    image: 'https://fakestoreapi.com/img/61IBBVJvSDL._AC_SY879_.jpg',
    rating_rate: 3.3,
    rating_count: 203
  },
  {
    title: 'SanDisk SSD PLUS 1TB Internal SSD',
    price: 109.0,
    description: 'Easy upgrade for faster boot up, shutdown, application load and response.',
    category: 'electronics',
    image: 'https://fakestoreapi.com/img/61U7T1koQqL._AC_SX679_.jpg',
    rating_rate: 2.9,
    rating_count: 470
  },
  {
    title: 'Silicon Power 256GB SSD 3D NAND A55 SLC Cache',
    price: 109.0,
    description: '3D NAND flash for high transfer speeds and superior endurance.',
    category: 'electronics',
    image: 'https://fakestoreapi.com/img/71kWymZ+c+L._AC_SX679_.jpg',
    rating_rate: 4.8,
    rating_count: 319
  },
  {
    title: 'WD 4TB Gaming Drive Works with Playstation 4',
    price: 114.0,
    description: 'Expand your PS4 gaming experience, Play anywhere. Fast and easy, setup.',
    category: 'electronics',
    image: 'https://fakestoreapi.com/img/61mtL65D4cL._AC_SX679_.jpg',
    rating_rate: 4.8,
    rating_count: 400
  },
  {
    title: 'Acer SB220Q bi 21.5 inches Full HD IPS Monitor',
    price: 599.0,
    description: '21. 5 inches Full HD (1920 x 1080) widescreen IPS display.',
    category: 'electronics',
    image: 'https://fakestoreapi.com/img/81QpkIctqPL._AC_SX679_.jpg',
    rating_rate: 2.9,
    rating_count: 250
  },
  {
    title: 'Samsung 49-Inch CHG90 144Hz Curved Gaming Monitor',
    price: 999.99,
    description: '49 inch super ultrawide 32:9 curved gaming monitor with dual QHD resolution.',
    category: 'electronics',
    image: 'https://fakestoreapi.com/img/81Zt42ioCgL._AC_SX679_.jpg',
    rating_rate: 2.2,
    rating_count: 140
  }
];

const services = [
  {
    name: 'Electrical Wiring',
    description: 'Full home electrical wiring installation and repair by certified electricians.',
    price: 1500.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/1014/1014944.png',
    category: 'wiring'
  },
  {
    name: 'AC Installation & Repair',
    description: 'Split / window AC installation, gas top-up, deep cleaning and breakdown repair.',
    price: 999.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/2956/2956744.png',
    category: 'ac'
  },
  {
    name: 'Ceiling Fan Service',
    description: 'Installation, regulator replacement, oiling and noise diagnosis for any ceiling fan.',
    price: 299.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/2843/2843281.png',
    category: 'fan'
  },
  {
    name: 'Refrigerator Repair',
    description: 'Cooling diagnosis, compressor service, gas refill and door-seal replacement.',
    price: 599.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/3082/3082031.png',
    category: 'refrigerator'
  },
  {
    name: 'Chimney Cleaning',
    description: 'Deep degreasing of kitchen chimney filters, motor service and duct check.',
    price: 799.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/2729/2729007.png',
    category: 'chimney'
  },
  {
    name: 'Geyser Installation',
    description: 'Wall-mounting, plumbing connection and safety check for instant or storage geysers.',
    price: 499.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/3658/3658829.png',
    category: 'geyser'
  },
  {
    name: 'Inverter & Battery Service',
    description: 'Battery health check, water top-up, inverter wiring and load balancing.',
    price: 399.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/3296/3296861.png',
    category: 'inverter'
  },
  {
    name: 'Switchboard Repair',
    description: 'MCB replacement, socket / switch fitting and earthing check.',
    price: 249.0,
    icon: 'https://cdn-icons-png.flaticon.com/512/2911/2911260.png',
    category: 'switchboard'
  }
];

const tx = db.transaction(() => {
  db.prepare('DELETE FROM products').run();
  db.prepare('DELETE FROM services').run();

  const insertProduct = db.prepare(`
    INSERT INTO products (title, price, description, category, image, rating_rate, rating_count)
    VALUES (@title, @price, @description, @category, @image, @rating_rate, @rating_count)
  `);
  for (const p of products) insertProduct.run(p);

  const insertService = db.prepare(`
    INSERT INTO services (name, description, price, icon, category)
    VALUES (@name, @description, @price, @icon, @category)
  `);
  for (const s of services) insertService.run(s);
});

tx();

console.log(`Seeded ${products.length} products and ${services.length} services.`);
