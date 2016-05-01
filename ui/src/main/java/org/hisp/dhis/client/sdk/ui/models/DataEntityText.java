package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

public class DataEntityText extends DataEntity {

    public DataEntityText(String id, String label) {
        super(id, label);
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.TEXT;
    }
}
