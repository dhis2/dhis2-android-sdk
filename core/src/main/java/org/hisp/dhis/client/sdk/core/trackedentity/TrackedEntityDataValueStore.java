package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.commons.DataStore;
import org.hisp.dhis.client.sdk.core.commons.DbContract.StateColumn;
import org.hisp.dhis.client.sdk.core.commons.DbContract.IdColumn;
import org.hisp.dhis.client.sdk.models.common.State;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.List;

public interface TrackedEntityDataValueStore extends DataStore<TrackedEntityDataValue> {

    List<TrackedEntityDataValue> query(String eventUid);
}
