package com.ospx.cloudsavemod;

import arc.files.Fi;
import arc.util.Log;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import mindustry.ui.fragments.LoadingFragment;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static mindustry.Vars.ui;

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
            } catch (GeneralSecurityException | IOException e) {
                ui.showException("An error occurred while connecting to Google Drive", e);
            }
        });
    }

    public static void export(LoadingFragment frag) {
        pool.submit(() -> {
            try {
                var file = new Fi("export_data.zip");

                ui.settings.exportData(file);

                File fileMetadata = new File();
                fileMetadata.setName("data.zip");
                fileMetadata.setParents(Collections.singletonList("appDataFolder"));

                FileContent mediaContent = new FileContent("application/zip", file.file());

                Drive.Files.Create request = service.files().create(fileMetadata, mediaContent);
                request.getMediaHttpUploader().setProgressListener(uploader -> {
                    switch (uploader.getUploadState()) {
                        case MEDIA_IN_PROGRESS -> frag.setProgress((float) uploader.getProgress());
                        case MEDIA_COMPLETE -> frag.hide();
                    }
                });
                request.execute();
            } catch (IOException e) {
                frag.hide();
                ui.showException(e);
            }
        });
    }

    public static void importData(LoadingFragment frag) {
        pool.submit(() -> {
            try {
                FileList files = service.files().list()
                        .setSpaces("appDataFolder")
                        .setPageSize(10)
                        .execute();
                Log.info(files.toString());
                var data = files.getFiles().stream()
                        .filter(f -> f.getName().equals("data.zip"))
                        .sorted(Comparator.comparing(f -> -f.getCreatedTime().getValue()))
                        .collect(Collectors.toList());

                Log.info(data.toString());
                if (!data.isEmpty()) {
                    ByteArrayOutputStream content = new ByteArrayOutputStream();

                    var request = service.files().get(data.get(0).getId());
                    request.getMediaHttpDownloader().setProgressListener(downloader -> {
                        switch (downloader.getDownloadState()) {
                            case MEDIA_IN_PROGRESS -> frag.setProgress((float) downloader.getProgress());
                            case MEDIA_COMPLETE -> frag.hide();
                        }
                    });
                    request.executeMediaAndDownloadTo(content);

                    try(OutputStream out = new FileOutputStream("data.zip")) {
                        content.writeTo(out);
                    }

                    ui.settings.importData(new Fi("data.zip"));
                } else {
                    ui.showInfo("Empty");
                }
            } catch (IOException e) {
                frag.hide();
                ui.showException(e);
            }
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
