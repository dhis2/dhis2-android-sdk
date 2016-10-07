package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.DbContract;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueStoreI.TrackedEntityDataValueColumns;
import org.hisp.dhis.client.sdk.models.common.State;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

public class TrackedEntityDataValueMapper implements Mapper<TrackedEntityDataValue> {

    private static Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(TrackedEntityDataValueColumns.TABLE_NAME).build();

    private static final String[] PROJECTION = new String[]{
            TrackedEntityDataValueColumns.COLUMN_ID,
            TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT,
            TrackedEntityDataValueColumns.COLUMN_EVENT,
            TrackedEntityDataValueColumns.COLUMN_STORED_BY,
            TrackedEntityDataValueColumns.COLUMN_VALUE,
            TrackedEntityDataValueColumns.COLUMN_STATE

    };

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_DATA_ELEMENT = 1;
    private static final int COLUMN_EVENT = 2;
    private static final int COLUMN_STORED_BY = 3;
    private static final int COLUMN_VALUE = 4;
    private static final int COLUMN_STATE = 5;

    public TrackedEntityDataValueMapper() {
        // explicit constructor
    }

    @Override
    public Uri getContentUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI,id);
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

        trackedEntityDataValue.setId(cursor.getInt(COLUMN_ID));
        trackedEntityDataValue.setDataElement(cursor.getString(COLUMN_DATA_ELEMENT));
        trackedEntityDataValue.setEventUid(cursor.getString(COLUMN_EVENT));
        trackedEntityDataValue.setStoredBy(cursor.getString(COLUMN_STORED_BY));
        trackedEntityDataValue.setValue(cursor.getString(COLUMN_VALUE));
        trackedEntityDataValue.setState(State.valueOf(cursor.getString(COLUMN_STATE)));


        return trackedEntityDataValue;
    }
}
