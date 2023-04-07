package com.ospx.cloudsavemod;

import arc.*;
import arc.util.Log;
import arc.util.Time;
import com.ospx.cloudsavemod.dialogs.MainDialog;
import mindustry.mod.*;

import static mindustry.Vars.ui;

public class Main extends Mod{
    @Override
    public void init() {
        Time.mark();

        ui.menufrag.addButton("Cloud Save",  Core.atlas.drawable("cloud-save-google"),
                () -> new MainDialog().show());
        Log.info("[Cloud Save] Loaded in @ ms", Time.elapsed());
    }
}
