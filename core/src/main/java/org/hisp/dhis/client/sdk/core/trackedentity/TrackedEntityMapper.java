package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.Mapper;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityTable.TrackedEntityColumns;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

import java.text.ParseException;

import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getInt;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getString;

class TrackedEntityMapper implements Mapper<TrackedEntity> {

    TrackedEntityMapper() {
        // Explicit empty constructor
    }

    @Override
    public Uri getContentUri() {
        return TrackedEntityTable.CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(TrackedEntityTable.CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return TrackedEntityTable.PROJECTION;
    }

    @Override
    public ContentValues toContentValues(TrackedEntity trackedEntity) {
        TrackedEntity.validate(trackedEntity);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TrackedEntityColumns.COLUMN_ID, trackedEntity.getId());
        contentValues.put(TrackedEntityColumns.COLUMN_UID, trackedEntity.getUid());
        contentValues.put(TrackedEntityColumns.COLUMN_CODE, trackedEntity.getCode());
        contentValues.put(TrackedEntityColumns.COLUMN_CREATED, trackedEntity.getCreated().toString());
        contentValues.put(TrackedEntityColumns.COLUMN_LAST_UPDATED, trackedEntity.getLastUpdated().toString());
        contentValues.put(TrackedEntityColumns.COLUMN_NAME, trackedEntity.getName());
        contentValues.put(TrackedEntityColumns.COLUMN_DISPLAY_NAME, trackedEntity.getDisplayName());
        contentValues.put(TrackedEntityColumns.COLUMN_SHORT_NAME, trackedEntity.getShortName());
        contentValues.put(TrackedEntityColumns.COLUMN_DISPLAY_SHORT_NAME, trackedEntity.getDisplayShortName());
        contentValues.put(TrackedEntityColumns.COLUMN_DESCRIPTION, trackedEntity.getDescription());
        contentValues.put(TrackedEntityColumns.COLUMN_DISPLAY_DESCRIPTION, trackedEntity.getDisplayDescription());

        return contentValues;
    }

    @Override
    public TrackedEntity toModel(Cursor cursor) {
        TrackedEntity trackedEntity = new TrackedEntity();

        trackedEntity.setId(getInt(cursor, TrackedEntityColumns.COLUMN_ID));
        trackedEntity.setUid(getString(cursor, TrackedEntityColumns.COLUMN_UID));
        trackedEntity.setCode(getString(cursor, TrackedEntityColumns.COLUMN_CODE));
        trackedEntity.setName(getString(cursor, TrackedEntityColumns.COLUMN_NAME));
        trackedEntity.setDisplayName(getString(cursor, TrackedEntityColumns.COLUMN_DISPLAY_NAME));
        trackedEntity.setShortName(getString(cursor, TrackedEntityColumns.COLUMN_SHORT_NAME));
        trackedEntity.setDisplayShortName(getString(cursor, TrackedEntityColumns.COLUMN_DISPLAY_SHORT_NAME));
        trackedEntity.setDescription(getString(cursor, TrackedEntityColumns.COLUMN_DESCRIPTION));
        trackedEntity.setDisplayDescription(getString(cursor, TrackedEntityColumns.COLUMN_DISPLAY_DESCRIPTION));

        try {
            trackedEntity.setCreated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(getString(cursor, TrackedEntityColumns.COLUMN_CREATED)));
            trackedEntity.setLastUpdated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(getString(cursor, TrackedEntityColumns.COLUMN_LAST_UPDATED)));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return trackedEntity;
    }
}
