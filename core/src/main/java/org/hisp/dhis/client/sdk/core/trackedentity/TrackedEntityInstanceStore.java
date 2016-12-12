package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.commons.database.DataStore;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.List;

public interface TrackedEntityInstanceStore extends DataStore<TrackedEntityInstance> {
    List<TrackedEntityInstance> query(String organisationUnitUid);
    TrackedEntityInstance queryByUid(String trackedEntityInstanceUid);

    List<TrackedEntityInstance> queryByTrackedEntityUid(String trackedEntityUid);
}
