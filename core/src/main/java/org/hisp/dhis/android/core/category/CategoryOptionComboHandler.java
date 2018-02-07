package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import android.support.annotation.NonNull;

public class CategoryOptionComboHandler {
    @NonNull
    private final CategoryOptionComboStore store;

    public CategoryOptionComboHandler(
            @NonNull CategoryOptionComboStore store) {
        this.store = store;
    }

    public void handle(@NonNull CategoryOptionCombo entity) {

        if (isDeleted(entity)) {
            store.delete(entity.uid());
        } else {

            int rowsAffected = store.update(entity);
            boolean updated = rowsAffected >= 1;

            if (!updated) {
                store.insert(entity);
            }
        }
    }

}
