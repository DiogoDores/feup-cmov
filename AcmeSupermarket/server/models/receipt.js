const mongoose = require('mongoose');

const receiptSchema = new mongoose.Schema({
});

module.exports = mongoose.model('Receipt', receiptSchema);
