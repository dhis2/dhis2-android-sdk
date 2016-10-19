package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

public class FormExpansionPanel extends FormEntity {

    public FormExpansionPanel(String id, String label) {
        // false tag = expansion panel is collapsed by default
        super(id, label, false);
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.EXPANSION_PANEL;
    }
}
