package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentResolver;

import org.hisp.dhis.client.sdk.core.commons.database.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

class TrackedEntityStoreImpl extends AbsIdentifiableObjectStore<TrackedEntity> implements TrackedEntityStore {

    TrackedEntityStoreImpl(ContentResolver contentResolver) {
        super(contentResolver, new TrackedEntityMapper());
    }
}
