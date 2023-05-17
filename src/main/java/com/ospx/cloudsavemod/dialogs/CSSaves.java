package com.ospx.cloudsavemod.dialogs;

import arc.input.KeyCode;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import com.ospx.cloudsavemod.models.Save;
import mindustry.gen.Call;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

import static mindustry.Vars.ui;

public class CSSaves extends CSBaseDialog {
    protected float locationY;

    public CSSaves(String savesTitle) {
        super(savesTitle);

        cont.clear();

        Stack stack = new Stack();
        ScrollPane scrollPane = new ScrollPane(stack);

        scrollPane.setFadeScrollBars(false);
        scrollPane.update(() -> locationY = scrollPane.getScrollY());

        Seq<Save> saves = getTestSavesList();

        if (!saves.isEmpty()) {
            Table uwu = new Table();
            for (Save save : saves) {
                uwu.button(t -> {
                    t.top().left();
                    t.margin(12f);

                    String details = "ID: " + save._id + " Time created: " + save.date.toString();
                    t.label(() -> details);
                    t.defaults().left().top();

                    t.table(right -> {
                        right.right();
                        right.button(Icon.save, () -> {
                            // Сохраняем
                            Call.announce("Save (id: " + save._id + ") has been successfully stored");
                        }).size(50f);
                        right.button(Icon.trash, () -> {
                            ui.showConfirm("@confirm", "Are you sure you want to delete a save?", () -> {
                                // Удаляем
                            });
                        }).size(50f);
                    }).growX().right().padRight(-8f).padTop(-8f);
                }, Styles.flatBordert, () -> {
                });

                stack.add(uwu);
            }
        } else {
            cont.table(Styles.black6, t -> t.add("No saves found")).height(80f);
        }

        cont.row();
        cont.add(scrollPane).growX();

        setFillParent(true);
        title.setAlignment(Align.center);
        titleTable.row();
    }

    public static Seq<Save> getTestSavesList() {
        var list = new Seq<Save>();
        for (int i = 0; i < 50; i++) list.add(new Save(false));
        return list;
    }

    @Override
    public void addCloseButton() {
        buttons.button("@back", Icon.left, this::hide).size(210f, 64f);

        keyDown(key -> {
            if (key == KeyCode.escape || key == KeyCode.back) hide();
        });
    }
}
