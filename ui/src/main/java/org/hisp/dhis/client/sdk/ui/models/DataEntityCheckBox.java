package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

public class DataEntityCheckBox extends DataEntity {
    private String value;

    public DataEntityCheckBox(String id, String label) {
        super(id, label);
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.CHECKBOX;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
