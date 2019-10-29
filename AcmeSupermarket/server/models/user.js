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
  password: {
    type: String,
    required: true,
    unique: false,
  },
  name: {
    type: String,
    required: true,
    unique: false,
  },
});

module.exports = mongoose.model('User', userSchema);
