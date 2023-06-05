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
    public static final boolean debug = true; // For testing purposes only, please disable, whatever it broke the game

    public Main() throws Exception {
    }

    public static String saveCredentials(String email, String password) {
        String result = Base64Coder.encodeString(email + ":" + password);
        Core.settings.put("cs_credentials", result);

        return result;
    }

    public static RestClient restClient;

    @Override
    public void init() {
        Time.mark();

        try {
            restClient = new RestClient(Core.settings.getString("cs_credentials"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ui.menufrag.addButton("Cloud Save", Icon.save, () -> new CSMenu().show());
        Log.infoTag("CloudSave", "Mod has loaded in " + Time.elapsed());
    }
}