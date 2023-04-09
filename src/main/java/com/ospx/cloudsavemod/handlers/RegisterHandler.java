package com.ospx.cloudsavemod.handlers;

import com.ospx.cloudsavemod.Main;
import okhttp3.Call;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static mindustry.Vars.ui;

public class RegisterHandler implements Handler {
    private final String email;
    private final String password;

    public RegisterHandler(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        if (!response.isSuccessful()) {
            ui.showErrorMessage(response.body().string());
            return;
        }

        Main.saveCredentials(email, password);
        ui.showInfo("Successfully registered");
    }
}
