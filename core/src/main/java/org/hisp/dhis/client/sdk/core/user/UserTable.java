package org.hisp.dhis.client.sdk.core.user;

import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.DbContract;
import org.hisp.dhis.client.sdk.core.commons.database.DbUtils;
import org.hisp.dhis.client.sdk.models.user.User;

public interface UserTable {
    interface UserColumns extends DbContract.IdentifiableColumns, DbContract.BodyColumn {
        String TABLE_NAME = "users";
    }

    Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(UserColumns.TABLE_NAME).build();
    String USERS = UserColumns.TABLE_NAME;
    String USER_ID = UserColumns.TABLE_NAME + "/#";

    String CONTENT_TYPE = DbUtils.getContentType(User.class);
    String CONTENT_ITEM_TYPE = DbUtils.getContentItemType(User.class);

    String[] PROJECTION = new String[]{
            UserColumns.COLUMN_ID,
            UserColumns.COLUMN_UID,
            UserColumns.COLUMN_CODE,
            UserColumns.COLUMN_CREATED,
            UserColumns.COLUMN_LAST_UPDATED,
            UserColumns.COLUMN_NAME,
            UserColumns.COLUMN_DISPLAY_NAME,
            UserColumns.COLUMN_BODY
    };

    String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " +
            UserColumns.TABLE_NAME + " (" +
            UserColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserColumns.COLUMN_UID + " TEXT NOT NULL," +
            UserColumns.COLUMN_CODE + " TEXT," +
            UserColumns.COLUMN_CREATED + " TEXT NOT NULL," +
            UserColumns.COLUMN_LAST_UPDATED + " TEXT NOT NULL," +
            UserColumns.COLUMN_NAME + " TEXT," +
            UserColumns.COLUMN_DISPLAY_NAME + " TEXT," +
            UserColumns.COLUMN_BODY + " TEXT " +
            " UNIQUE " + "(" + UserColumns.COLUMN_UID + ")" + " ON CONFLICT REPLACE" + " )";

    String DROP_TABLE_USERS = "DROP TABLE IF EXISTS " +
            UserColumns.TABLE_NAME;
}
