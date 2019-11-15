const mongoose = require('mongoose');

const receiptSchema = new mongoose.Schema({
  products: [{
    type: mongoose.Schema.Types.Mixed,
    required: true,
  }],
  subtotal: {
    type: Number,
    required: true,
  },
  total: {
    type: Number,
    required: true,
  },
  voucher: {
    type: String,
    required: false,
  },
  milestone: {
    type: Number,
    required: true,
  },
});

module.exports = mongoose.model('Receipt', receiptSchema);
