package org.hisp.dhis.client.sdk.ui.rows.datepickerrowview;

import android.text.Editable;

import org.hisp.dhis.client.sdk.ui.models.FormEntityDate;
import org.hisp.dhis.client.sdk.ui.views.AbsTextWatcher;


final class DatePickerRowOnValueChangedListener extends AbsTextWatcher {
    private FormEntityDate dataEntity;

    void setDataEntity(FormEntityDate dataEntity) {
        this.dataEntity = dataEntity;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (dataEntity != null) {
            dataEntity.setValue(editable.toString(), true);
        }
    }
}
