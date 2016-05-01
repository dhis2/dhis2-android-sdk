package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

public class DataEntityDate extends DataEntity {
    private String value;

    public DataEntityDate(String id, String label) {
        super(id, label);
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.DATE;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
