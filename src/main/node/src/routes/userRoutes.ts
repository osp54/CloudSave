import express, {Router} from 'express';
import bcrypt from 'bcrypt';

import {User} from '../models/userModel'

export const userRoutes: Router = express.Router();

userRoutes.post('/register', async (req, res) => {
    try {
        const {email, password} = req.body;

        let userExists = await User.findOne({email});

        if (userExists) {
            res.status(401).json({message: "Email is already in use"});
            return;
        }

        let user = new User({
            email,
            password: bcrypt.hashSync(password, 8)
        });

        await user.save();
        res.status(200).json({message: "User Registered successfully"});
    } catch (err: any) {
        console.log(err.stack);
        return res.status(500).send({
            message: err.message
        });
    }
});