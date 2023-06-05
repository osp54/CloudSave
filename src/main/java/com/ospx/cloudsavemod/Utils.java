package com.ospx.cloudsavemod;

import com.ospx.cloudsavemod.models.RestMessage;
import org.eclipse.jetty.client.ContentResponse;

import static com.ospx.cloudsavemod.Main.gson;
import static mindustry.Vars.ui;

public class Utils {
    public static boolean showErrorStatus(ContentResponse response) {
        if (response.getStatus() != 200) {
            ui.showErrorMessage(gson.fromJson(response.getContentAsString(), RestMessage.class).message);
            return true;
        }

        return false;
    }
}
