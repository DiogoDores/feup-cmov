const User = require('../models/user')

exports.getUser = async (req, res, next) => {
    try {
        const user = await User.findOne({ username: req.params.username })
        if (user == null) return res.status(404).json({ message: 'Cannot find user.' })
        res.user = user
    } catch (err) {
        return res.status(500).json({ message: err.message })
    }
    next()
}
