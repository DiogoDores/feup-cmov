const express = require('express')
const models = require('./models')

const app = express()
require('dotenv').config()

models.connectDB().then(async () => {
    app.listen(process.env.PORT, () => {
        console.log(`Example app listening on port ${process.env.PORT}!`)
    })
})

app.get('/', (req, res) => res.send('Hello world!'))