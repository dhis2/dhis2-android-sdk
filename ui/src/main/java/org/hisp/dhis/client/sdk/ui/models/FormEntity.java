package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public abstract class FormEntity {
    private final String id;
    private final String label;

    public FormEntity(String id, String label) {
        this.id = isNull(id, "id must not be null");
        this.label = isNull(label, "label must not be null");
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @NonNull
    public abstract Type getType();

    public enum Type {
        EDITTEXT,
        CHECKBOX,
        COORDINATES,
        RADIO_BUTTONS,
        DATE,

        // not implemented
        FILTER,
        TEXT
    }
}
