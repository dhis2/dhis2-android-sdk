package org.hisp.dhis.client.sdk.ui.rows.checkboxrowview;

import android.widget.CompoundButton;

import org.hisp.dhis.client.sdk.ui.models.FormEntityCheckBox;

import static org.hisp.dhis.client.sdk.ui.rows.checkboxrowview.CheckBoxRowView.EMPTY_FIELD;
import static org.hisp.dhis.client.sdk.ui.rows.checkboxrowview.CheckBoxRowView.TRUE;

final class CheckBoxRowOnCheckBoxListener implements CompoundButton.OnCheckedChangeListener {
    private FormEntityCheckBox dataEntity;

    void setDataEntity(FormEntityCheckBox dataEntity) {
        this.dataEntity = dataEntity;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String newValue = isChecked ? TRUE : EMPTY_FIELD;
        dataEntity.setValue(newValue, true);
    }
}
