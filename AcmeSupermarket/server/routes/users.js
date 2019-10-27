const express = require('express')
const mongoose = require('mongoose')

const router = express.Router()
const User = require('../models/user')
const mw = require('../middlewares/user')

// Get all users.
router.get('/', async (req, res) => {
    try {
        const users = await User.find()
        res.json(users)
    } catch (err) {
        res.status(500).json({ message: err.message })
    }
})

// Get one user by username.
router.get('/:username', mw.getUser, (req, res) => {
    res.json(res.user)
})

// Create one user.
router.post('/', async (req, res) => {
    const user = new User({
        username: req.body.username,
    })

    try {
        const newUser = await user.save()
        res.status(201).json(newUser)
    } catch (err) {
        res.status(400).json({ message: err.message })
    }
})

// Update one user.
router.patch('/:username', mw.getUser, async (req, res) => {
    if (req.body.username != null) {
        res.user.username = req.body.username
    }

    try {
        const updatedUser = await res.user.save()
        res.json(updatedUser)
    } catch (error) {
        res.status(400).json({ message: err.message })
    }
})

// Delete one user.
router.delete('/:username', mw.getUser, async (req, res) => {
    try {
        await res.user.remove()
        res.json({ message: 'Deleted this user.' })
    } catch (err) {
        res.status(500).json({ message: err.message })
    }
})

module.exports = router