package com.ospx.cloudsavemod.dialogs;

import arc.scene.ui.layout.Table;
import com.ospx.cloudsavemod.Main;
import com.ospx.cloudsavemod.Utils;
import mindustry.gen.Icon;
import mindustry.gen.Tex;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.ospx.cloudsavemod.Main.restClient;
import static mindustry.Vars.iconMed;
import static mindustry.Vars.ui;

public class CSActions {
    public static class CSManage extends CSBaseDialog {
        private String email;
        private String password;

        public CSManage(String title) {
            super(title);

            var actions = new Table(Tex.button);

            float margin = 8f;
            actions.defaults().size(400f, 60f);

            actions.field("Email", this::setEmail).margin(margin).row();
            actions.field("Password", this::setPassword).margin(margin).row();

            actions.button("Register", Icon.add, iconMed, this::registerAccount).padTop(margin).row();
            actions.button("Login", Icon.players, iconMed, this::login).padTop(margin).row();

            cont.top().margin(14f).clearChildren();
            cont.add(actions);
            row();
            pane(cont).grow().top();
            row();
            add(buttons).fillX();
        }

        private void setEmail(String email) {
            this.email = email;
        }

        private void setPassword(String password) {
            this.password = password;
        }

        private void registerAccount() {
            try {
                var response = restClient.register(email, password);

                if (Utils.showErrorStatus(response)) return;

                restClient.updateCredentials(Main.saveCredentials(email, password));
                hide();
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                ui.showException("An error occurred while registration", e);
            }
        }

        private void login() {
            restClient.updateCredentials(Main.saveCredentials(email, password));
            hide();
        }
    }
}
