import express, { Router } from 'express';

import { Types } from 'mongoose';
import { CustomRequest, auth } from '../auth';
import { UploadedFile } from 'express-fileupload';
import AdmZip from 'adm-zip'
import { Environment } from '../main';
import path from 'path';
import fs from 'fs/promises';

export const savesRoutes: Router = express.Router();

savesRoutes.get("/saves/list", auth, async (req, res) => {
   const creq = req as CustomRequest;
   
   res.status(200).json({
      saves: creq.user.saves.map((s) => {
		return {id: s.id, createdAt: s.createdAt}
	  })
   });
});

savesRoutes.post("/saves/upload", auth, async (req, res) => {
	const creq = req as CustomRequest;

	if(!req.files || !req.files.save) {
		return res.status(400).send({
			message: 'Missing file'
		});
	}

	const save = req.files!.save as UploadedFile;
	if (save.mimetype !== "application/zip") {
		return res.status(400).send({
			message: 'File mimetype must be application/zip'
		});
	}

	try {
		const zip = new AdmZip(save.data);
		if (!zip || !zip.getEntry("settings.bin")) {
			return res.status(400).send({
				message: 'Invalid save file'
			});
		}

		const id = new Types.ObjectId();
		const filePath = getSaveFilePath(id.toString());

		await fs.writeFile(filePath, save.data);

		creq.user.saves.push({
			_id: id,
			file: filePath,
			createdAt: Date.now()
		});

		await creq.user.save();
		
		res.status(200).send({
			message: "Success",
			save: creq.user.saves.at(-1)
		});

	} catch (err: any) {
		console.error(err.stack)
		return res.status(500).send({
			message: err.message
		});
	}
});

savesRoutes.get("/saves/:id/download", auth, (req, res) => {
	const creq = req as CustomRequest;
	const id = req.params.id;

	if (!creq.user.saves.some(s => s.id === id)) {
		res.status(403).send({
			message: "It is not your save"
		})
	}

	try {
		res.download(getSaveFilePath(id));
	} catch (err: any) {
		console.log(err.stack)
		res.status(500).send({
			message: err.message
		});
	}
});

function getSaveFilePath(id: string): string {
	return path.join(Environment.storageDirectory, id.toString());
}