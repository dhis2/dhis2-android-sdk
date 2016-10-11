package org.hisp.dhis.client.sdk.core.user;

import android.content.ContentResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.models.user.User;

public class UserStoreImpl extends AbsIdentifiableObjectStore<User> implements UserStore {
    public static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " +
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

    public static final String DROP_TABLE_USERS = "DROP TABLE IF EXISTS " +
            UserColumns.TABLE_NAME;

    public UserStoreImpl(ContentResolver contentResolver, ObjectMapper objectMapper) {
        super(contentResolver, new UserMapper(objectMapper));
    }
}
