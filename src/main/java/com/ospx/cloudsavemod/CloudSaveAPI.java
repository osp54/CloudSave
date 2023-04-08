package com.ospx.cloudsavemod;

import arc.files.Fi;
import arc.util.serialization.Base64Coder;
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static mindustry.Vars.dataDirectory;
import static mindustry.Vars.ui;

public class CloudSaveAPI {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
                    if (credentials == null) return chain.proceed(chain.request());
                    Request request = chain.request().newBuilder()
                            .header("Authorization", "Basic " + credentials)
                        .build();
                    return chain.proceed(request);
                }
            }).build();

    public static Fi credentialsFile = dataDirectory.child("cloudsave_credentials");
    public static String credentials;

    public static String apiServerURL = "http://localhost:3000";

    public static void init() {
        if (credentialsFile.exists()) { // todo хранить пароль и имейл в Core.settings
            credentials = credentialsFile.readString();
        }
    }

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

    public static void saveCredentials(String email, String password) {
        String encoded = Base64Coder.encodeString(email + ":" + password);

        credentials = encoded;
        credentialsFile.writeString(encoded);
    }

    private static boolean checkCredentials() {
        if (credentials == null) {
            ui.showErrorMessage("Register first");
            return true;
        }
        return false;
    }
}
