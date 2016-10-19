package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentResolver;
import android.database.Cursor;

import org.hisp.dhis.client.sdk.core.commons.database.AbsDataStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueTable.TrackedEntityDataValueColumns;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

class TrackedEntityDataValueStoreImpl extends AbsDataStore<TrackedEntityDataValue> implements TrackedEntityDataValueStore {

    TrackedEntityDataValueStoreImpl(ContentResolver contentResolver) {
        super(contentResolver, new TrackedEntityDataValueMapper());
    }

    @Override
    public List<TrackedEntityDataValue> query(String eventUid) {
        isNull(eventUid, "event uid must not be null");

        final String selection = TrackedEntityDataValueColumns.COLUMN_EVENT + " = ? ";
        final String[] selectionArgs = new String[]{eventUid};

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);

        return toModels(cursor);
    }
}
