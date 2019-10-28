const mongoose = require('mongoose')

const userSchema = new mongoose.Schema({
    uuid: {
        type: String,
        default: require('uuid/v1')(),
        unique: true
    },
    username: {
        type: String,
        required: true,
        unique: true,
    },
    password: {
        type: String,
        required: true,
        unique: false
    },
    name: {
        type: String,
        required: true,
        unique: false,
    },
})

module.exports = mongoose.model('User', userSchema)
