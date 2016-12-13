package org.hisp.dhis.android.core.user;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.commons.BaseIdentifiableObjectContract;
import org.hisp.dhis.android.core.data.database.DbContract;
import org.hisp.dhis.android.core.data.database.DbUtils;

public final class UserCredentialsContract {
    // ContentProvider related properties
    public static final String USER_CREDENTIALS = "userCredentials";
    public static final String USER_CREDENTIALS_ID = USER_CREDENTIALS + "/#";

    public static final String CONTENT_TYPE_DIR = DbUtils.directoryType(USER_CREDENTIALS);
    public static final String CONTENT_TYPE_ITEM = DbUtils.itemType(USER_CREDENTIALS);

    @NonNull
    public static Uri userCredentials() {
        return Uri.withAppendedPath(DbContract.AUTHORITY_URI, USER_CREDENTIALS);
    }

    @NonNull
    public static Uri userCredentials(long id) {
        return ContentUris.withAppendedId(userCredentials(), id);
    }

    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String USERNAME = "username";
        String USER = "user";
    }
}
