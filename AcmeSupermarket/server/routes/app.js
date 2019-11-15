const express = require('express');
const NodeRSA = require('node-rsa');
const QRCode = require('qrcode');

const products = require('../seeds/product.json');

const router = express.Router();

router.get('/', (req, res) => {
  res.sendFile('index.html', { root: 'views' });
});

router.get('/generate', (req, res) => {
  // Import supermarket private key.
  const key = new NodeRSA();
  key.importKey(process.env.PRIVATE_KEY, 'pkcs8-private');

  products.forEach((product) => {
    // Encrypt with supermarket private key.
    const encrypted = key.encrypt(JSON.stringify(product), 'base64');
    const decrypted = key.decrypt(encrypted, 'utf8');

    QRCode.toFile(`public/images/${product.name}.png`, decrypted, {
      color: { light: '#0000' },
    }, (err) => { if (err) throw err; });
  });
  res.sendStatus(200);
});

module.exports = router;
