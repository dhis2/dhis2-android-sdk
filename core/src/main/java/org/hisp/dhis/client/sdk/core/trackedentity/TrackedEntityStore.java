package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.commons.DbContract.NameableColumns;

public interface TrackedEntityStore {
    interface TrackedEntityColumns extends NameableColumns {
        String TABLE_NAME = "trackedEntities";
    }
}
