import dotenv from 'dotenv';
import fs from 'fs';
import path from 'path';
import * as env from 'env-var';
dotenv.config();

export class Environment {
    static readonly expressPort: number = env.get('PORT').required().asIntPositive();
    static readonly mongodbURI: string = env.get('MONGODB_URI').required().asString();
    static storageDirectory: string = env.get('STORAGE_DIRECTORY').asString() || 'data/';
    static {
        this.storageDirectory = this.storageDirectory.startsWith("/") ? this.storageDirectory :  path.join(process.cwd(), this.storageDirectory);
        if (!fs.existsSync(this.storageDirectory)) fs.mkdirSync(this.storageDirectory);
    }
}

import express, { Express } from 'express';
import { json, urlencoded } from 'body-parser';
import fileUpload from 'express-fileupload'
import mongoose from 'mongoose';
import morgan from 'morgan';

import { userRoutes } from './routes/userRoutes';
import { savesRoutes } from './routes/savesRoutes';

const app: Express = express();

mongoose.connect(Environment.mongodbURI, {
    dbName: "cloudsave"
}).then(() => {
    console.log('Connected to Database Successfully');
})

app.use(json());
app.use(urlencoded({ extended: true }));
app.use(morgan('short'));
app.use(fileUpload({
    limits: { fileSize: 200 * 1024 * 1024 },
    abortOnLimit: true
}));

app.use(userRoutes);
app.use(savesRoutes);

app.listen(Environment.expressPort, () => {
    console.log(`Server is running at http://localhost:${Environment.expressPort}`);
});

