package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.DbContract;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityStore.TrackedEntityColumns;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

import java.text.ParseException;

public class TrackedEntityMapper implements Mapper<TrackedEntity> {
    private static Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(TrackedEntityColumns.TABLE_NAME).build();

    private static final String[] PROJECTION = new String[]{
            TrackedEntityColumns.COLUMN_ID,
            TrackedEntityColumns.COLUMN_UID,
            TrackedEntityColumns.COLUMN_CODE,
            TrackedEntityColumns.COLUMN_CREATED,
            TrackedEntityColumns.COLUMN_LAST_UPDATED,
            TrackedEntityColumns.COLUMN_NAME,
            TrackedEntityColumns.COLUMN_DISPLAY_NAME,
            TrackedEntityColumns.COLUMN_SHORT_NAME,
            TrackedEntityColumns.COLUMN_DISPLAY_SHORT_NAME,
            TrackedEntityColumns.COLUMN_DESCRIPTION,
            TrackedEntityColumns.COLUMN_DISPLAY_DESCRIPTION,
    };

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_UID = 1;
    private static final int COLUMN_CODE = 2;
    private static final int COLUMN_CREATED = 3;
    private static final int COLUMN_LAST_UPDATED = 4;
    private static final int COLUMN_NAME = 5;
    private static final int COLUMN_DISPLAY_NAME = 6;
    private static final int COLUMN_SHORT_NAME = 7;
    private static final int COLUMN_DISPLAY_SHORT_NAME = 8;
    private static final int COLUMN_DESCRIPTION = 9;
    private static final int COLUMN_DISPLAY_DESCRIPTION = 10;


    public TrackedEntityMapper() {
        // Explicit empty constructor
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

        trackedEntity.setId(cursor.getInt(COLUMN_ID));
        trackedEntity.setUid(cursor.getString(COLUMN_UID));
        trackedEntity.setCode(cursor.getString(COLUMN_CODE));
        trackedEntity.setName(cursor.getString(COLUMN_NAME));
        trackedEntity.setDisplayName(cursor.getString(COLUMN_DISPLAY_NAME));
        trackedEntity.setShortName(cursor.getString(COLUMN_SHORT_NAME));
        trackedEntity.setDisplayShortName(cursor.getString(COLUMN_DISPLAY_SHORT_NAME));
        trackedEntity.setDescription(cursor.getString(COLUMN_DESCRIPTION));
        trackedEntity.setDisplayDescription(cursor.getString(COLUMN_DISPLAY_DESCRIPTION));

        try {
            trackedEntity.setCreated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(cursor.getString(COLUMN_CREATED)));
            trackedEntity.setLastUpdated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(cursor.getString(COLUMN_LAST_UPDATED)));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return trackedEntity;
    }
}
