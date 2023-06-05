import express, { Router } from 'express';
import { Types } from 'mongoose';
import { auth, CustomRequest } from '../auth';
import { UploadedFile } from 'express-fileupload';
import crypto from 'crypto';
import AdmZip from 'adm-zip';
import fs from 'fs/promises';
import { Environment } from '../main';
import path from "path";
import stream from "stream";

export const savesRoutes: Router = express.Router();

savesRoutes.get('/saves/list', auth, async (req, res) => {
    const { user } = req as CustomRequest;

    res.status(200).json({
        saves: user.saves,
    });
});

savesRoutes.post('/saves/upload', auth, async (req, res) => {
    const { user, notHashedPassword, files } = req as CustomRequest;

    if (!files || !files.save) {
        return res.status(400).send({
            message: 'Missing file',
        });
    }

    const save = files.save as UploadedFile;
    if (save.mimetype !== 'application/zip') {
        return res.status(400).send({
            message: 'File mimetype must be application/zip',
        });
    }

    try {
        const zip = new AdmZip(save.data);
        if (!zip || !zip.getEntry('settings.bin')) {
            return res.status(400).send({
                message: 'Invalid save file',
            });
        }

        const id = new Types.ObjectId();
        const filePath = getSaveFilePath(id.toString());

        await fs.writeFile(
            filePath,
            encryptBuffer(save.data, notHashedPassword)
        );

        user.saves.push({
            _id: id,
            createdAt: Date.now(),
        });

        await user.save();

        res.status(200).send({
            message: 'Success',
            save: user.saves.at(-1),
        });
    } catch (err: any) {
        console.error(err.stack);
        return res.status(500).send({
            message: err.message,
        });
    }
});

savesRoutes.get('/saves/:id/download', auth, async (req, res) => {
    const { user, notHashedPassword, params } = req as CustomRequest;
    const { id } = params;

    if (!user.saves.some((s) => s.id === id)) {
        res.status(403).send({
            message: 'Save not found',
        });
    }

    try {
        const file = await fs.readFile(getSaveFilePath(id));

        const decrypted = decryptBuffer(file, notHashedPassword);

        res.setHeader('Content-Disposition', 'attachment; filename=save.zip');
        res.setHeader('Content-Type', 'application/zip');

        const readStream = new stream.PassThrough();
        readStream.end(decrypted);
        readStream.pipe(res);
    } catch (err: any) {
        console.log(err.stack);
        res.status(500).send({
            message: err.message,
        });
    }
});

function getSaveFilePath(id: string): string {
    return path.resolve(Environment.storageDirectory, id.toString());
}

function encryptBuffer(buffer: Buffer, password: string): Buffer {
    const key = crypto.createHash('sha256').update(password).digest();
    const iv = crypto.randomBytes(16);

    const cipher = crypto.createCipheriv('aes-256-cbc', key, iv);

    return Buffer.concat([iv, cipher.update(buffer), cipher.final()]);
}

function decryptBuffer(buffer: Buffer, password: string): Buffer {
    const iv = buffer.subarray(0, 16);
    buffer = buffer.subarray(16);

    const key = crypto.createHash('sha256').update(password).digest();

    const decipher = crypto.createDecipheriv('aes-256-cbc', key, iv);

    return Buffer.concat([decipher.update(buffer), decipher.final()]);
}