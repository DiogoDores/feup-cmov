const mongoose = require('mongoose')
const user = require('./user')

exports.connectDB = () => {
    return mongoose.connect(process.env.DATABASE_URL)
}
