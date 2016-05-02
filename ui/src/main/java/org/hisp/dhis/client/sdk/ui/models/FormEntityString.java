package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.Nullable;

abstract class FormEntityString extends FormEntity {
    private OnFormEntityChangeListener onFormEntityChangeListener;
    private String value;

    public FormEntityString(String id, String label) {
        super(id, label);
    }

    @Nullable
    public OnFormEntityChangeListener getOnFormEntityChangeListener() {
        return onFormEntityChangeListener;
    }

    public void setOnFormEntityChangeListener(@Nullable OnFormEntityChangeListener changeListener) {
        this.onFormEntityChangeListener = changeListener;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    public void setValue(@Nullable String value) {
        if (!isValueSame(value)) {
            this.value = value;

            if (onFormEntityChangeListener != null) {
                this.onFormEntityChangeListener.onFormEntityChanged(this);
            }
        }
    }

    private boolean isValueSame(@Nullable String value) {
        if (this.value != null) {
            return this.value.equals(value);
        } else {
            return value == null;
        }
    }
}
