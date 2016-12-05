package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.AbsMapper;
import org.hisp.dhis.client.sdk.core.commons.database.Mapper;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueTable.TrackedEntityDataValueColumns;
import org.hisp.dhis.client.sdk.models.common.State;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getLong;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getString;

class TrackedEntityDataValueMapper extends AbsMapper<TrackedEntityDataValue> {

    TrackedEntityDataValueMapper() {
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
        if (!trackedEntityDataValue.isValid()) {
            throw new IllegalArgumentException("TrackedEntityDataValue is not valid");
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(TrackedEntityDataValueColumns.COLUMN_ID, trackedEntityDataValue.id());
        contentValues.put(TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT, trackedEntityDataValue.dataElement());
        contentValues.put(TrackedEntityDataValueColumns.COLUMN_EVENT, trackedEntityDataValue.event());
        contentValues.put(TrackedEntityDataValueColumns.COLUMN_STORED_BY, trackedEntityDataValue.storedBy());
        contentValues.put(TrackedEntityDataValueColumns.COLUMN_VALUE, trackedEntityDataValue.value());
        contentValues.put(TrackedEntityDataValueColumns.COLUMN_STATE, trackedEntityDataValue.state().toString());

        return contentValues;
    }

    @Override
    public TrackedEntityDataValue toModel(Cursor cursor) {
        return TrackedEntityDataValue.builder()
                .id(getLong(cursor, TrackedEntityDataValueColumns.COLUMN_ID))
                .dataElement(getString(cursor, TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT))
                .event(getString(cursor, TrackedEntityDataValueColumns.COLUMN_EVENT))
                .storedBy(getString(cursor, TrackedEntityDataValueColumns.COLUMN_STORED_BY))
                .value(getString(cursor, TrackedEntityDataValueColumns.COLUMN_VALUE))
                .state(State.valueOf(getString(cursor, TrackedEntityDataValueColumns.COLUMN_STATE)))
                .build();
    }
}
