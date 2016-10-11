package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.DbContract;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

import java.text.ParseException;

import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getInt;
import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getString;

public class TrackedEntityMapper implements Mapper<TrackedEntity> {
    public interface TrackedEntityColumns extends DbContract.NameableColumns {
        String TABLE_NAME = "trackedEntities";
    }

    private static Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(TrackedEntityColumns.TABLE_NAME).build();

    public static final String TRACKED_ENTITIES = TrackedEntityColumns.TABLE_NAME;
    public static final String TRACKED_ENTITY_ID = TrackedEntityColumns.TABLE_NAME + "/#";

    public static String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/org.hisp.dhis.models.TrackedEntity";
    public static String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "/org.hisp.dhis.models.TrackedEntity";

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
