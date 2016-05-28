package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

public class FormEntityDate extends FormEntityCharSequence {

    public FormEntityDate(String id, String label) {
        this(id, label, null);
    }

    public FormEntityDate(String id, String label, Object tag) {
        super(id, label, tag);
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.DATE;
    }
}
