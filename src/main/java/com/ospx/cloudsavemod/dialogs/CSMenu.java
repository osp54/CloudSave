package com.ospx.cloudsavemod.dialogs;

import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;

import static arc.scene.ui.TextButton.TextButtonStyle;
import static mindustry.Vars.iconMed;

public class CSMenu extends CSBaseDialog {
    public CSMenu() {
        super("Cloud Save");

        // Menu from game settings
        var menu = new Table(Tex.button);

        // Styles
        TextButtonStyle style = Styles.flatt;
        float margin = 8f, size = iconMed;
        menu.defaults().size(400f, 60f);

        // Dialogs
        var savesDialog = new CSSaves("Saves");
        var loginDialog = new CSActions.CSLogin("Login");
        var registerDialog = new CSActions.CSLogin("Register");
        var logoutDialog = new CSActions.CSLogout("Logout");

        // Buttons
        if (!Core.settings.getBool("cloud-save-logged")) {
            menu.button("Login", Icon.players, style, size, loginDialog::show).marginLeft(margin).row();
            menu.button("Register", Icon.add, style, size, registerDialog::show).marginLeft(margin).row();
        } else {
            menu.button("Manage saves", Icon.save, style, size, savesDialog::show).marginLeft(margin).row();
            menu.button("Logout", Icon.exit, style, size, logoutDialog::show).marginLeft(margin).row();
        }

        cont.top();
        cont.margin(14f);
        cont.clearChildren();
        cont.add(menu);

        row();
        pane(cont).grow().top();
        row();
        add(buttons).fillX();
    }
}
