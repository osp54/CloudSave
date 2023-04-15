package com.ospx.cloudsavemod.dialogs;

import arc.input.KeyCode;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Stack;
import arc.struct.Seq;
import arc.util.Align;
import com.ospx.cloudsavemod.models.Save;
import mindustry.gen.Icon;

public class CSSaves extends CSBaseDialog {
    protected float locationY;
    protected Seq<Save> saves;

    public CSSaves(String savesTitle) {
        super(savesTitle);

        cont.clear();

        Stack stack = new Stack();
        ScrollPane scrollPane = new ScrollPane(stack);

        scrollPane.setFadeScrollBars(false);
        scrollPane.update(() -> locationY = scrollPane.getScrollY());

        cont.row();
        cont.add(scrollPane).growX();

        setFillParent(true);
        title.setAlignment(Align.center);
        titleTable.row();
    }

    @Override
    public void addCloseButton() {
        buttons.button("@back", Icon.left, this::hide).size(210f, 64f);

        keyDown(key -> {
            if (key == KeyCode.escape || key == KeyCode.back) hide();
        });
    }
}
