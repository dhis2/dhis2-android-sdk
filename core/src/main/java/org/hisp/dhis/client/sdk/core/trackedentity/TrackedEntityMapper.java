package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.AbsMapper;
import org.hisp.dhis.client.sdk.core.commons.database.Mapper;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityTable.TrackedEntityColumns;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

import java.text.ParseException;

import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getLong;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getString;

class TrackedEntityMapper extends AbsMapper<TrackedEntity> {

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
        if (!trackedEntity.isValid()) {
            throw new IllegalArgumentException("TrackedEntity is not valid");

        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(TrackedEntityColumns.COLUMN_ID, trackedEntity.id());
        contentValues.put(TrackedEntityColumns.COLUMN_UID, trackedEntity.uid());
        contentValues.put(TrackedEntityColumns.COLUMN_CODE, trackedEntity.code());
        contentValues.put(TrackedEntityColumns.COLUMN_CREATED, BaseIdentifiableObject.DATE_FORMAT.format(trackedEntity.created()));
        contentValues.put(TrackedEntityColumns.COLUMN_LAST_UPDATED, BaseIdentifiableObject.DATE_FORMAT.format(trackedEntity.lastUpdated()));
        contentValues.put(TrackedEntityColumns.COLUMN_NAME, trackedEntity.name());
        contentValues.put(TrackedEntityColumns.COLUMN_DISPLAY_NAME, trackedEntity.displayName());
        contentValues.put(TrackedEntityColumns.COLUMN_SHORT_NAME, trackedEntity.shortName());
        contentValues.put(TrackedEntityColumns.COLUMN_DISPLAY_SHORT_NAME, trackedEntity.displayShortName());
        contentValues.put(TrackedEntityColumns.COLUMN_DESCRIPTION, trackedEntity.description());
        contentValues.put(TrackedEntityColumns.COLUMN_DISPLAY_DESCRIPTION, trackedEntity.displayDescription());

        return contentValues;
    }

    @Override
    public TrackedEntity toModel(Cursor cursor) {

        TrackedEntity trackedEntity;

        try {
            trackedEntity = TrackedEntity.builder()
                    .id(getLong(cursor, TrackedEntityColumns.COLUMN_ID))
                    .uid(getString(cursor, TrackedEntityColumns.COLUMN_UID))
                    .code(getString(cursor, TrackedEntityColumns.COLUMN_CODE))
                    .name(getString(cursor, TrackedEntityColumns.COLUMN_NAME))
                    .displayName(getString(cursor, TrackedEntityColumns.COLUMN_DISPLAY_NAME))
                    .created(BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, TrackedEntityColumns.COLUMN_CREATED)))
                    .lastUpdated(BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, TrackedEntityColumns.COLUMN_LAST_UPDATED)))
                    .shortName(getString(cursor, TrackedEntityColumns.COLUMN_SHORT_NAME))
                    .displayShortName(getString(cursor, TrackedEntityColumns.COLUMN_DISPLAY_SHORT_NAME))
                    .description(getString(cursor, TrackedEntityColumns.COLUMN_DESCRIPTION))
                    .displayDescription(getString(cursor, TrackedEntityColumns.COLUMN_DISPLAY_DESCRIPTION))
                    .build();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return trackedEntity;
    }
}
