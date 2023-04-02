package com.ospx.cloudsavemod;

import arc.Core;
import arc.files.Fi;
import arc.func.Cons;
import arc.util.Log;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static mindustry.Vars.enableDarkness;
import static mindustry.Vars.ui;

@SuppressWarnings("SimplifyStreamApiCallChains")
public class GoogleDrive {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Mindustry Cloud Save";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */

    public static boolean connected = false;

    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE_APPDATA);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public static Drive service;
    public static ExecutorService pool = Executors.newFixedThreadPool(2);

    public static void connect() {
        pool.submit(() -> {
            NetHttpTransport HTTP_TRANSPORT;
            try {
                HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();

                connected = true;
            } catch (GeneralSecurityException | IOException e) {
                ui.loadfrag.hide();
                ui.showException("An error occurred while connecting to Google Drive", e);
            }
        });
    }

    public static Future<?> exportData() {
        return pool.submit(() -> {
            try {
                var file = Core.files.local("mindustry-data-export.zip");
                ui.settings.exportData(file);

                File fileMetadata = new File();
                fileMetadata.setName("data.zip");
                fileMetadata.setParents(Collections.singletonList("appDataFolder"));

                FileContent mediaContent = new FileContent("application/zip", file.file());

                Drive.Files.Create request = service.files().create(fileMetadata, mediaContent);
                request.getMediaHttpUploader().setDirectUploadEnabled(file.readBytes().length < 6_000_000).setProgressListener(uploader -> {
                    switch (uploader.getUploadState()) {
                        case MEDIA_IN_PROGRESS -> ui.loadfrag.setProgress((float) uploader.getProgress());
                        case MEDIA_COMPLETE -> ui.loadfrag.hide();
                    }
                });
                request.execute();

                ui.showInfo("@data.exported");
            } catch (IOException e) {
                ui.loadfrag.hide();
                ui.showException(e);
            }
        });
    }

    public static Future<?> importData() {
        return getDataFiles((data) -> {
            Log.info(data.toString());

            if (data.isEmpty()) {
                ui.showInfo("Empty");
                return;
            }

            try {
                var request = service.files().get(data.get(0).getId());
                request.getMediaHttpDownloader().setDirectDownloadEnabled(data.get(0).size() < 6_000_000).setProgressListener(downloader -> {
                    switch (downloader.getDownloadState()) {
                        case MEDIA_IN_PROGRESS -> ui.loadfrag.setProgress((float) downloader.getProgress());
                        case MEDIA_COMPLETE -> ui.loadfrag.hide();
                    }
                });
                request.executeMediaAndDownloadTo(new FileOutputStream("data.zip"));

                ui.settings.importData(new Fi("data.zip"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Future<?> getDataFiles(Cons<List<File>> callback) {
        return pool.submit(() -> {
            FileList files;
            try {
                files = service.files().list()
                        .setSpaces("appDataFolder")
                        .setQ("mimeType='application/zip'")
                        .setFields("files(createdTime, name, id, size)")
                        .execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Log.info(files.toString());

            callback.get(files.getFiles().stream()
                    .filter(f -> f.getName().equals("data.zip"))
                    .sorted(Comparator.comparing(f -> -f.getCreatedTime().getValue()))
                    .collect(Collectors.toList()));
        });
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = Main.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        //returns an authorized Credential object.
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
