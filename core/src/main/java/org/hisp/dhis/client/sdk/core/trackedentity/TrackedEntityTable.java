package org.hisp.dhis.client.sdk.core.trackedentity;

import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.DbContract;
import org.hisp.dhis.client.sdk.core.commons.database.DbUtils;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

public interface TrackedEntityTable {
    interface TrackedEntityColumns extends DbContract.NameableColumns {
        String TABLE_NAME = "trackedEntities";
    }

    Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(TrackedEntityColumns.TABLE_NAME).build();

    String TRACKED_ENTITIES = TrackedEntityColumns.TABLE_NAME;
    String TRACKED_ENTITY_ID = TrackedEntityColumns.TABLE_NAME + "/#";

    String CONTENT_TYPE = DbUtils.getContentType(TrackedEntity.class);
    String CONTENT_ITEM_TYPE = DbUtils.getContentItemType(TrackedEntity.class);

    String[] PROJECTION = new String[]{
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
    String CREATE_TABLE_TRACKED_ENTITIES = "CREATE TABLE IF NOT EXISTS " +
            TrackedEntityColumns.TABLE_NAME + " (" +
            TrackedEntityColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrackedEntityColumns.COLUMN_UID + " TEXT UNIQUE NOT NULL ON CONFLICT REPLACE," +
            TrackedEntityColumns.COLUMN_CODE + " TEXT," +
            TrackedEntityColumns.COLUMN_CREATED + " TEXT NOT NULL," +
            TrackedEntityColumns.COLUMN_LAST_UPDATED + " TEXT NOT NULL," +
            TrackedEntityColumns.COLUMN_NAME + " TEXT," +
            TrackedEntityColumns.COLUMN_DISPLAY_NAME + " TEXT," +
            TrackedEntityColumns.COLUMN_SHORT_NAME + "TEXT," +
            TrackedEntityColumns.COLUMN_DISPLAY_SHORT_NAME + "TEXT," +
            TrackedEntityColumns.COLUMN_DESCRIPTION + "TEXT," +
            TrackedEntityColumns.COLUMN_DISPLAY_DESCRIPTION + "TEXT )";

    String DROP_TABLE_TRACKED_ENTITIES = "DROP TABLE IF EXISTS " +
            TrackedEntityColumns.TABLE_NAME;
}
