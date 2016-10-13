package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueTable.TrackedEntityDataValueColumns;
import org.hisp.dhis.client.sdk.models.common.State;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getInt;
import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getString;

public class TrackedEntityDataValueMapper implements Mapper<TrackedEntityDataValue> {

    public TrackedEntityDataValueMapper() {
        // explicit constructor
    }

    @Override
    public Uri getContentUri() {
        return TrackedEntityDataValueTable.CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(TrackedEntityDataValueTable.CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return TrackedEntityDataValueTable.PROJECTION;
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
