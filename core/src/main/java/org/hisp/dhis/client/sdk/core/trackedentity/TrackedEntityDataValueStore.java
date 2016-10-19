package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.commons.database.DataStore;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.List;

public interface TrackedEntityDataValueStore extends DataStore<TrackedEntityDataValue> {
    List<TrackedEntityDataValue> query(String eventUid);
}
