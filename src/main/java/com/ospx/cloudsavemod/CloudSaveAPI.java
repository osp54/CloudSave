package com.ospx.cloudsavemod;

import arc.Core;
import okhttp3.*;

import java.io.File;

import static mindustry.Vars.ui;

public class CloudSaveAPI {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                String credentials = Core.settings.getString("cs_credentials");
                if (credentials == null) return chain.proceed(chain.request());

                Request request = chain.request().newBuilder()
                        .header("Authorization", "Basic " + credentials)
                        .build();
                return chain.proceed(request);
            }).build();
    public static String apiServerURL = "http://localhost:3000";

    public static void registerAccount(String email, String password, Callback callback) {
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder().url(apiServerURL + "/register").post(formBody).build();
        client.newCall(request).enqueue(callback);
    }

    public static void getSavesList(Callback callback) {
        if (checkCredentials()) return;

        Request request = new Request.Builder().url(apiServerURL + "/saves/list").get().build();
        client.newCall(request).enqueue(callback);
    }

    public static void uploadSave(File file, Callback callback) {
        if (checkCredentials()) return;

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "save",
                        "save.zip",
                        RequestBody.create(file, MediaType.parse("application/zip"))
                )
                .build();

        Request request = new Request.Builder().url(apiServerURL + "/saves/upload").post(body).build();
        client.newCall(request).enqueue(callback);
    }

    public static void downloadSave(String id, Callback callback) {
        Request request = new Request.Builder().url(apiServerURL + "/saves/" + id + "/download").get().build();

        client.newCall(request).enqueue(callback);
    }

    private static boolean checkCredentials() {
        if (Core.settings.getString("cs_credentials") == null) {
            ui.showErrorMessage("Register/Login first");
            return true;
        }
        return false;
    }
}
