package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.commons.DbContract.NameableColumns;
import org.hisp.dhis.client.sdk.core.commons.IdentifiableObjectStore;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

public interface TrackedEntityStore extends IdentifiableObjectStore<TrackedEntity> {
    interface TrackedEntityColumns extends NameableColumns {
        String TABLE_NAME = "trackedEntities";
    }
}
