package com.ospx.cloudsavemod;

import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;

public class MainDialog extends BaseDialog {
    public MainDialog() {
        super("Cloud Save");

        closeOnBack();

        shown(() -> {
            cont.clear();
            Table table = new Table();
            table.defaults().width(150).height(70);
            table.button("Save", () -> {
                Vars.ui.loadfrag.show();
                GoogleDrive.export(Vars.ui.loadfrag);
            }).padRight(10f).tooltip("Export data to cloud");
            table.button("Import", () -> {
                Vars.ui.loadfrag.show();
                GoogleDrive.importData(Vars.ui.loadfrag);
            }).tooltip("Import the data from cloud");
            table.row();
            cont.add(table);
        });

        addCloseButton();
    }
}
