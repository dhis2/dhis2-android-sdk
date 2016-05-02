package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

public class FormEntityText extends FormEntity {

    public FormEntityText(String id, String label) {
        super(id, label);
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.TEXT;
    }
}
