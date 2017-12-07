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

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class AuthenticatedUserStoreShould extends AbsStoreTestCase {

    private static final String USER_UID = "test_user_uid";
    private static final String USER_CREDENTIALS = "test_user_credentials";

    private static final String[] PROJECTION = {
            AuthenticatedUserModel.Columns.ID,
            AuthenticatedUserModel.Columns.USER,
            AuthenticatedUserModel.Columns.CREDENTIALS,
    };

    private AuthenticatedUserStore authenticatedUserStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        authenticatedUserStore = new AuthenticatedUserStoreImpl(databaseAdapter());
        // row which will be referenced
        ContentValues userRow = UserStoreShould.create(1L, USER_UID);
        database().insert(UserModel.TABLE, null, userRow);
    }

    @Test
    public void insert_in_data_base_when_insert() {
        // inserting authenticated user model item
        long rowId = authenticatedUserStore.insert(USER_UID, USER_CREDENTIALS);

        Cursor cursor = database().query(AuthenticatedUserModel.TABLE,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(1L, USER_UID, USER_CREDENTIALS).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_uid_arg() {
        authenticatedUserStore.insert(null, USER_CREDENTIALS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ithrow_illegal_argument_exception_when_nsert_null_credentials_arg() {
        authenticatedUserStore.insert(USER_UID, null);
    }

    @Test
    public void insert_in_data_base_when_insert_deferrable_row() {
        final String deferrableUserUid = "deferrableUserUid";

        database().beginTransaction();
        long rowId = authenticatedUserStore.insert(deferrableUserUid, USER_CREDENTIALS);
        ContentValues userRow = UserStoreShould.create(2L, deferrableUserUid);
        database().insert(UserModel.TABLE, null, userRow);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(AuthenticatedUserModel.TABLE,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(1L, deferrableUserUid, USER_CREDENTIALS).isExhausted();
    }

    @Test
    public void return_persisted_row_on_query() {
        ContentValues authenticatedUser = new ContentValues();
        authenticatedUser.put(AuthenticatedUserModel.Columns.USER, USER_UID);
        authenticatedUser.put(AuthenticatedUserModel.Columns.CREDENTIALS, USER_CREDENTIALS);

        database().insert(AuthenticatedUserModel.TABLE, null, authenticatedUser);

        AuthenticatedUserModel authenticatedUserModel = AuthenticatedUserModel.builder()
                .id(1L).user(USER_UID).credentials(USER_CREDENTIALS)
                .build();

        assertThat(authenticatedUserStore.query().size()).isEqualTo(1);
        assertThat(authenticatedUserStore.query()).contains(authenticatedUserModel);
    }

    @Test
    public void return_empty_list_when_query_empty_table() {
        assertThat(authenticatedUserStore.query()).isEmpty();
    }

    @Test
    public void delete_all_rows_in_data_base_when_delete_without_params() {
        ContentValues authenticatedUser = new ContentValues();
        authenticatedUser.put(AuthenticatedUserModel.Columns.USER, USER_UID);
        authenticatedUser.put(AuthenticatedUserModel.Columns.CREDENTIALS, USER_CREDENTIALS);

        database().insert(AuthenticatedUserModel.TABLE, null, authenticatedUser);

        int deleted = authenticatedUserStore.delete();

        Cursor cursor = database().query(AuthenticatedUserModel.TABLE,
                null, null, null, null, null, null);
        assertThat(deleted).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_authenticated_user_when_delete_user_foreign_key() {
        authenticatedUserStore.insert(USER_UID, USER_CREDENTIALS);
        database().delete(UserModel.TABLE, UserModel.Columns.UID + "=?", new String[]{USER_UID});
        Cursor cursor = database().query(AuthenticatedUserModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_authenticated_user_with_invalid_uid_foreign_key() {
        String wrongUserUid = "wrong";
        authenticatedUserStore.insert(wrongUserUid, USER_CREDENTIALS);
    }

}
