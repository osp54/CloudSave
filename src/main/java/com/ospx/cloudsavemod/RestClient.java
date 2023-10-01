package com.ospx.cloudsavemod;

import arc.Core;
import com.github.kevinsawicki.http.HttpRequest;
import com.ospx.cloudsavemod.models.Saves;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.ospx.cloudsavemod.Main.gson;

public class RestClient {
    public static String API_SERVER_URL = "http://localhost:3000";

    public String credentials;


    public RestClient(String credentials) {
        this.credentials = credentials;
    }

    public int register(String email, String password) throws ExecutionException, InterruptedException, TimeoutException {
        return HttpRequest.post(API_SERVER_URL + "/register")
                .form("email", email)
                .form("password", password)
                .code();
    }

    public Saves getSavesList() {
        String json  = HttpRequest.get(API_SERVER_URL + "/saves/list")
                .authorization(generateBasicAuth())
                .body();

        if (json == null) return null;

        return gson.fromJson(json, Saves.class);
    }

    public int uploadSave(File file) {
        return HttpRequest.post(API_SERVER_URL + "/saves/upload")
                .authorization(generateBasicAuth())
                .part("save", file)
                .code();
    }

    public int downloadSave(String id) {
        return HttpRequest.get(API_SERVER_URL + "/saves/" + id + "/download")
                .authorization(generateBasicAuth())
                .receive(Core.files.local("save.zip").file())
                .code();
    }

    private String generateBasicAuth() {
        return "Basic " + credentials;
    }

    public void updateCredentials(String credentials) {
        this.credentials = credentials;
    }
}
