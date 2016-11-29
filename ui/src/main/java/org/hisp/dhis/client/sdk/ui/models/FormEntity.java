package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public abstract class FormEntity {
    private final String id;
    private final String label;
    private final Object tag;
    private boolean locked = false;  //default is false

    public FormEntity(String id, String label){
        this(id, label, null);
    }

    public FormEntity(String id, String label, Object tag) {
        this.id = isNull(id, "id must not be null");
        this.label = isNull(label, "label must not be null");
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Object getTag() {
        return tag;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @NonNull
    public abstract Type getType();

    public enum Type {
        EDITTEXT, CHECKBOX, COORDINATES, RADIO_BUTTONS, DATE, FILTER, TEXT, EXPANSION_PANEL
    }
}
