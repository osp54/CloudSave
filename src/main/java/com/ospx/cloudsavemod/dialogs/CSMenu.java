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

        // Menu from game settings
        var menu = new Table(Tex.button);

        // Styles
        TextButtonStyle style = Styles.flatt;
        float margin = 8f, size = iconMed;
        menu.defaults().size(400f, 60f);

        // Dialogs
        var savesDialog = new CSSaves("Saves");
        var manageAccount = new CSActions.CSManage("Manage account");

        // Buttons
        if (Core.settings.getString("cs_credentials") == null || Core.settings.getString("cs_credentials").isEmpty()) {
            menu.button("Manage account", Icon.players, style, size, manageAccount::show).marginLeft(margin).row();
        } else {
            menu.button("Manage saves", Icon.save, style, size, savesDialog::show).marginLeft(margin).row();
            menu.button("Logout", Icon.exit, style, size, () -> Vars.ui.showConfirm("Do you really want to sign out?", () -> {
                Core.settings.remove("cs_credentials");
                Vars.ui.showInfo("You have been signed out successfully");
            })).marginLeft(margin).row();
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
