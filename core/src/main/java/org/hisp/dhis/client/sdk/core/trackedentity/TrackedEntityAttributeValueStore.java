package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.commons.database.DataStore;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;

import java.util.List;

public interface TrackedEntityAttributeValueStore extends DataStore<TrackedEntityAttributeValue> {
    List<TrackedEntityAttributeValue> query(String trackedEntityInstanceUid);
    List<TrackedEntityAttributeValue> query(String trackedEntityInstanceUid, String programUid);
}
