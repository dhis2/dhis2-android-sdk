package org.hisp.dhis.client.sdk.ui.rows.edittextrowview;

import android.text.Editable;

import org.hisp.dhis.client.sdk.ui.models.FormEntityEditText;
import org.hisp.dhis.client.sdk.ui.views.AbsTextWatcher;

final class EditTextRowOnValueChangedListener extends AbsTextWatcher {
    private FormEntityEditText dataEntity;

    void setDataEntity(FormEntityEditText dataEntity) {
        this.dataEntity = dataEntity;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (dataEntity != null) {
            dataEntity.setValue(editable.toString(), true);
        }
    }
}
