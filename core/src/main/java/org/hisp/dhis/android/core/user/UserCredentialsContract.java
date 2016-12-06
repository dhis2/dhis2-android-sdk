package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.commons.database.BaseIdentifiableObjectContract;

public final class UserCredentialsContract {
    public static final String USER_CREDENTIALS = "UserCredentials";

    public static final String CREATE_TABLE = "CREATE TABLE " + USER_CREDENTIALS + " (" +
            Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Columns.UID + " TEXT," +
            Columns.CODE + " TEXT," +
            Columns.NAME + " TEXT," +
            Columns.DISPLAY_NAME + " TEXT," +
            Columns.CREATED + " TEXT," +
            Columns.LAST_UPDATED + " TEXT," +
            Columns.USERNAME + " TEXT," +
            Columns.USER + " TEXT NOT NULL UNIQUE," +
            "FOREIGN KEY (" + Columns.USER + ") REFERENCES " + UserContract.USER +
            " (" + UserContract.Columns.UID + ") ON DELETE CASCADE" +
            ");";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + USER_CREDENTIALS;

    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String USERNAME = "username";
        String USER = "user";
    }
}
