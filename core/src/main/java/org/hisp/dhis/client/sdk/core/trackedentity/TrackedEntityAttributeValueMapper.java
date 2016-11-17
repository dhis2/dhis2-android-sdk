package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.AbsMapper;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeValueTable.TrackedEntityAttributeValueColumns;
import org.hisp.dhis.client.sdk.models.common.State;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;

import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getLong;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getString;

class TrackedEntityAttributeValueMapper extends AbsMapper<TrackedEntityAttributeValue> {

    TrackedEntityAttributeValueMapper() {
        // explicit constructor
    }

    @Override
    public Uri getContentUri() {
        return TrackedEntityAttributeValueTable.CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(TrackedEntityAttributeValueTable.CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return TrackedEntityAttributeValueTable.PROJECTION;
    }

    @Override
    public ContentValues toContentValues(TrackedEntityAttributeValue trackedEntityAttributeValue) {
        if (!trackedEntityAttributeValue.isValid()) {
            throw new IllegalArgumentException("TrackedEntityAttributeValue is not valid");
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(TrackedEntityAttributeValueColumns.COLUMN_ID, trackedEntityAttributeValue.id());
        contentValues.put(TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_ATTRIBUTE, trackedEntityAttributeValue.trackedEntityAttributeUid());
        contentValues.put(TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_INSTANCE, trackedEntityAttributeValue.trackedEntityInstanceUid());
        contentValues.put(TrackedEntityAttributeValueColumns.COLUMN_VALUE, trackedEntityAttributeValue.value());
        contentValues.put(TrackedEntityAttributeValueColumns.COLUMN_STATE, trackedEntityAttributeValue.state().toString());

        return contentValues;
    }

    @Override
    public TrackedEntityAttributeValue toModel(Cursor cursor) {
        TrackedEntityAttributeValue trackedEntityAttributeValue = TrackedEntityAttributeValue.builder()
                .id(getLong(cursor, TrackedEntityAttributeValueColumns.COLUMN_ID))
                .trackedEntityAttributeUid(getString(cursor, TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_ATTRIBUTE))
                .trackedEntityInstanceUid(getString(cursor, TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_INSTANCE))
                .value(getString(cursor, TrackedEntityAttributeValueColumns.COLUMN_VALUE))
                .state(State.valueOf(getString(cursor, TrackedEntityAttributeValueColumns.COLUMN_STATE)))
                .build();

        return trackedEntityAttributeValue;
    }
}
