package com.ospx.cloudsavemod;

import static com.ospx.cloudsavemod.Main.gson;
import static mindustry.Vars.ui;

public class Utils {
    public static boolean showErrorStatus(int status) {
        if (status != 200) {
            ui.showErrorMessage("Error. Server returned status code " + status);
            return true;
        }

        return false;
    }
}
