package org.hisp.dhis.client.sdk.ui.rows.checkboxrowview;

import android.view.View;
import android.widget.CheckBox;

final class CheckBoxRowViewOnRowClickListener implements View.OnClickListener {
    private final CheckBox checkBox;

    CheckBoxRowViewOnRowClickListener(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    @Override
    public void onClick(View v) {
        checkBox.setChecked(!checkBox.isChecked());
    }
}
