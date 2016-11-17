package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentResolver;
import android.database.Cursor;

import org.hisp.dhis.client.sdk.core.commons.database.AbsDataStore;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;

import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

//TODO: Review if we need a method List<TrackedEntityAttributeValue> query(String trackedEntityInstanceUid, String programUid)
class TrackedEntityAttributeValueStoreImpl extends AbsDataStore<TrackedEntityAttributeValue> implements TrackedEntityAttributeValueStore {

    TrackedEntityAttributeValueStoreImpl(ContentResolver contentResolver) {
        super(contentResolver, new TrackedEntityAttributeValueMapper());
    }

    @Override
    public List<TrackedEntityAttributeValue> query(String trackedEntityInstanceUid) {
        isNull(trackedEntityInstanceUid, "trackedEntityInstance uid must not be null");

        final String selection = TrackedEntityAttributeValueTable.TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_INSTANCE + " = ? ";
        final String[] selectionArgs = new String[]{trackedEntityInstanceUid};

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);

        return toModels(cursor);
    }
}
