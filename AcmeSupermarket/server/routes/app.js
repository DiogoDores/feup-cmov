const express = require('express');
const QRCode = require('qrcode');
const path = require('path');
const products = require('../seeds/product.json');

const router = express.Router();

router.get('/', (req, res) => {
  res.sendFile('index.html', { root: 'views' });
});

router.get('/generate', (req, res) => {
  products.forEach((product) => {
    QRCode.toFile(`public/images/${product.name}.png`, JSON.stringify(product), {
      color: { light: '#0000' },
    }, (err) => { if (err) console.log(err); });
  });
  res.sendStatus(200);
});

module.exports = router;
