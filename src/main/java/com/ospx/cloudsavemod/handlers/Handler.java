package com.ospx.cloudsavemod.handlers;

import okhttp3.Call;
import okhttp3.Callback;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static mindustry.Vars.ui;

public interface Handler extends Callback {
    @Override
    default void onFailure(@NotNull Call call, @NotNull IOException e) {
        ui.showException(e);
    }
}
