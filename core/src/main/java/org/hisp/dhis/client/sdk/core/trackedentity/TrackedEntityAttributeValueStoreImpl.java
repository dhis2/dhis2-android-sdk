package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentResolver;
import android.database.Cursor;

import org.hisp.dhis.client.sdk.core.commons.database.AbsDataStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueTable.TrackedEntityDataValueColumns;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

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

    @Override
    public List<TrackedEntityAttributeValue> query(String trackedEntityInstanceUid, String programUid) {
        isNull(trackedEntityInstanceUid, "trackedEntityInstance uid must not be null");
        isNull(programUid, "program uid must not be null");

        final String selection = TrackedEntityAttributeValueTable.TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_INSTANCE + " = ? AND " +
                TrackedEntityAttributeValueTable.TrackedEntityAttributeValueColumns.COLUMN_PROGRAM + " = ?"; problemet her er at det er jo ikke noe COLUMN_PROGRAM, så vet ikke om samme metode vil funke for query med standard selection args
        final String[] selectionArgs = new String[]{trackedEntityInstanceUid, programUid};

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);

        return toModels(cursor);
    }
}
