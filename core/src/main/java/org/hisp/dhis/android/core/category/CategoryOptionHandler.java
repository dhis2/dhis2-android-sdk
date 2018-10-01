package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public final class CategoryOptionHandler  {

    private CategoryOptionHandler() {
    }

    public static SyncHandler<CategoryOption> create(DatabaseAdapter databaseAdapter) {
        return new IdentifiableSyncHandlerImpl<>(CategoryOptionStore.create(databaseAdapter));
    }
}