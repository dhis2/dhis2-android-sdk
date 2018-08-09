package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.arch.handlers.ObjectWithoutUidSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public class DataSetCompleteRegistrationHandler {

    private DataSetCompleteRegistrationHandler() {}

    public static SyncHandler<DataSetCompleteRegistration> create(DatabaseAdapter databaseAdapter) {
        return new ObjectWithoutUidSyncHandlerImpl<>(DataSetCompleteRegistrationStore.create(databaseAdapter));
    }

}
