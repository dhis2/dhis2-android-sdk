/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.user.UserCredentialsModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class UserCredentialsStoreShould extends AbsStoreTestCase {

    public static final long ID = 1L;
    public static final String UID = "test_user_credentials_uid";
    public static final String USER_UID = "test_user_uid";
    public static final String NAME = "test_name";
    public static final String CODE = "test_code";
    public static final String DISPLAY_NAME = "test_display_name";
    public static final String CREATED = "test_created";
    public static final String LAST_UPDATED = "test_lastUpdated";
    public static final String USER_CREDENTIALS_CODE = "test_user_credentials_code";
    public static final String USER_CREDENTIALS_NAME = "test_user_credentials_name";
    public static final String USER_CREDENTIALS_DISPLAY_NAME = "test_user_credentials_display_name";

    public static final String USER_CREDENTIALS_USERNAME = "test_user_credentials_username";

    private static final String[] USER_CREDENTIALS_PROJECTION = {
            UserCredentialsModel.Columns.UID,
            UserCredentialsModel.Columns.CODE,
            UserCredentialsModel.Columns.NAME,
            UserCredentialsModel.Columns.DISPLAY_NAME,
            UserCredentialsModel.Columns.CREATED,
            UserCredentialsModel.Columns.LAST_UPDATED,
            UserCredentialsModel.Columns.USERNAME,
            UserCredentialsModel.Columns.USER,
    };

    private UserCredentialsStore store;

    public final Date date;
    public final String dateString;

    public UserCredentialsStoreShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        store = new UserCredentialsStoreImpl(databaseAdapter());

        // row which will be referenced
        ContentValues userRow = UserStoreShould.create(1L, USER_UID);
        database().insert(UserModel.TABLE, null, userRow);
    }

    @Test
    public void insert_in_data_base_when_insert() {
        long rowId = store.insert(
                UID,
                USER_CREDENTIALS_CODE,
                USER_CREDENTIALS_NAME,
                USER_CREDENTIALS_DISPLAY_NAME,
                date, date,
                USER_CREDENTIALS_USERNAME,
                USER_UID);

        Cursor cursor = database().query(UserCredentialsModel.TABLE,
                USER_CREDENTIALS_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID,
                USER_CREDENTIALS_CODE,
                USER_CREDENTIALS_NAME,
                USER_CREDENTIALS_DISPLAY_NAME,
                dateString,
                dateString,
                USER_CREDENTIALS_USERNAME,
                USER_UID
        ).isExhausted();
    }

    @Test
    public void insert_in_data_base_when_insert_deferred_row() {
        final String deferredUid = "deferredForeignKeyUid";

        database().beginTransaction();

        long rowId = store.insert(
                UID,
                USER_CREDENTIALS_CODE,
                USER_CREDENTIALS_NAME,
                USER_CREDENTIALS_DISPLAY_NAME,
                date, date,
                USER_CREDENTIALS_USERNAME,
                deferredUid);
        ContentValues userRow = UserStoreShould.create(2L, deferredUid);
        database().insert(UserModel.TABLE, null, userRow);

        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(UserCredentialsModel.TABLE,
                USER_CREDENTIALS_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID,
                USER_CREDENTIALS_CODE,
                USER_CREDENTIALS_NAME,
                USER_CREDENTIALS_DISPLAY_NAME,
                dateString,
                dateString,
                USER_CREDENTIALS_USERNAME,
                deferredUid
        ).isExhausted();
    }

    @Test
    public void insert_in_data_base_when_insert_nullable_row() {
        long rowId = store.insert(
                UID,
                null,
                null,
                null,
                null, null,
                null,
                USER_UID);

        Cursor cursor = database().query(UserCredentialsModel.TABLE,
                USER_CREDENTIALS_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID,
                null,
                null,
                null,
                null, null,
                null,
                USER_UID
        ).isExhausted();
    }

    @Test
    public void delete_in_data_base_when_delete_user_foreign_key() {
        store.insert(
                UID,
                USER_CREDENTIALS_CODE,
                USER_CREDENTIALS_NAME,
                USER_CREDENTIALS_DISPLAY_NAME,
                date, date,
                USER_CREDENTIALS_USERNAME,
                USER_UID);

        database().delete(UserModel.TABLE, UserModel.Columns.UID + "=?", new String[]{USER_UID});

        Cursor cursor = database().query(UserCredentialsModel.TABLE, USER_CREDENTIALS_PROJECTION,
                null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void update_in_data_base_when_update() throws Exception {
        ContentValues user = new ContentValues();
        user.put(UserModel.Columns.ID, ID);
        user.put(UserModel.Columns.UID, USER_UID);
        database().insert(UserModel.TABLE, null, user);

        ContentValues userCredentials = new ContentValues();
        userCredentials.put(Columns.ID, ID);
        userCredentials.put(Columns.UID, UID);
        userCredentials.put(Columns.NAME, NAME);
        userCredentials.put(Columns.USER, USER_UID);
        database().insert(UserCredentialsModel.TABLE, null, userCredentials);

        String[] projection = {
                Columns.ID, Columns.UID, Columns.NAME, Columns.USER
        };
        Cursor cursor = database().query(UserCredentialsModel.TABLE, projection, null, null, null, null, null);

        // checking that userCredentials was successfully inserted into database
        assertThatCursor(cursor).hasRow(ID, UID, NAME, USER_UID);

        Date date = new Date();

        int updatedRow = store.update(
                UID, CODE, "new name", DISPLAY_NAME, date, date, USER_CREDENTIALS_USERNAME, USER_UID, UID
        );

        cursor = database().query(UserCredentialsModel.TABLE, projection, null, null, null, null, null);

        // checking that userCredentials was successfully updated
        assertThat(updatedRow).isEqualTo(1);
        assertThatCursor(cursor).hasRow(ID, UID, "new name", USER_UID);

    }

    @Test
    public void delete_in_data_base_when_delete_user_credentials() throws Exception {

        // inserting foreign key
        ContentValues user = new ContentValues();
        user.put(UserModel.Columns.ID, ID);
        user.put(UserModel.Columns.UID, USER_UID);
        database().insert(UserModel.TABLE, null, user);

        ContentValues userCredentials = new ContentValues();
        userCredentials.put(Columns.ID, ID);
        userCredentials.put(Columns.UID, UID);
        userCredentials.put(Columns.NAME, NAME);
        userCredentials.put(Columns.USER, USER_UID);
        database().insert(UserCredentialsModel.TABLE, null, userCredentials);

        String[] projection = {
                Columns.ID, Columns.UID, Columns.NAME, Columns.USER
        };
        Cursor cursor = database().query(UserCredentialsModel.TABLE, projection, null, null, null, null, null);

        // checking that userCredentials was successfully inserted into database
        assertThatCursor(cursor).hasRow(ID, UID, NAME, USER_UID);

        int deletedRow = store.delete(UID);

        cursor = database().query(UserCredentialsModel.TABLE, projection, null, null, null, null, null);

        assertThat(deletedRow).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();

    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_invalid_user_foreign_key() {
        store.insert(
                UID,
                USER_CREDENTIALS_CODE,
                USER_CREDENTIALS_NAME,
                USER_CREDENTIALS_DISPLAY_NAME,
                date, date,
                USER_CREDENTIALS_USERNAME,
                "wrong");
    }

    @Test
    public void delete_all_rows_when_delete_without_params() {
        ContentValues userCredentials = CreateUserCredentialsUtils.create(ID, UID, USER_UID);
        database().insert(UserCredentialsModel.TABLE, null, userCredentials);

        int deleted = store.delete();

        Cursor cursor = database().query(UserCredentialsModel.TABLE, null, null, null, null, null, null);
        assertThat(deleted).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_uid_arg() {
        store.insert(null, USER_CREDENTIALS_CODE, USER_CREDENTIALS_NAME, USER_CREDENTIALS_DISPLAY_NAME,
                date, date, USER_CREDENTIALS_USERNAME, USER_UID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_user_uid_arg() {
        store.insert(UID, USER_CREDENTIALS_CODE, USER_CREDENTIALS_NAME, USER_CREDENTIALS_DISPLAY_NAME,
                date, date, USER_CREDENTIALS_USERNAME, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_uid_arg() {
        store.update(null, CODE, "new name", DISPLAY_NAME, date, date, USER_CREDENTIALS_USERNAME, USER_UID, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_User_uid_arg() {
        store.update(UID, CODE, "new name", DISPLAY_NAME, date, date, USER_CREDENTIALS_USERNAME, null, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_where_arg() {
        store.update(UID, CODE, "new name", DISPLAY_NAME, date, date, USER_CREDENTIALS_USERNAME, USER_UID, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_delete_null_uid_arg() {
        store.delete(null);
    }
}
