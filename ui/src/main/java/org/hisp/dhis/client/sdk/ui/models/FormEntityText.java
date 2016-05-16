package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

public class FormEntityText extends FormEntity {

    public FormEntityText(String id, String label) {
        this(id, label, null);
    }

    public FormEntityText(String id, String label, Object tag) {
        super(id, label, tag);
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.TEXT;
    }
}
