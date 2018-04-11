package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public class CategoryOptionComboHandler {
    @NonNull
    private final CategoryOptionComboStore store;

    CategoryOptionComboHandler(
            @NonNull CategoryOptionComboStore store) {
        this.store = store;
    }

    public void handle(@NonNull CategoryOptionCombo entity) {

        if (isDeleted(entity)) {
            store.delete(entity);
        } else {

            boolean updated = store.update(entity, entity);

            if (!updated) {
                store.insert(entity);
            }
        }
    }

    public static CategoryOptionComboHandler create(DatabaseAdapter databaseAdapter) {
        return new CategoryOptionComboHandler(new CategoryOptionComboStoreImpl(databaseAdapter));
    }
}
