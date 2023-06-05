package com.ospx.cloudsavemod.dialogs;

import arc.graphics.Color;
import com.ospx.cloudsavemod.models.Save;
import mindustry.gen.Icon;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class CSSaveDialog extends CSBaseDialog {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

    public CSSaveDialog(String title, Save save) {
        super(title);

        cont.pane(description -> {
            description.center();
            description.defaults().padTop(10).left();

            description.add("[lightgray] Save ID:").color(Color.gray).padTop(0);
            description.row();
            description.add(save._id).growX().wrap().padTop(5);
            description.row();

            description.add("[lightgray] Save date:").color(Color.gray).padTop(0);
            description.row();
            description.add(formatter.format(Instant.ofEpochMilli(save.date.getTime()))).growX().wrap().padTop(5);
            description.row();

            row();
        });

        buttons.button("Load", Icon.save, () -> CSSaves.loadSave(save));
        buttons.button("Delete", Icon.trash, () -> CSSaves.deleteSave(save));
    }
}
