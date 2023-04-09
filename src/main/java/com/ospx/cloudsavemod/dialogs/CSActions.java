package com.ospx.cloudsavemod.dialogs;

import arc.scene.ui.layout.Table;
import com.ospx.cloudsavemod.CloudSaveAPI;
import com.ospx.cloudsavemod.handlers.RegisterHandler;
import mindustry.gen.Icon;
import mindustry.gen.Tex;

import static mindustry.Vars.iconMed;
import static mindustry.Vars.ui;

public class CSActions {
    public static class CSRegister extends CSBaseDialog {
        String email;
        String password;

        public CSRegister(String title) {
            super(title);

            var actions = new Table(Tex.button);
            float margin = 8f;
            actions.defaults().size(400f, 60f);

            actions.field("Email", text -> this.email = text).margin(margin).row();
            actions.field("Password", text -> this.password = text).margin(margin).row();
            actions.button("Confirm", Icon.add, iconMed, () ->
                    CloudSaveAPI.registerAccount(email, password, new RegisterHandler(email, password))).padTop(margin).row();

            cont.top();
            cont.margin(14f);
            cont.clearChildren();
            cont.add(actions);

            row();
            pane(cont).grow().top();
            row();
            add(buttons).fillX();
        }
    }

    public static class CSLogin extends CSBaseDialog {
        public CSLogin(String title) {
            super(title);
        }
    }

    public static class CSLogout extends CSBaseDialog {
        public CSLogout(String title) {
            super(title);
        }
    }
}
