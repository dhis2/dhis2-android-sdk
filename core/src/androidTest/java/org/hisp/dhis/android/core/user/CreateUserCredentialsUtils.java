package org.hisp.dhis.android.core.user;

import android.content.ContentValues;

public class CreateUserCredentialsUtils {

    public static final long ID = 1L;
    public static final String UID = "test_user_credentials_uid";
    public static final String NAME = "test_name";
    public static final String CODE = "test_code";
    public static final String DISPLAY_NAME = "test_display_name";
    public static final String CREATED = "test_created";
    public static final String LAST_UPDATED = "test_lastUpdated";
    public static final String USERNAME = "test_username";

    public static ContentValues create(long id, String uid, String user) {
        ContentValues userCredentials = new ContentValues();
        userCredentials.put(UserCredentialsModel.Columns.ID, id);
        userCredentials.put(UserCredentialsModel.Columns.UID, uid);
        userCredentials.put(UserCredentialsModel.Columns.CODE, CODE);
        userCredentials.put(UserCredentialsModel.Columns.NAME, NAME);
        userCredentials.put(UserCredentialsModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        userCredentials.put(UserCredentialsModel.Columns.CREATED, CREATED);
        userCredentials.put(UserCredentialsModel.Columns.LAST_UPDATED, LAST_UPDATED);
        userCredentials.put(UserCredentialsModel.Columns.USERNAME, USERNAME);
        userCredentials.put(UserCredentialsModel.Columns.USER, user);
        return userCredentials;
    }
}
