package org.hisp.dhis.client.sdk.ui.rows.coordinaterowview;

import android.text.Editable;

import org.hisp.dhis.client.sdk.ui.models.FormEntityCoordinate;
import org.hisp.dhis.client.sdk.ui.views.AbsTextWatcher;

// TODO we need to have two OnValueChangedListeners(one for latitude, another for longitude)
final class CoordinateRowOnValueChangedListener extends AbsTextWatcher {
    private FormEntityCoordinate dataEntity;

    public void setDataEntity(FormEntityCoordinate dataEntity) {
        this.dataEntity = dataEntity;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (dataEntity != null) {
            // dataEntity.updateValue(editable.toString());
        }
    }
}
