package com.ospx.cloudsavemod.dialogs;

import arc.Core;
import arc.files.Fi;
import arc.input.KeyCode;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import com.ospx.cloudsavemod.RestClient;
import com.ospx.cloudsavemod.Utils;
import com.ospx.cloudsavemod.models.Save;
import com.ospx.cloudsavemod.models.Saves;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Icon;
import mindustry.net.Administration;
import mindustry.ui.Styles;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static arc.Core.app;
import static com.ospx.cloudsavemod.Main.gson;
import static com.ospx.cloudsavemod.Main.restClient;
import static mindustry.Vars.ui;

public class CSSaves extends CSBaseDialog {
    protected float locationY;

    public CSSaves(String savesTitle) {
        super(savesTitle);
        cont.clear();
        shown(() -> {
            Saves saves = restClient.getSavesList();

            if (saves == null) {
                hide();
                ui.showErrorMessage("Unable to load saves list. Check your internet connection.");
                return;
            }
            Seq<Save> savesSeq = Seq.with(saves.saves);

            Stack stack = new Stack();
            ScrollPane scrollPane = new ScrollPane(stack);
            scrollPane.update(() -> locationY = scrollPane.getScrollY());

            if (!savesSeq.isEmpty()) {
                Table table = new Table();

                for (Save save : savesSeq) {
                    table.button(t -> {
                        t.top().left();
                        t.margin(12);
                        t.defaults().left().top();

                        t.table(title -> {
                            title.left();
                            title.table(text -> {
                                text.add("[accent]" + save._id + "\n" + "[lightgray]" + save.date).wrap().top().width(300f).growX().left();
                                text.row();
                            }).top().growX();
                            title.add().growX();
                        }).growX().growY().left();

                        t.table(right -> {
                            right.right();
                            right.button(Icon.save, () -> CSSaves.loadSave(save)).size(50f).pad(5f);
                            right.button(Icon.trash, () -> CSSaves.deleteSave(save)).size(50f).pad(5f);
                        }).growX().right().padRight(-8f).padTop(-8f);
                    }, Styles.flatBordert, () -> new CSSaveDialog("Save", save).show()).row();

                    stack.add(table);
                }
            } else {
                cont.table(Styles.black6, t -> t.add("No saves found")).height(80f);
            }

            cont.row();
            cont.add(scrollPane).growX();

            setFillParent(true);
            title.setAlignment(Align.center);
            titleTable.row();
        });
    }

    public static void loadSave(Save save) {
        ui.showConfirm("@confirm", "Are you sure you want to load a save?", () -> {
            var status = restClient.downloadSave(save._id);
            if (Utils.showErrorStatus(status)) return;

            Fi dest = Core.files.local("save.zip");

            ui.settings.importData(dest);
            ui.showInfoOnHidden("You need to restart in order for the changes to take effect", app::exit);
        });
    }

    public static void deleteSave(Save save) {
        ui.showConfirm("@confirm", "Are you sure you want to delete a save?", () -> {
            // Remove save
            Call.announce("Save (id: " + save._id + ") has been successfully deleted");
        });
    }

    @Override
    public void addCloseButton() {
        buttons.button("@back", Icon.left, this::hide).size(210f, 64f);

        keyDown(key -> {
            if (key == KeyCode.escape || key == KeyCode.back) hide();
        });
    }
}
