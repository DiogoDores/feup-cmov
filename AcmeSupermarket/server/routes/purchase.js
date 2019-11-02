const express = require('express');

const router = express.Router();
const User = require('../models/user');
const mw = require('../middlewares/user');

router.post('/:username', mw.getUser, (req, res) => {
  console.log(req.body);
  const total = req.body.products.map((prod) => prod.price).reduce((acc, prod) => acc + prod);

  console.log(Math.round(total * 100) / 100);
});

module.exports = router;
