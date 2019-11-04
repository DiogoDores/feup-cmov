require('dotenv').config();
const NodeRSA = require('node-rsa');

const express = require('express');
const mongoose = require('mongoose');
const usersRouter = require('./routes/users');
const purchaseRouter = require('./routes/purchase');

const app = express();
app.use(express.json());

mongoose.connect(process.env.DATABASE_URL, {
  useNewUrlParser: true, useUnifiedTopology: true, useCreateIndex: true,
});

const db = mongoose.connection;
db.on('error', (err) => console.log(err));
db.once('open', () => console.log('Connected to database!'));

app.use('/users', usersRouter);
app.use('/purchase', purchaseRouter);

// Generate new 512 bit-length key.
const key = new NodeRSA({ b: 512 });
const kp = key.generateKeyPair();

process.env.PUBLIC_KEY = kp.exportKey('public');
process.env.PRIVATE_KEY = kp.exportKey('private');

app.listen(process.env.PORT, () => {
  console.log(`Example app listening on port ${process.env.PORT}!`);
});
