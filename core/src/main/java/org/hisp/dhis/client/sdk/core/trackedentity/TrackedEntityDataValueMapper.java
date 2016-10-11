package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.DbContract;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.models.common.State;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getInt;
import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getString;

public class TrackedEntityDataValueMapper implements Mapper<TrackedEntityDataValue> {
    public interface TrackedEntityDataValueColumns extends DbContract.IdColumn, DbContract.StateColumn {
        String TABLE_NAME = "trackedEntityDataValues";

        String COLUMN_DATA_ELEMENT = "dataElement";
        String COLUMN_EVENT = "event";
        String COLUMN_STORED_BY = "storedBy";
        String COLUMN_VALUE = "value";
    }

    private static Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(TrackedEntityDataValueColumns.TABLE_NAME).build();

    public static final String TRACKED_ENTITY_DATA_VALUES = TrackedEntityDataValueColumns.TABLE_NAME;
    public static final String TRACKED_ENTITY_DATA_VALUE_ID = TrackedEntityDataValueColumns.TABLE_NAME + "/#";

    public static String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/org.hisp.dhis.models.TrackedEntityDataValue";
    public static String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "/org.hisp.dhis.models.TrackedEntityDataValue";

    private static final String[] PROJECTION = new String[]{
            TrackedEntityDataValueColumns.COLUMN_ID,
            TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT,
            TrackedEntityDataValueColumns.COLUMN_EVENT,
            TrackedEntityDataValueColumns.COLUMN_STORED_BY,
            TrackedEntityDataValueColumns.COLUMN_VALUE,
            TrackedEntityDataValueColumns.COLUMN_STATE

    };

    public TrackedEntityDataValueMapper() {
        // explicit constructor
    }

    @Override
    public Uri getContentUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return PROJECTION;
    }

    @Override
    public ContentValues toContentValues(TrackedEntityDataValue trackedEntityDataValue) {
        TrackedEntityDataValue.validate(trackedEntityDataValue);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TrackedEntityDataValueColumns.COLUMN_ID, trackedEntityDataValue.getId());
        contentValues.put(TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT, trackedEntityDataValue.getDataElement());
        contentValues.put(TrackedEntityDataValueColumns.COLUMN_EVENT, trackedEntityDataValue.getEventUid());
        contentValues.put(TrackedEntityDataValueColumns.COLUMN_STORED_BY, trackedEntityDataValue.getStoredBy());
        contentValues.put(TrackedEntityDataValueColumns.COLUMN_VALUE, trackedEntityDataValue.getValue());
        contentValues.put(TrackedEntityDataValueColumns.COLUMN_STATE, trackedEntityDataValue.getState().toString());

        return contentValues;
    }

    @Override
    public TrackedEntityDataValue toModel(Cursor cursor) {
        TrackedEntityDataValue trackedEntityDataValue = new TrackedEntityDataValue();
        trackedEntityDataValue.setId(getInt(cursor, TrackedEntityDataValueColumns.COLUMN_ID));
        trackedEntityDataValue.setDataElement(getString(cursor, TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT));
        trackedEntityDataValue.setEventUid(getString(cursor, TrackedEntityDataValueColumns.COLUMN_EVENT));
        trackedEntityDataValue.setStoredBy(getString(cursor, TrackedEntityDataValueColumns.COLUMN_STORED_BY));
        trackedEntityDataValue.setValue(getString(cursor, TrackedEntityDataValueColumns.COLUMN_VALUE));
        trackedEntityDataValue.setState(State.valueOf(getString(cursor, TrackedEntityDataValueColumns.COLUMN_STATE)));

        return trackedEntityDataValue;
    }
}
