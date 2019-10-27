const mongoose = require('mongoose')

const userSchema = new mongoose.Schema({
    username: {
        type: String,
        unique: true,
    },
})

module.exports = mongoose.model('User', userSchema)
