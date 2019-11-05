const express = require('express');

const router = express.Router();
const User = require('../models/user');
const mw = require('../middlewares/user');

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
  });

  try {
    const newUser = await user.save();
    res.status(201).json({ uuid: newUser.uuid, sm_public_key: process.env.PUBLIC_KEY });
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// Attempt to login.
router.get('/login/:username', mw.getUser, async (req, res) => {
  res.status(200).json({ uuid: res.user.uuid, sm_public_key: 'SPK' });
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
