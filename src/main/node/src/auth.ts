import { NextFunction, Request, Response } from 'express';
import bcrypt from 'bcrypt';
import { IUser, User } from './models/userModel';
import { Types, Document } from 'mongoose';

export interface CustomRequest extends Request {
    user: Document<unknown, {}, IUser> & Omit<IUser & Required<{
        _id: Types.ObjectId;
    }>, never>;
    notHashedPassword: string;
}

export async function auth(req: Request, res: Response, next: NextFunction) {
    const creq = req as CustomRequest;
    const authorization = req.headers.authorization;

    if (!authorization || !authorization.startsWith('Basic ')) {
        return res.header('WWW-Authenticate', 'Basic').send();
    }

    const [ email, password ] = Buffer.from(authorization.substring(5).trim(), "base64").toString().split(":");

    if (!email || !password) {
        return invalidCredentials(res);
    }

    const user = await User.findOne({ email });

    if (!user) {
        return invalidCredentials(res);
    }

    const passwordIsValid = bcrypt.compareSync(
        password,
        user.password
    );
    
    if (!passwordIsValid) {
        return invalidCredentials(res);
    }
    
    creq.user = user;
    creq.notHashedPassword = password;
    next();
}

function invalidCredentials(res: Response) {
    return res.status(401).send({
        message: "Invalid Email/Password!"
    });
} 