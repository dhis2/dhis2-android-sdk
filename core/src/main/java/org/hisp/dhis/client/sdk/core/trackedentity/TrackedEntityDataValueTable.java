package org.hisp.dhis.client.sdk.core.trackedentity;

import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.DbContract;
import org.hisp.dhis.client.sdk.core.commons.database.DbUtils;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

public interface TrackedEntityDataValueTable {
    interface TrackedEntityDataValueColumns extends DbContract.IdColumn, DbContract.StateColumn {
        String TABLE_NAME = "trackedEntityDataValues";

        String COLUMN_DATA_ELEMENT = "dataElement";
        String COLUMN_EVENT = "event";
        String COLUMN_STORED_BY = "storedBy";
        String COLUMN_VALUE = "value";
    }

    Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(TrackedEntityDataValueColumns.TABLE_NAME).build();

    String TRACKED_ENTITY_DATA_VALUES = TrackedEntityDataValueColumns.TABLE_NAME;
    String TRACKED_ENTITY_DATA_VALUE_ID = TrackedEntityDataValueColumns.TABLE_NAME + "/#";

    String CONTENT_TYPE = DbUtils.getContentType(TrackedEntityDataValue.class);
    String CONTENT_ITEM_TYPE = DbUtils.getContentItemType(TrackedEntityDataValue.class);

    String[] PROJECTION = new String[]{
            TrackedEntityDataValueColumns.COLUMN_ID,
            TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT,
            TrackedEntityDataValueColumns.COLUMN_EVENT,
            TrackedEntityDataValueColumns.COLUMN_STORED_BY,
            TrackedEntityDataValueColumns.COLUMN_VALUE,
            TrackedEntityDataValueColumns.COLUMN_STATE

    };

    String CREATE_TABLE_TRACKED_ENTITY_DATA_VALUES = "CREATE TABLE IF NOT EXISTS " +
            TrackedEntityDataValueColumns.TABLE_NAME + " (" +
            TrackedEntityDataValueColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT + " TEXT NOT NULL," +
            TrackedEntityDataValueColumns.COLUMN_EVENT + " TEXT NOT NULL," +
            TrackedEntityDataValueColumns.COLUMN_STORED_BY + " TEXT NOT NULL," +
            TrackedEntityDataValueColumns.COLUMN_VALUE + " TEXT," +
            TrackedEntityDataValueColumns.COLUMN_STATE + " TEXT" + ")";

    String DROP_TABLE_TRACKED_ENTITY_DATA_VALUES = "DROP TABLE IF EXISTS " +
            TrackedEntityDataValueColumns.TABLE_NAME;
}
