import {model, Schema, Types, ValidatorProps} from "mongoose"
import isEmail from "validator/lib/isEmail";

export interface ISave {
    _id: Types.ObjectId;
    createdAt: Date;
}

export interface IUser {
    _id: Types.ObjectId;
    email: string;
    password: string;

    saves: Types.DocumentArray<ISave>;
}

export const User = model("User", new Schema<IUser>({
    email: {
        type: String,
        unique: true,
        required: [true, 'Email is required'],
        validate: {
            validator: (value: string) => isEmail(value),
            message: (props: ValidatorProps) => `${props.value} is not a valid email`
        }
    },
    password: {
        type: String,
        required: [true, 'Password is required'],
        validate: {
            validator: (value: string) => value.length > 7
        },
        message: () => 'Password must be at least seven characters long'
    },
    saves: [{
        createdAt: Date
    }]
}));