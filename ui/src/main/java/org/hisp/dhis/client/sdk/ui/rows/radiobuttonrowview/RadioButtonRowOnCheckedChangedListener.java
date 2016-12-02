package org.hisp.dhis.client.sdk.ui.rows.radiobuttonrowview;

import android.widget.RadioGroup;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.FormEntityRadioButtons;

import static org.hisp.dhis.client.sdk.ui.rows.radiobuttonrowview.RadioButtonRowView.EMPTY_FIELD;
import static org.hisp.dhis.client.sdk.ui.rows.radiobuttonrowview.RadioButtonRowView.FALSE;
import static org.hisp.dhis.client.sdk.ui.rows.radiobuttonrowview.RadioButtonRowView.TRUE;


final class RadioButtonRowOnCheckedChangedListener implements RadioGroup.OnCheckedChangeListener {
    private FormEntityRadioButtons dataEntity;

    public void setDataEntity(FormEntityRadioButtons dataEntity) {
        this.dataEntity = dataEntity;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String newValue;

        if (checkedId == R.id.radiobutton_row_radiobutton_first) {
            newValue = TRUE;
        } else if (checkedId == R.id.radiobutton_row_radiobutton_second) {
            newValue = FALSE;
        } else {
            newValue = EMPTY_FIELD;
        }

        dataEntity.setValue(newValue, true);
    }
}
