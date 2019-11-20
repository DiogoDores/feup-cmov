const express = require('express');
const NodeRSA = require('node-rsa');
const QRCode = require('qrcode');

const products = require('../seeds/product.json');

const router = express.Router();

const generateBuffer = (product) => {
  // Trim string if its length is higher than 35.
  const trimmedName = product.name.length > 35 ? product.name.slice(0, 35) : product.name;
  const uuid = product.uuid.replace(/-/g, '');
  const buffer = Buffer.alloc(4 + 16 + 4 + 4 + 1 + trimmedName.length);

  buffer.writeUInt32BE(0x41636D65, 0); // Write ACME tag.
  buffer.writeUInt32BE(parseInt(uuid.slice(0, 8), 16), 4); // Write MSB of UUID.
  buffer.writeUInt32BE(parseInt(uuid.slice(8, 16), 16), 8);

  buffer.writeUInt32BE(parseInt(uuid.slice(16, 24), 16), 12); // Write LSB of UUID.
  buffer.writeUInt32BE(parseInt(uuid.slice(24, 32), 16), 16);

  const [euros, cents] = product.price.toString().split('.');
  buffer.writeUInt32BE(euros, 20); // Write euros.
  buffer.writeUInt32BE(cents, 24); // Write cents.

  buffer.writeUInt8(trimmedName.length, 28); // Write name length.
  buffer.write(trimmedName, 29); // Write name string.
  return buffer;
};

router.get('/', (req, res) => {
  res.sendFile('index.html', { root: 'views' });
});

router.get('/generate', (req, res) => {
  // Import supermarket private key.
  const key = new NodeRSA();
  key.importKey(process.env.PRIVATE_KEY, 'pkcs1-private');
  key.setOptions({ encryptionScheme: 'pkcs1' });

  products.forEach((product) => {
    const buffer = generateBuffer(product);

    // Encrypt with supermarket private key.
    const encrypted = key.encryptPrivate(buffer, 'hex');

    QRCode.toFile(`public/images/${product.name}.png`, encrypted, {
      color: { light: '#0000' },
    }, (err) => { if (err) throw err; });
  });

  res.sendStatus(200);
});

module.exports = router;
