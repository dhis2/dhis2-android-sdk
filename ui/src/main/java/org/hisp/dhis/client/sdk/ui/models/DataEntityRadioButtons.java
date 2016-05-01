package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

public class DataEntityRadioButtons extends DataEntity {
    private String value;

    public DataEntityRadioButtons(String id, String label) {
        super(id, label);
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.RADIO_BUTTONS;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
