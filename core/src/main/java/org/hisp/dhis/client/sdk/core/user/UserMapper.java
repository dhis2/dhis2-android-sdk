package org.hisp.dhis.client.sdk.core.user;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.DbContract;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.user.User;

import java.io.IOException;
import java.text.ParseException;

import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getInt;
import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getString;

public class UserMapper implements Mapper<User> {
    public interface UserColumns extends DbContract.IdentifiableColumns, DbContract.BodyColumn {
        String TABLE_NAME = "users";
    }

    private static Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(UserColumns.TABLE_NAME).build();
    public static final String USERS = UserColumns.TABLE_NAME;
    public static final String USER_ID = UserColumns.TABLE_NAME + "/#";

    public static String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/org.hisp.dhis.models.User";
    public static String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "/org.hisp.dhis.models.User";

    private static final String[] PROJECTION = new String[]{
            UserColumns.COLUMN_ID,
            UserColumns.COLUMN_UID,
            UserColumns.COLUMN_CODE,
            UserColumns.COLUMN_CREATED,
            UserColumns.COLUMN_LAST_UPDATED,
            UserColumns.COLUMN_NAME,
            UserColumns.COLUMN_DISPLAY_NAME,
            UserColumns.COLUMN_BODY
    };
    private final ObjectMapper objectMapper;

    public UserMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Uri getContentUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return PROJECTION;
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
