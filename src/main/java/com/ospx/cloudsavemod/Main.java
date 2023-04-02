package com.ospx.cloudsavemod;

import arc.*;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.mod.*;

public class Main extends Mod{
    @Override
    public void init() {
        Time.mark();
        GoogleDrive.connect();

        Vars.ui.menufrag.addButton("Cloud Save",  Core.atlas.drawable("cloud-save-google"),
                () -> new MainDialog().show());

        Log.info("[Cloud Save] Loaded in @ ms", Time.elapsed());
    }
}
