package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.commons.DbContract.StateColumn;
import org.hisp.dhis.client.sdk.core.commons.DbContract.IdColumn;

public interface TrackedEntityDataValueStoreI {
    interface TrackedEntityDataValueColumns extends IdColumn, StateColumn {
        String TABLE_NAME = "trackedEntityDataValues";

        String COLUMN_DATA_ELEMENT = "dataElement";
        String COLUMN_EVENT = "event";
        String COLUMN_STORED_BY = "storedBy";
        String COLUMN_VALUE = "value";
    }
}
