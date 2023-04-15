package com.ospx.cloudsavemod;

import arc.Core;
import arc.util.Log;
import arc.util.Time;
import arc.util.serialization.Base64Coder;
import com.google.gson.Gson;
import com.ospx.cloudsavemod.dialogs.CSMenu;
import mindustry.gen.Icon;
import mindustry.mod.Mod;

import static mindustry.Vars.ui;

public class Main extends Mod {
    public static final Gson gson = new Gson();

    public static void saveCredentials(String email, String password) {
        Core.settings.put("cs_credentials", Base64Coder.encodeString(email + ":" + password));
    }

    @Override
    public void init() {
        Time.mark();
        ui.menufrag.addButton("Cloud Save", Icon.save, () -> new CSMenu().show());
        Log.infoTag("CloudSave", "Mod has loaded in " + Time.elapsed());
    }
}