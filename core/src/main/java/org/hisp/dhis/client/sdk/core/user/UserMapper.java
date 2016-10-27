package org.hisp.dhis.client.sdk.core.user;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.database.Mapper;
import org.hisp.dhis.client.sdk.models.user.User;

import java.io.IOException;

import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getLong;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getString;
import static org.hisp.dhis.client.sdk.core.user.UserTable.UserColumns;

class UserMapper implements Mapper<User> {
    private final ObjectMapper objectMapper;

    UserMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Uri getContentUri() {
        return UserTable.CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(UserTable.CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return UserTable.PROJECTION;
    }

    @Override
    public ContentValues toContentValues(User user) {
        if (!user.isValid()) {
            throw new IllegalArgumentException("User is not valid");
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserColumns.COLUMN_ID, user.id());
        contentValues.put(UserColumns.COLUMN_UID, user.uid());
        contentValues.put(UserColumns.COLUMN_CODE, user.code());
        contentValues.put(UserColumns.COLUMN_CREATED, user.created().toString());
        contentValues.put(UserColumns.COLUMN_LAST_UPDATED, user.lastUpdated().toString());
        contentValues.put(UserColumns.COLUMN_NAME, user.name());
        contentValues.put(UserColumns.COLUMN_DISPLAY_NAME, user.displayName());

        // try to serialize the user into JSON blob
        try {
            contentValues.put(UserColumns.COLUMN_BODY, objectMapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
        return contentValues;
    }

    @Override
    public User toModel(Cursor cursor) {
        User user;
        // trying to deserialize the JSON blob into User instance
        try {
            user = objectMapper.readValue(getString(cursor, UserColumns.COLUMN_BODY), User.class);
            user.toBuilder().id(getLong(cursor, UserColumns.COLUMN_ID));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        return user;
    }
}
