package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

final class CategoryOptionComboHandler {

    private CategoryOptionComboHandler() {
    }

    public static SyncHandler<CategoryOptionCombo> create(DatabaseAdapter databaseAdapter) {
        return new IdentifiableSyncHandlerImpl<>(CategoryOptionComboStore.create(databaseAdapter));
    }
}