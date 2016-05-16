package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

public class FormEntityCheckBox extends FormEntity {
    private String value;

    public FormEntityCheckBox(String id, String label) {
        this(id, label, null);
    }

    public FormEntityCheckBox(String id, String label, Object tag) {
        super(id, label, tag);
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
