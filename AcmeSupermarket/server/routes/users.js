const express = require('express');
const uuid = require('uuid/v4');
const NodeRSA = require('node-rsa');

const router = express.Router();
const User = require('../models/user');
const Receipt = require('../models/receipt');

const mw = require('../middlewares/user');

const VOUCHER_CAP = 100;

// Get all users.
router.get('/', async (req, res) => {
  try {
    const users = await User.find();
    res.json(users);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Get one user by username.
router.get('/:username', mw.getUser, (req, res) => {
  res.json(res.user);
});

// Create one user.
router.post('/', async (req, res) => {
  const user = new User({
    public_key: req.body.public_key,
    name: req.body.name,
    username: req.body.username,
    discount: 0.00,
    expense: 0.00,
    vouchers: [],
    receipts: [],
  });

  try {
    const newUser = await user.save();
    res.status(201).json({ uuid: newUser.uuid, sm_public_key: process.env.PUBLIC_KEY });
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// Get receipts from UUID.
router.post('/:username/history', mw.getUser, async (req, res) => {
  const key = new NodeRSA({ b: 512 });
  const formatted = `-----BEGIN PUBLIC KEY-----\n${res.user.public_key}-----END PUBLIC KEY-----`;
  key.importKey(formatted, 'pkcs8-public-pem');
  key.setOptions({ signingScheme: 'pkcs1-sha256' });

  if (key.verify(req.body.uuid, req.body.uuid_signed, 'utf8', 'base64')) {
    res.send(res.user);
  } else {
    res.sendStatus(403);
  }
});

// Attempt to login.
router.get('/login/:username', mw.getUser, async (req, res) => {
  res.status(200).json({ uuid: res.user.uuid, sm_public_key: process.env.PUBLIC_KEY });
});

// Update one user.
router.patch('/:username', mw.getUser, async (req, res) => {
  if (req.body.public_key != null) {
    res.user.public_key = req.body.public_key;
  }

  try {
    const updatedUser = await res.user.save();
    res.json(updatedUser);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// Register purchase.
router.post('/buy/:username', mw.getUser, async (req, res) => {
  console.log(req.body);

  const subtotal = req.body.products.map((prod) => prod.price).reduce((acc, prod) => acc + prod);
  let total = subtotal;

  // Spend accumulated discount if user requested.
  if (req.body.apply_discount) {
    total -= res.user.discount;

    // If the accumulated discount is higher than the total, store the remaining cash.
    if (total < 0) {
      res.user.discount = Math.abs(total);
      total = 0;
    } else {
      res.user.discount = 0;
    }
  }

  // Apply 15% and add it to accumulated discount if there's a voucher.
  if ('voucher' in req.body) {
    if (res.user.vouchers.includes(req.body.voucher)) {
      res.user.discount += (0.15 * subtotal);
      res.user.vouchers = res.user.vouchers.filter((v) => v !== req.body.voucher);
    } else {
      console.log(`Voucher ${req.body.voucher} is invalid.`);
    }
  }

  // Save new expense.
  res.user.expense += total;

  // Check if total expense is multiple of 100, and if so generate a new voucher.
  if (res.user.expense >= VOUCHER_CAP) {
    res.user.expense -= VOUCHER_CAP;
    res.user.vouchers.push(uuid());
  }

  // Generate a new receipt.
  const receipt = new Receipt({
    products: req.body.products,
    subtotal,
    total,
    voucher: ('voucher' in req.body && res.user.vouchers.includes(req.body.voucher)) ? req.body.voucher : null,
    milestone: Math.round((VOUCHER_CAP - res.user.expense) * 100) / 100,
  });

  res.user.receipts.push(receipt);

  // Round expense and discount to two decimal places.
  res.user.expense = Math.round(res.user.expense * 100) / 100;
  res.user.discount = Math.round(res.user.discount * 100) / 100;

  await res.user.save();
  res.json(receipt);
});

// Delete one user.
router.delete('/:username', mw.getUser, async (req, res) => {
  try {
    await res.user.remove();
    res.json({ message: 'Deleted this user.' });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

module.exports = router;
