const express = require('express');
const QRCode = require('qrcode');
const NodeRSA = require('node-rsa');

const Product = require('../models/product');

const router = express.Router();
const mw = require('../middlewares/user');

// Get all products.
router.get('/', async (req, res) => {
  try {
    const products = await Product.find();
    res.json(products);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

router.get('/dummy', (req, res) => {
  const key = new NodeRSA();
  console.log(process.env.PRIVATE_KEY);
  key.importKey(process.env.PRIVATE_KEY, 'pkcs8-private');
  key.setOptions({ encryptionScheme: 'pkcs1' });

  const dummyProduct = JSON.stringify({
    name: 'Frozen Lasagne',
    uuid: 'dummy-uuid',
    price: 2.99,
  });

  const encrypted = key.encrypt(dummyProduct, 'base64');
  console.log(encrypted);
  const decrypted = key.decrypt(encrypted, 'utf8');
  console.log(decrypted);

  QRCode.toDataURL(dummyProduct, { type: 'image/png' }, (err, url) => {
    const img = Buffer.from(url.split(',')[1], 'base64');
    res.writeHead(200, { 'Content-Type': 'image/png', 'Content-Length': img.length });
    res.end(img);
  });
});

router.post('/:username', mw.getUser, (req, res) => {
  console.log(req.body);
  const total = req.body.products.map((prod) => prod.price).reduce((acc, prod) => acc + prod);

  console.log(Math.round(total * 100) / 100);
});

module.exports = router;
