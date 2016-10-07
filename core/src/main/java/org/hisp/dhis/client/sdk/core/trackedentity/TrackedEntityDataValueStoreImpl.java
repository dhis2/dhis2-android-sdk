package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentResolver;

import org.hisp.dhis.client.sdk.core.commons.AbsDataStore;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

public class TrackedEntityDataValueStoreImpl extends AbsDataStore<TrackedEntityDataValue> implements TrackedEntityDataValueStoreI{
    public TrackedEntityDataValueStoreImpl(ContentResolver contentResolver, Mapper<TrackedEntityDataValue> mapper) {
        super(contentResolver, mapper);
    }
}
