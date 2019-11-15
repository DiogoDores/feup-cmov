require('dotenv').config();
const NodeRSA = require('node-rsa');

const express = require('express');
const mongoose = require('mongoose');
const appRouter = require('./routes/app');
const usersRouter = require('./routes/users');
const productsRouter = require('./routes/products');

const app = express();
app.use(express.json());

mongoose.connect(process.env.DATABASE_URL, {
  useNewUrlParser: true, useUnifiedTopology: true, useCreateIndex: true,
});

const db = mongoose.connection;
db.on('error', (err) => console.log(err));

db.once('open', () => {
  // db.collection('products').find({}).toArray((err, res) => console.log(res));
  console.log('Connected to database!');
});

app.use('/', appRouter);
app.use('/users', usersRouter);
app.use('/products', productsRouter);

// Generate new 512 bit-length key.
const key = new NodeRSA({ b: 512 });
const kp = key.generateKeyPair();

process.env.PUBLIC_KEY = kp.exportKey('pkcs8-public');
process.env.PRIVATE_KEY = kp.exportKey('pkcs8-private');

app.use(express.static('views'));
app.use(express.static('public'));

app.listen(process.env.PORT, () => {
  console.log(`Example app listening on port ${process.env.PORT}!`);
});
