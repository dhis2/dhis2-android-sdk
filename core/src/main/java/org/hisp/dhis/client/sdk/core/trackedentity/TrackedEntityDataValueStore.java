package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.commons.DataStore;
import org.hisp.dhis.client.sdk.core.commons.DbContract.StateColumn;
import org.hisp.dhis.client.sdk.core.commons.DbContract.IdColumn;
import org.hisp.dhis.client.sdk.models.common.State;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.List;

public interface TrackedEntityDataValueStore extends DataStore<TrackedEntityDataValue> {
    interface TrackedEntityDataValueColumns extends IdColumn, StateColumn {
        String TABLE_NAME = "trackedEntityDataValues";

        String COLUMN_DATA_ELEMENT = "dataElement";
        String COLUMN_EVENT = "event";
        String COLUMN_STORED_BY = "storedBy";
        String COLUMN_VALUE = "value";
    }

    List<TrackedEntityDataValue> query(String eventUid);
}
