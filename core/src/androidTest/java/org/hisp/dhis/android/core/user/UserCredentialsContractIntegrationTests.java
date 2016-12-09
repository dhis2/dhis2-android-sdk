package org.hisp.dhis.android.core.user;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.android.core.database.AbsProviderTestCase;
import org.hisp.dhis.android.core.database.DbOpenHelper;
import org.hisp.dhis.android.core.user.UserCredentialsContract.Columns;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.database.CursorAssert.assertThatCursor;

public class UserCredentialsContractIntegrationTests extends AbsProviderTestCase {
    public static final String[] USER_CREDENTIALS_PROJECTION = {
            Columns.ID,
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.USERNAME,
            Columns.USER,
    };

    /**
     * Provides an instance of ContentValues which correspond to UserCredentials table.
     * Note: does not contain value for 'user' foreign key (should be overriden by client)
     *
     * @return ContentValues
     */
    public static ContentValues create() {
        ContentValues userCredentials = new ContentValues();
        userCredentials.put(Columns.ID, 1L);
        userCredentials.put(Columns.UID, "test_uid");
        userCredentials.put(Columns.CODE, "test_code");
        userCredentials.put(Columns.NAME, "test_name");
        userCredentials.put(Columns.DISPLAY_NAME, "test_display_name");
        userCredentials.put(Columns.CREATED, "test_created");
        userCredentials.put(Columns.LAST_UPDATED, "test_lastUpdated");
        userCredentials.put(Columns.USERNAME, "test_username");
        userCredentials.putNull(Columns.USER);
        return userCredentials;
    }

    private ContentValues userCredentials;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ContentValues user = UserContractIntegrationTests.create();
        user.put(Columns.ID, 1L);
        user.put(Columns.UID, "test_user_uid");

        // insert row into parent table
        database().insert(DbOpenHelper.Tables.USER, null, user);

        userCredentials = create();

        // reference parent row table
        userCredentials.put(Columns.USER, "test_user_uid");
    }

    public void testGetType_shouldReturnCorrectMimeTypes() {
        assertThat(getProvider().getType(UserCredentialsContract.userCredentials()))
                .isEqualTo(UserCredentialsContract.CONTENT_TYPE_DIR);
        assertThat(getProvider().getType(UserCredentialsContract.userCredentials(1L)))
                .isEqualTo(UserCredentialsContract.CONTENT_TYPE_ITEM);
    }

    public void testInsert_shouldPersistRow() {
        Uri itemUri = getProvider().insert(UserCredentialsContract.userCredentials(), userCredentials);

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_CREDENTIALS,
                USER_CREDENTIALS_PROJECTION, null, null, null, null, null);
        assertThat(ContentUris.parseId(itemUri)).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(USER_CREDENTIALS_PROJECTION, userCredentials).isExhausted();
    }

    public void testInsert_shouldNotThrowOnExistingId() {
        database().insert(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentials);
        getProvider().insert(UserCredentialsContract.userCredentials(), userCredentials);
    }

    public void testUpdate_shouldUpdateRow() {
        database().insert(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentials);

        int updatedCount = getProvider().update(
                UserCredentialsContract.userCredentials(1L), userCredentials, null, null);

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_CREDENTIALS,
                USER_CREDENTIALS_PROJECTION, null, null, null, null, null);

        assertThat(updatedCount).isEqualTo(1);
        assertThatCursor(cursor).hasRow(USER_CREDENTIALS_PROJECTION, userCredentials).isExhausted();
    }
}
