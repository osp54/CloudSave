package com.ospx.cloudsavemod;

import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;

import static mindustry.Vars.ui;

public class MainDialog extends BaseDialog {
    public MainDialog() {
        super("Cloud Save");

        closeOnBack();

        shown(() -> {
            cont.clear();
            Table table = new Table();
            table.defaults().width(150).height(70);

            table.button("Connect", () -> {
                if (GoogleDrive.connected) {
                    ui.showErrorMessage("Already connected");
                    return;
                }

                ui.loadfrag.show();
                GoogleDrive.connect();
                ui.loadfrag.hide();
            }).padBottom(10).tooltip("Manual connect to google drive");
            table.row();
            table.button("Save", () -> {
                if (checkConnected()) return;

                ui.loadfrag.show();
                var task = GoogleDrive.exportData();

                ui.loadfrag.setButton(() -> {
                    if (!task.isCancelled()) task.cancel(false);
                    ui.loadfrag.hide();
                });
            }).padRight(10f).tooltip("Export data to cloud");

            table.button("Import", () -> {
                if (checkConnected()) return;

                Vars.ui.loadfrag.show();
                var task = GoogleDrive.importData();

                ui.loadfrag.setButton(() -> {
                    if (!task.isCancelled()) task.cancel(false);
                    ui.loadfrag.hide();
                });
            }).tooltip("Import the data from cloud");

            table.row();
            cont.add(table);
        });

        addCloseButton();
    }

    private boolean checkConnected() {
        if (!GoogleDrive.connected) {
            ui.showErrorMessage("It is not connected to Google drive.");
        }

        return !GoogleDrive.connected;
    }
}
