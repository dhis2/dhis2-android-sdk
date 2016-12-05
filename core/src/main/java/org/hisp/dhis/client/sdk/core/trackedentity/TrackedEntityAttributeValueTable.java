package org.hisp.dhis.client.sdk.core.trackedentity;

import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.DbContract;
import org.hisp.dhis.client.sdk.core.commons.database.DbUtils;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInstanceTable.TrackedEntityInstanceColumns;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;

public interface TrackedEntityAttributeValueTable {
    interface TrackedEntityAttributeValueColumns extends DbContract.IdColumn, DbContract.StateColumn {
        String TABLE_NAME = "trackedEntityAttributeValues";

        String COLUMN_TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
        String COLUMN_TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";
        String COLUMN_VALUE = "value";
    }

    String CREATE_TABLE_TRACKED_ENTITY_ATTRIBUTE_VALUES = "CREATE TABLE IF NOT EXISTS " +
            TrackedEntityAttributeValueColumns.TABLE_NAME + " (" +
            TrackedEntityAttributeValueColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_ATTRIBUTE + " TEXT NOT NULL," +
            TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_INSTANCE + " TEXT NOT NULL," +
            TrackedEntityAttributeValueColumns.COLUMN_VALUE + " TEXT," +
            TrackedEntityAttributeValueColumns.COLUMN_STATE + " TEXT," +
            "FOREIGN KEY " + "(" +
            TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_INSTANCE + ")" +
            "REFERENCES " + TrackedEntityInstanceColumns.TABLE_NAME + "(" + TrackedEntityInstanceColumns.COLUMN_UID + ") " +
            "UNIQUE (" + TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_INSTANCE + ", " + TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_ATTRIBUTE + ")" +
            " ON CONFLICT REPLACE " +
            ");";

    String DROP_TABLE_TRACKED_ENTITY_ATTRIBUTE_VALUES = "DROP TABLE IF EXISTS " +
            TrackedEntityAttributeValueColumns.TABLE_NAME;

    Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(TrackedEntityAttributeValueColumns.TABLE_NAME).build();

    String TRACKED_ENTITY_ATTRIBUTE_VALUES = TrackedEntityAttributeValueColumns.TABLE_NAME;
    String TRACKED_ENTITY_ATTRIBUTE_VALUE_ID = TrackedEntityAttributeValueColumns.TABLE_NAME + "/#";

    String CONTENT_TYPE = DbUtils.getContentType(TrackedEntityAttributeValue.class);
    String CONTENT_ITEM_TYPE = DbUtils.getContentItemType(TrackedEntityAttributeValue.class);

    String[] PROJECTION = new String[]{
            TrackedEntityAttributeValueColumns.COLUMN_ID,
            TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_ATTRIBUTE,
            TrackedEntityAttributeValueColumns.COLUMN_TRACKED_ENTITY_INSTANCE,
            TrackedEntityAttributeValueColumns.COLUMN_VALUE,
            TrackedEntityAttributeValueColumns.COLUMN_STATE
    };

}
