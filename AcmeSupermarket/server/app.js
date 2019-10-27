require('dotenv').config()

const express = require('express')
const mongoose = require('mongoose')
const app = express()
app.use(express.json())

mongoose.connect(process.env.DATABASE_URL, { useNewUrlParser: true })

const db = mongoose.connection
db.on('error', (err) => console.log(err))
db.once('open', () => console.log('Connected to database!'))

const usersRouter = require('./routes/users')
app.use('/users', usersRouter)

app.listen(process.env.PORT, () => {
    console.log(`Example app listening on port ${process.env.PORT}!`)
})