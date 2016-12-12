package org.hisp.dhis.android.core.user;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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
    public static ContentValues create(long id, String uid, String user) {
        ContentValues userCredentials = new ContentValues();
        userCredentials.put(Columns.ID, id);
        userCredentials.put(Columns.UID, uid);
        userCredentials.put(Columns.CODE, "test_code");
        userCredentials.put(Columns.NAME, "test_name");
        userCredentials.put(Columns.DISPLAY_NAME, "test_display_name");
        userCredentials.put(Columns.CREATED, "test_created");
        userCredentials.put(Columns.LAST_UPDATED, "test_lastUpdated");
        userCredentials.put(Columns.USERNAME, "test_username");
        userCredentials.put(Columns.USER, user);
        return userCredentials;
    }

    public void testGetType_shouldReturnCorrectMimeTypes() {
        assertThat(getProvider().getType(UserCredentialsContract.userCredentials()))
                .isEqualTo(UserCredentialsContract.CONTENT_TYPE_DIR);
        assertThat(getProvider().getType(UserCredentialsContract.userCredentials(1L)))
                .isEqualTo(UserCredentialsContract.CONTENT_TYPE_ITEM);
    }

    public void testInsert_shouldPersistRow() {
        // we need to insert row into parent table first
        ContentValues user = UserContractIntegrationTests.create(1L, "test_user_uid");
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, user);

        ContentValues userCredentials = create(1L, "test_uid", "test_user_uid");
        Uri itemUri = getProvider().insert(UserCredentialsContract.userCredentials(), userCredentials);

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_CREDENTIALS,
                USER_CREDENTIALS_PROJECTION, null, null, null, null, null);

        assertThat(ContentUris.parseId(itemUri)).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(USER_CREDENTIALS_PROJECTION, userCredentials).isExhausted();
    }

    public void testInsert_shouldThrowOnExistingId() {
        try {
            // we need to insert row into parent table first
            ContentValues user = UserContractIntegrationTests.create(1L, "test_user_uid");
            database().insertOrThrow(DbOpenHelper.Tables.USER, null, user);

            ContentValues userCredentials = create(1L, "test_uid", "test_user_uid");
            database().insertOrThrow(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentials);

            getProvider().insert(UserCredentialsContract.userCredentials(), userCredentials);

            fail("SQLiteConstraintException was expected, but nothing was thrown");
        } catch (SQLiteConstraintException constraintException) {
            assertThat(constraintException).isNotNull();
        }
    }

    public void testUpdate_shouldUpdateRow() {
        // we need to insert row into parent table first
        ContentValues user = UserContractIntegrationTests.create(1L, "test_user_uid");
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, user);

        ContentValues userCredentials = create(1L, "test_uid", "test_user_uid");
        database().insertOrThrow(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentials);

        int updatedCount = getProvider().update(
                UserCredentialsContract.userCredentials(1L), userCredentials, null, null);

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_CREDENTIALS,
                USER_CREDENTIALS_PROJECTION, null, null, null, null, null);

        assertThat(updatedCount).isEqualTo(1);
        assertThatCursor(cursor).hasRow(USER_CREDENTIALS_PROJECTION, userCredentials).isExhausted();
    }

    public void testUpdateByUriWithId_shouldUpdateRow() {
        // we need to insert row into parent table first
        ContentValues user = UserContractIntegrationTests.create(1L, "test_user_uid");
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, user);

        ContentValues userCredentials = create(1L, "test_uid", "test_user_uid");
        database().insertOrThrow(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentials);

        userCredentials.put(Columns.USERNAME, "test_username_another");
        int updatedCount = getProvider().update(UserCredentialsContract
                .userCredentials(1L), userCredentials, null, null);

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_CREDENTIALS,
                USER_CREDENTIALS_PROJECTION, null, null, null, null, null);

        assertThat(updatedCount).isEqualTo(1);
        assertThatCursor(cursor).hasRow(USER_CREDENTIALS_PROJECTION, userCredentials).isExhausted();
    }

    public void testDelete_shouldDeleteRow() {
        // we need to insert row into parent table first
        ContentValues user = UserContractIntegrationTests.create(1L, "test_user_uid");
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, user);

        ContentValues userCredentials = create(1L, "test_uid", "test_user_uid");
        database().insertOrThrow(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentials);

        int deletedCount = getProvider().delete(UserCredentialsContract.userCredentials(),
                Columns.ID + " = ?", new String[]{String.valueOf(1L)});

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_CREDENTIALS,
                USER_CREDENTIALS_PROJECTION, null, null, null, null, null);

        assertThat(deletedCount).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    public void testDeleteByUriWithId_shouldDeleteRow() {
        // we need to insert row into parent table first
        ContentValues user = UserContractIntegrationTests.create(1L, "test_user_uid");
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, user);

        ContentValues userCredentials = create(1L, "test_uid", "test_user_uid");
        database().insertOrThrow(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentials);

        int deletedCount = getProvider().delete(UserCredentialsContract.userCredentials(1L), null, null);

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_CREDENTIALS,
                USER_CREDENTIALS_PROJECTION, null, null, null, null, null);

        assertThat(deletedCount).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    public void testDeleteUser_shouldDeleteRow() {
        // we need to insert row into parent table first
        ContentValues user = UserContractIntegrationTests.create(1L, "test_user_uid");
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, user);

        ContentValues userCredentials = create(1L, "test_uid", "test_user_uid");
        database().insertOrThrow(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentials);

        // delete user in order to trigger foreign key constraint
        database().delete(DbOpenHelper.Tables.USER, UserContract.Columns.ID + " = ?",
                new String[]{String.valueOf(1L)});
    }

    public void testQuery_shouldReturnRows() {
        // first, we need to insert users which will be referenced by user credentials
        ContentValues userOne = UserContractIntegrationTests.create(1L, "test_user_one_uid");
        ContentValues userTwo = UserContractIntegrationTests.create(2L, "test_user_two_uid");

        database().insertOrThrow(DbOpenHelper.Tables.USER, null, userOne);
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, userTwo);

        // inserting user credentials
        ContentValues userCredentialsOne = create(1L, "test_uid_one", "test_user_one_uid");
        ContentValues userCredentialsTwo = create(2L, "test_uid_two", "test_user_two_uid");

        database().insertOrThrow(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentialsOne);
        database().insertOrThrow(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentialsTwo);

        Cursor cursor = getProvider().query(UserCredentialsContract.userCredentials(),
                USER_CREDENTIALS_PROJECTION, null, null, null);
        assertThatCursor(cursor)
                .hasRow(USER_CREDENTIALS_PROJECTION, userCredentialsOne)
                .hasRow(USER_CREDENTIALS_PROJECTION, userCredentialsTwo)
                .isExhausted();
    }

    public void testQueryByUriWithId_shouldReturnRow() {
        // first, we need to insert users which will be referenced by user credentials
        ContentValues userOne = UserContractIntegrationTests.create(1L, "test_user_one_uid");
        ContentValues userTwo = UserContractIntegrationTests.create(2L, "test_user_two_uid");

        database().insertOrThrow(DbOpenHelper.Tables.USER, null, userOne);
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, userTwo);

        ContentValues userCredentialsOne = create(1L, "test_uid_one", "test_user_one_uid");
        ContentValues userCredentialsTwo = create(2L, "test_uid_two", "test_user_two_uid");

        database().insert(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentialsOne);
        database().insert(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentialsTwo);

        Cursor cursor = getProvider().query(UserCredentialsContract.userCredentials(2L),
                USER_CREDENTIALS_PROJECTION, null, null, null);
        assertThatCursor(cursor).hasRow(USER_CREDENTIALS_PROJECTION,
                userCredentialsTwo).isExhausted();
    }
}
