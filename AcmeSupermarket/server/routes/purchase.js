const express = require('express');
const QRCode = require('qrcode');
const NodeRSA = require('node-rsa');

const router = express.Router();
const mw = require('../middlewares/user');

router.get('/', (req, res) => {
  const key = new NodeRSA({ b: 512 }).importKey(process.env.PRIVATE_KEY);
  console.log('Encrypting Hello world');

  const encrypted = key.encrypt('Hello world', 'base64');
  console.log(encrypted);

  const decrypted = key.decrypt(encrypted, 'utf8');
  console.log(decrypted);
  
});

router.post('/:username', mw.getUser, (req, res) => {
  console.log(req.body);
  const total = req.body.products.map((prod) => prod.price).reduce((acc, prod) => acc + prod);

  console.log(Math.round(total * 100) / 100);
});

module.exports = router;
