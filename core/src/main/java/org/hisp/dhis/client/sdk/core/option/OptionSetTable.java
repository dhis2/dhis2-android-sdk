package org.hisp.dhis.client.sdk.core.option;

import android.content.ContentResolver;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.DbContract;

public interface OptionSetTable {
    interface OptionSetColumns extends DbContract.IdColumn, DbContract.IdentifiableColumns, DbContract.VersionColumn, DbContract.BodyColumn {
        String TABLE_NAME = "optionSet";
    }

    Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(OptionSetColumns.TABLE_NAME).build();

    String OPTION_SETS = OptionSetColumns.TABLE_NAME;
    String OPTION_SET_ID = OptionSetColumns.TABLE_NAME + "/#";

    String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/org.hisp.dhis.models.OptionSet";
    String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "/org.hisp.dhis.models.OptionSet";

    String[] PROJECTION = new String[]{
            OptionSetColumns.COLUMN_ID,
            OptionSetColumns.COLUMN_UID,
            OptionSetColumns.COLUMN_CODE,
            OptionSetColumns.COLUMN_CREATED,
            OptionSetColumns.COLUMN_LAST_UPDATED,
            OptionSetColumns.COLUMN_NAME,
            OptionSetColumns.COLUMN_DISPLAY_NAME,
            OptionSetColumns.COLUMN_BODY
    };

    String CREATE_TABLE_OPTION_SETS = "CREATE TABLE IF NOT EXISTS " +
            OptionSetColumns.TABLE_NAME + " (" +
            OptionSetColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OptionSetColumns.COLUMN_UID + " TEXT NOT NULL," +
            OptionSetColumns.COLUMN_NAME + " TEXT," +
            OptionSetColumns.COLUMN_DISPLAY_NAME + " TEXT," +
            OptionSetColumns.COLUMN_CODE + " TEXT," +
            OptionSetColumns.COLUMN_CREATED + " TEXT NOT NULL," +
            OptionSetColumns.COLUMN_LAST_UPDATED + " TEXT NOT NULL," +
            OptionSetColumns.COLUMN_VERSION + " INTEGER NOT NULL," +
            OptionSetColumns.COLUMN_BODY + " TEXT NOT NULL" +
            " UNIQUE " + "(" + OptionSetColumns.COLUMN_UID + ")" + " ON CONFLICT REPLACE" + " )";

    String DROP_TABLE_OPTION_SETS = "DROP TABLE IF EXISTS " +
            OptionSetColumns.TABLE_NAME;
}
