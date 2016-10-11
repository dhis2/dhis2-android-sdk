package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentResolver;
import android.database.Cursor;

import org.hisp.dhis.client.sdk.core.commons.AbsDataStore;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueMapper.TrackedEntityDataValueColumns;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class TrackedEntityDataValueStoreImpl extends AbsDataStore<TrackedEntityDataValue> implements TrackedEntityDataValueStore {

    public static final String CREATE_TABLE_TRACKED_ENTITY_DATA_VALUES = "CREATE TABLE IF NOT EXISTS " +
            TrackedEntityDataValueColumns.TABLE_NAME + " (" +
            TrackedEntityDataValueColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT + " TEXT NOT NULL," +
            TrackedEntityDataValueColumns.COLUMN_EVENT + " TEXT NOT NULL," +
            TrackedEntityDataValueColumns.COLUMN_STORED_BY + " TEXT NOT NULL," +
            TrackedEntityDataValueColumns.COLUMN_VALUE + " TEXT," +
            TrackedEntityDataValueColumns.COLUMN_STATE + " TEXT" + ")";

    public static final String DROP_TABLE_TRACKED_ENTITY_DATA_VALUES = "DROP TABLE IF EXISTS " +
            TrackedEntityDataValueColumns.TABLE_NAME;

    public TrackedEntityDataValueStoreImpl(ContentResolver contentResolver, Mapper<TrackedEntityDataValue> mapper) {
        super(contentResolver, mapper);
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
