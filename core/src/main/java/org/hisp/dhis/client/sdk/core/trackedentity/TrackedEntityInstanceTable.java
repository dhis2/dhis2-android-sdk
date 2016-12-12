package org.hisp.dhis.client.sdk.core.trackedentity;

import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.DbContract;
import org.hisp.dhis.client.sdk.core.commons.database.DbContract.IdColumn;
import org.hisp.dhis.client.sdk.core.commons.database.DbContract.StateColumn;
import org.hisp.dhis.client.sdk.core.commons.database.DbContract.TimeStampColumns;
import org.hisp.dhis.client.sdk.core.commons.database.DbContract.UidColumn;
import org.hisp.dhis.client.sdk.core.commons.database.DbUtils;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

public interface TrackedEntityInstanceTable {
    interface TrackedEntityInstanceColumns extends IdColumn, UidColumn, StateColumn, TimeStampColumns {
        String TABLE_NAME = "trackedEntityInstances";

        String COLUMN_ORGANISATION_UNIT = "organisationUnit";
        String COLUMN_TRACKED_ENTITY = "trackedEntity";
    }

    String CREATE_TABLE_TRACKED_ENTITY_INSTANCES = "CREATE TABLE IF NOT EXISTS " +
            TrackedEntityInstanceColumns.TABLE_NAME + " (" +
            TrackedEntityInstanceColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrackedEntityInstanceColumns.COLUMN_UID + " TEXT UNIQUE NOT NULL ON CONFLICT REPLACE," +
            TrackedEntityInstanceColumns.COLUMN_CREATED + " TEXT NOT NULL," +
            TrackedEntityInstanceColumns.COLUMN_LAST_UPDATED + " TEXT," +
            TrackedEntityInstanceColumns.COLUMN_TRACKED_ENTITY + " TEXT NOT NULL," +
            TrackedEntityInstanceColumns.COLUMN_STATE + " TEXT," +
            TrackedEntityInstanceColumns.COLUMN_ORGANISATION_UNIT + " TEXT )";

    String DROP_TABLE_TRACKED_ENTITY_INSTANCES = "DROP TABLE IF EXISTS " +
            TrackedEntityInstanceColumns.TABLE_NAME;

    Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(TrackedEntityInstanceColumns.TABLE_NAME).build();

    String TRACKED_ENTITY_INSTANCES = TrackedEntityInstanceColumns.TABLE_NAME;
    String TRACKED_ENTITY_INSTANCE_ID = TrackedEntityInstanceColumns.TABLE_NAME + "/#";

    String CONTENT_TYPE = DbUtils.getContentType(TrackedEntityInstance.class);
    String CONTENT_ITEM_TYPE = DbUtils.getContentItemType(TrackedEntityInstance.class);

    String[] PROJECTION = new String[]{
            TrackedEntityInstanceColumns.COLUMN_ID,
            TrackedEntityInstanceColumns.COLUMN_UID,
            TrackedEntityInstanceColumns.COLUMN_CREATED,
            TrackedEntityInstanceColumns.COLUMN_LAST_UPDATED,
            TrackedEntityInstanceColumns.COLUMN_TRACKED_ENTITY,
            TrackedEntityInstanceColumns.COLUMN_STATE,
            TrackedEntityInstanceColumns.COLUMN_ORGANISATION_UNIT
    };

}
