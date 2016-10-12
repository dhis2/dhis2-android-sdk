package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentResolver;

import org.hisp.dhis.client.sdk.core.commons.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

public class TrackedEntityStoreImpl extends AbsIdentifiableObjectStore<TrackedEntity> implements TrackedEntityStore {

    public TrackedEntityStoreImpl(ContentResolver contentResolver) {
        super(contentResolver, new TrackedEntityMapper());
    }
}
