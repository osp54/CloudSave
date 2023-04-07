package com.ospx.cloudsavemod.dialogs;

import arc.scene.ui.layout.Table;
import mindustry.ui.dialogs.BaseDialog;
public class MainDialog extends BaseDialog {
    public MainDialog() {
        super("Cloud Save");
        closeOnBack();

        shown(() -> {
            cont.clear();
            Table table = new Table();
            cont.add(table);
        });

        addCloseButton();
    }
}
