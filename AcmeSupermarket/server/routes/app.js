const express = require('express');
const NodeRSA = require('node-rsa');
const QRCode = require('qrcode');

const products = require('../seeds/product.json');

const router = express.Router();

const generateBuffer = (product) => {
  // Trim string if its length is higher than 35.
  const trimmedName = product.name.length > 35 ? product.name.slice(0, 35) : product.name;
  const buffer = Buffer.alloc(16 + 4 + 4 + 1 + trimmedName.length);

  const uuidMostSignificant = parseInt(product.uuid.replace(/-/g, '').slice(0, 16), 16);
  const uuidLeastSignificant = parseInt(product.uuid.replace(/-/g, '').slice(16), 16);

  const [euros, cents] = product.price.toString().split('.');

  //buffer.writeUInt32BE(0x16762042, 0);
  //buffer.writeUInt32BE(0x3f5d4dd5, 4);

  //buffer.writeUInt32BE(0x829a4519, 8);
  //buffer.writeUInt32BE(0x32901953, 12);

  buffer.writeBigUInt64BE(0x167620423f5d4dd5n, 0); // Write most significant bits of UUID.
  buffer.writeBigUInt64BE(0x829a451932901953n, 8); // Write least significant bits of UUID.

  buffer.writeUInt32BE(euros, 16); // Write euros.
  buffer.writeUInt32BE(cents, 20); // Write cents.

  buffer.writeUInt8(trimmedName.length, 24); // Write name length.

  buffer.write(trimmedName, 25); // Write name string.
  return buffer;
};

router.get('/', (req, res) => {
  res.sendFile('index.html', { root: 'views' });
});

router.get('/generate', (req, res) => {
  // Import supermarket private key.
  const key = new NodeRSA();
  key.importKey(process.env.PRIVATE_KEY, 'pkcs1-private');

  products.forEach((product) => {
    const buffer = generateBuffer(product);
    // Encrypt with supermarket private key.
    const encrypted = key.encryptPrivate(buffer, 'base64');
    console.log(encrypted);

    //const key2 = key.importKey(process.env.PUBLIC_KEY, 'pkcs8-public');
    //const decrypted = key2.decryptPublic(encrypted, 'utf8');
    //console.log(decrypted);

    console.log(buffer);
    
    QRCode.toFile(`public/images/${product.name}.png`, [{ data: buffer, mode: 'byte' }], {
      color: { light: '#0000' },
    }, (err) => { if (err) throw err; });
  });
  res.sendStatus(200);
});

module.exports = router;
