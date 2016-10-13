package org.hisp.dhis.client.sdk.core.user;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.user.User;


import java.io.IOException;
import java.text.ParseException;

import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getInt;
import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getString;
import static org.hisp.dhis.client.sdk.core.user.UserTable.UserColumns;

public class UserMapper implements Mapper<User> {
    private final ObjectMapper objectMapper;

    public UserMapper(ObjectMapper objectMapper) {
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
        User.validate(user);

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserColumns.COLUMN_ID, user.getId());
        contentValues.put(UserColumns.COLUMN_UID, user.getUid());
        contentValues.put(UserColumns.COLUMN_CODE, user.getCode());
        contentValues.put(UserColumns.COLUMN_CREATED, user.getCreated().toString());
        contentValues.put(UserColumns.COLUMN_LAST_UPDATED, user.getLastUpdated().toString());
        contentValues.put(UserColumns.COLUMN_NAME, user.getName());
        contentValues.put(UserColumns.COLUMN_DISPLAY_NAME, user.getDisplayName());

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
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        user.setId(getInt(cursor, UserColumns.COLUMN_ID));
        user.setUid(getString(cursor, UserColumns.COLUMN_UID));
        user.setCode(getString(cursor, UserColumns.COLUMN_CODE));
        user.setName(getString(cursor, UserColumns.COLUMN_NAME));
        user.setDisplayName(getString(cursor, UserColumns.COLUMN_DISPLAY_NAME));

        try {
            user.setCreated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(getString(cursor, UserColumns.COLUMN_CREATED)));
            user.setLastUpdated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(getString(cursor, UserColumns.COLUMN_LAST_UPDATED)));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return user;
    }
}
