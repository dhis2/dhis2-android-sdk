package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

public class TrackedEntityStoreImpl extends AbsIdentifiableObjectStore<TrackedEntity> implements TrackedEntityStore {

    public static final String CREATE_TABLE_TRACKED_ENTITIES = "CREATE TABLE IF NOT EXISTS " +
            TrackedEntityColumns.TABLE_NAME + " (" +
            TrackedEntityColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrackedEntityColumns.COLUMN_UID + " TEXT NOT NULL," +
            TrackedEntityColumns.COLUMN_CODE + " TEXT," +
            TrackedEntityColumns.COLUMN_CREATED + " TEXT NOT NULL," +
            TrackedEntityColumns.COLUMN_LAST_UPDATED + " TEXT NOT NULL," +
            TrackedEntityColumns.COLUMN_NAME + " TEXT," +
            TrackedEntityColumns.COLUMN_DISPLAY_NAME + " TEXT," +
            TrackedEntityColumns.COLUMN_SHORT_NAME + "TEXT," +
            TrackedEntityColumns.COLUMN_DISPLAY_SHORT_NAME + "TEXT," +
            TrackedEntityColumns.COLUMN_DESCRIPTION + "TEXT," +
            TrackedEntityColumns.COLUMN_DISPLAY_DESCRIPTION + "TEXT" +
            " UNIQUE " + "(" + TrackedEntityColumns.COLUMN_UID + ")" + " ON CONFLICT REPLACE" + " )";

    public static final String DROP_TABLE_TRACKED_ENTITIES = "DROP TABLE IF EXISTS " +
            TrackedEntityColumns.TABLE_NAME;

    public TrackedEntityStoreImpl(ContentResolver contentResolver) {
        super(contentResolver, new TrackedEntityMapper());
    }


}
