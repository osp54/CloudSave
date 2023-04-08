package com.ospx.cloudsavemod;

import arc.util.Log;
import arc.util.Time;
import com.ospx.cloudsavemod.dialogs.CSMenu;
import mindustry.gen.Icon;
import mindustry.mod.Mod;

import static mindustry.Vars.ui;

public class Main extends Mod {
    @Override
    public void init() {
        Time.mark();
        ui.menufrag.addButton("Cloud Save", Icon.save, () -> new CSMenu().show());
        Log.infoTag("CloudSave", "Mod has loaded in " + Time.elapsed());
    }
}
