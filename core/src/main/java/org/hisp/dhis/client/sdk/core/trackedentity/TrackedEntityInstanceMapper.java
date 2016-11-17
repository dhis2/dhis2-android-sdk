package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.AbsMapper;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInstanceTable.TrackedEntityInstanceColumns;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.common.State;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.io.IOException;

import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getLong;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getString;

public class TrackedEntityInstanceMapper extends AbsMapper<TrackedEntityInstance> {

    TrackedEntityInstanceMapper() {
        // explicit constructor
    }

    @Override
    public Uri getContentUri() {
        return TrackedEntityInstanceTable.CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(TrackedEntityInstanceTable.CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return TrackedEntityInstanceTable.PROJECTION;
    }

    @Override
    public ContentValues toContentValues(TrackedEntityInstance trackedEntityInstance) throws IOException {
        if (!trackedEntityInstance.isValid()) {
            throw new IllegalArgumentException("Tracked entity instance is not valid");
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(TrackedEntityInstanceColumns.COLUMN_ID, trackedEntityInstance.id());
        contentValues.put(TrackedEntityInstanceColumns.COLUMN_UID, trackedEntityInstance.uid());
        contentValues.put(TrackedEntityInstanceColumns.COLUMN_CREATED, BaseIdentifiableObject.DATE_FORMAT.format(trackedEntityInstance.created()));
        contentValues.put(TrackedEntityInstanceColumns.COLUMN_LAST_UPDATED, trackedEntityInstance.lastUpdated() != null ? BaseIdentifiableObject.DATE_FORMAT.format(trackedEntityInstance.lastUpdated()) : null);
        contentValues.put(TrackedEntityInstanceColumns.COLUMN_ORGANISATION_UNIT, trackedEntityInstance.organisationUnit());
        contentValues.put(TrackedEntityInstanceColumns.COLUMN_STATE, trackedEntityInstance.state().toString());

        return contentValues;
    }

    @Override
    public TrackedEntityInstance toModel(Cursor cursor) {
        TrackedEntityInstance trackedEntityInstance = null;

        try {
            trackedEntityInstance = TrackedEntityInstance.builder()
                    .id(getLong(cursor, TrackedEntityInstanceColumns.COLUMN_ID))
                    .uid(getString(cursor, TrackedEntityInstanceColumns.COLUMN_UID))
                    .organisationUnit(getString(cursor, TrackedEntityInstanceColumns.COLUMN_ORGANISATION_UNIT))
                    .state(State.valueOf(getString(cursor, TrackedEntityInstanceColumns.COLUMN_STATE)))
                    .created(BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, TrackedEntityInstanceColumns.COLUMN_CREATED)))
                    .lastUpdated(getString(cursor, TrackedEntityInstanceColumns.COLUMN_LAST_UPDATED) != null ? BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, TrackedEntityInstanceColumns.COLUMN_LAST_UPDATED)) : null)
                    .build();

        } catch (java.text.ParseException e) {
            throw new IllegalArgumentException(e);
        }
        return trackedEntityInstance;
    }
}
