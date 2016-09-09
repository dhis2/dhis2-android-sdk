package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.common.persistence.IdentifiableObjectStore;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

public interface TrackedEntityStore extends IdentifiableObjectStore<TrackedEntity> {
    TrackedEntity queryByUid(String uid);
}
