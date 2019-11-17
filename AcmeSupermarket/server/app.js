require('dotenv').config();
const NodeRSA = require('node-rsa');

const express = require('express');
const mongoose = require('mongoose');
const appRouter = require('./routes/app');
const usersRouter = require('./routes/users');

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

// Generate new 512 bit-length key.
const key = new NodeRSA({ b: 512 });
key.setOptions({ encryptionScheme: 'pkcs1_oaep' });
const kp = key.generateKeyPair();

// Generate the supermarket keypair.
process.env.PUBLIC_KEY = kp.exportKey('pkcs8-public-pem');
console.log(process.env.PUBLIC_KEY);

process.env.PRIVATE_KEY = kp.exportKey('pkcs1-private-pem');
console.log(process.env.PRIVATE_KEY);

app.use(express.static('views'));
app.use(express.static('public'));

app.listen(process.env.PORT, () => {
  console.log(`Example app listening on port ${process.env.PORT}!`);
});
