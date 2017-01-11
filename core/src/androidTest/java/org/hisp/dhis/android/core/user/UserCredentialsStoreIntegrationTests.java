package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class UserCredentialsStoreIntegrationTests extends AbsStoreTestCase {
    private static final String[] PROJECTION = {
            UserCredentialsContract.Columns.UID,
            UserCredentialsContract.Columns.CODE,
            UserCredentialsContract.Columns.NAME,
            UserCredentialsContract.Columns.DISPLAY_NAME,
            UserCredentialsContract.Columns.CREATED,
            UserCredentialsContract.Columns.LAST_UPDATED,
            UserCredentialsContract.Columns.USERNAME,
            UserCredentialsContract.Columns.USER,
    };

    private UserCredentialsStore userCredentialsStore;

    public static ContentValues create(long id, String uid, String user) {
        ContentValues userCredentials = new ContentValues();
        userCredentials.put(UserCredentialsContract.Columns.ID, id);
        userCredentials.put(UserCredentialsContract.Columns.UID, uid);
        userCredentials.put(UserCredentialsContract.Columns.CODE, "test_code");
        userCredentials.put(UserCredentialsContract.Columns.NAME, "test_name");
        userCredentials.put(UserCredentialsContract.Columns.DISPLAY_NAME, "test_display_name");
        userCredentials.put(UserCredentialsContract.Columns.CREATED, "test_created");
        userCredentials.put(UserCredentialsContract.Columns.LAST_UPDATED, "test_lastUpdated");
        userCredentials.put(UserCredentialsContract.Columns.USERNAME, "test_username");
        userCredentials.put(UserCredentialsContract.Columns.USER, user);
        return userCredentials;
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        userCredentialsStore = new UserCredentialsStoreImpl(database());

        // row which will be referenced
        ContentValues userRow = UserStoreIntegrationTests.create(1L, "test_user_uid");
        database().insert(DbOpenHelper.Tables.USER, null, userRow);
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        Date date = new Date();

        // inserting authenticated user model item
        long rowId = userCredentialsStore.insert(
                "test_user_credentials_uid",
                "test_user_credentials_code",
                "test_user_credentials_name",
                "test_user_credentials_display_name",
                date, date,
                "test_user_credentials_username",
                "test_user_uid");

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_CREDENTIALS,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(
                        "test_user_credentials_uid",
                        "test_user_credentials_code",
                        "test_user_credentials_name",
                        "test_user_credentials_display_name",
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        "test_user_credentials_username",
                        "test_user_uid"
                ).isExhausted();
    }

    @Test
    public void delete_shouldDeleteAllRows() {
        ContentValues userCredentials = create(1L, "test_user_credentials", "test_user_uid");
        database().insert(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentials);

        int deleted = userCredentialsStore.delete();

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_CREDENTIALS,
                null, null, null, null, null, null);
        assertThat(deleted).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        userCredentialsStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
