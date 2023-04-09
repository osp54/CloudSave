package com.ospx.cloudsavemod.handlers;

import arc.Core;
import arc.files.Fi;
import okhttp3.Call;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static mindustry.Vars.ui;

public class DownloadSaveHandler implements Handler {
    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        if (!response.isSuccessful()) {
            System.out.println(response);
            return; // todo show error
        }

        if (response.body() == null) {
            ui.showErrorMessage("The server returned nothing.");
            return;
        }

        Fi dest = Core.files.local("save.zip");

        BufferedSink sink = Okio.buffer(Okio.sink(dest.file()));
        sink.writeAll(response.body().source());
        sink.close();

        ui.settings.importData(dest);
        ui.showInfo("imported");
    }
}
