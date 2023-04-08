package com.ospx.cloudsavemod.handlers;

import okhttp3.Call;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

public class UploadSaveHandler implements Handler {
    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) {
        if (!response.isSuccessful()) {
            // todo handle error
        }
        // todo notice
    }
}
