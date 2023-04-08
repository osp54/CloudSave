package com.ospx.cloudsavemod.dialogs;

import mindustry.ui.dialogs.BaseDialog;

public class CSBaseDialog extends BaseDialog {
    public CSBaseDialog(String title) {
        super(title);

        shouldPause = false;
        addCloseButton();
    }
}
