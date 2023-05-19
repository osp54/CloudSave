package com.ospx.cloudsavemod.dialogs;

import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;

import static arc.scene.ui.TextButton.TextButtonStyle;
import static mindustry.Vars.iconMed;

public class CSMenu extends CSBaseDialog {
    public CSMenu() {
        super("Cloud Save");

        var menu = new Table(Tex.button);
        menu.defaults().size(400f, 60f);

        var savesDialog = new CSSaves("Saves");
        var manageAccount = new CSActions.CSManage("Manage account");

        TextButtonStyle style = Styles.flatt;
        float margin = 8f, size = iconMed;

        String credentials = Core.settings.getString("cs_credentials");
        if (credentials == null || credentials.isEmpty()) {
            menu.button("Manage account", Icon.players, style, size, manageAccount::show).marginLeft(margin).row();
        } else {
            menu.button("Manage saves", Icon.save, style, size, savesDialog::show).marginLeft(margin).row();
            menu.button("Logout", Icon.exit, style, size, this::confirmLogout).marginLeft(margin).row();
        }

        cont.top().margin(14f).clearChildren();
        cont.add(menu);
        row();
        pane(cont).grow().top();
        row();
        add(buttons).fillX();
    }

    private void confirmLogout() {
        Vars.ui.showConfirm("Do you really want to sign out?", () -> {
            Core.settings.remove("cs_credentials");
            Vars.ui.showInfo("You have been signed out successfully");
        });
    }
}
