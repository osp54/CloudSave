package com.ospx.cloudsavemod.handlers;

import arc.struct.Seq;
import com.ospx.cloudsavemod.models.Save;
import okhttp3.Call;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.ospx.cloudsavemod.Main.gson;

public class SavesListHandler implements Handler {
    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        if (!response.isSuccessful() || response.body() == null) {
            // todo handle error
            return;
        }

        Seq<Save> saves = Seq.with(gson.fromJson(response.body().string(), Save[].class));

        // todo show saves
    }
}
