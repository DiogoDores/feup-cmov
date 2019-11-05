const express = require('express');
const QRCode = require('qrcode');
const NodeRSA = require('node-rsa');

const router = express.Router();
const mw = require('../middlewares/user');

router.get('/', (req, res) => {
  const key = new NodeRSA({ b: 512 }).importKey(process.env.PRIVATE_KEY);

  const dummyProduct = JSON.stringify({
    uuid: 'dummy-uuid',
    price: 2.99,
    name: 'Frozen Lasagne',
  });

  const encrypted = key.encrypt(dummyProduct, 'base64');
  console.log(encrypted);
  const decrypted = key.decrypt(encrypted, 'utf8');
  console.log(decrypted);

  QRCode.toDataURL(encrypted, { type: 'image/png' }, (err, url) => {
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
