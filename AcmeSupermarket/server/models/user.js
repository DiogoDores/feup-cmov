const mongoose = require('mongoose');
const uuid = require('uuid/v4');

const userSchema = new mongoose.Schema({
  uuid: {
    type: String,
    default: uuid,
    unique: true,
  },
  public_key: {
    type: String,
    default: '',
  },
  username: {
    type: String,
    required: true,
    unique: true,
  },
  name: {
    type: String,
    required: true,
    unique: false,
  },
  discount: {
    type: Number,
    required: false,
    unique: false,
  },
  expense: {
    type: Number,
    required: false,
    unique: false,
  },
  vouchers: [{
    type: String,
    required: false,
    unique: false,
  }],
  receipts: [{
    type: mongoose.Schema.Types.Mixed,
    required: false,
    unique: false,
  }],
});

module.exports = mongoose.model('User', userSchema);
