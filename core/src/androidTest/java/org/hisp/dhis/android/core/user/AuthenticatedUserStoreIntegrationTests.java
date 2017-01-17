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
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class AuthenticatedUserStoreIntegrationTests extends AbsStoreTestCase {
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
        authenticatedUserStore = new AuthenticatedUserStoreImpl(database());

        // row which will be referenced
        ContentValues userRow = UserStoreIntegrationTests.create(1L, "test_user_uid");
        database().insert(DbOpenHelper.Tables.USER, null, userRow);
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        // inserting authenticated user model item
        long rowId = authenticatedUserStore.insert("test_user_uid", "test_user_credentials");

        Cursor cursor = database().query(DbOpenHelper.Tables.AUTHENTICATED_USER,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(1L, "test_user_uid", "test_user_credentials")
                .isExhausted();
    }

    @Test
    public void query_shouldReturnPersistedRows() {
        ContentValues authenticatedUser = new ContentValues();
        authenticatedUser.put(AuthenticatedUserModel.Columns.USER, "test_user_uid");
        authenticatedUser.put(AuthenticatedUserModel.Columns.CREDENTIALS, "test_user_credentials");

        database().insert(DbOpenHelper.Tables.AUTHENTICATED_USER, null, authenticatedUser);

        AuthenticatedUserModel authenticatedUserModel = AuthenticatedUserModel.builder()
                .id(1L).user("test_user_uid").credentials("test_user_credentials")
                .build();

        assertThat(authenticatedUserStore.query().size()).isEqualTo(1);
        assertThat(authenticatedUserStore.query()).contains(authenticatedUserModel);
    }

    @Test
    public void query_shouldReturnEmptyListOnEmptyTable() {
        assertThat(authenticatedUserStore.query()).isEmpty();
    }

    @Test
    public void delete_shouldDeleteAllRows() {
        ContentValues authenticatedUser = new ContentValues();
        authenticatedUser.put(AuthenticatedUserModel.Columns.USER, "test_user_uid");
        authenticatedUser.put(AuthenticatedUserModel.Columns.CREDENTIALS, "test_user_credentials");

        database().insert(DbOpenHelper.Tables.AUTHENTICATED_USER, null, authenticatedUser);

        int deleted = authenticatedUserStore.delete();

        Cursor cursor = database().query(DbOpenHelper.Tables.AUTHENTICATED_USER,
                null, null, null, null, null, null);
        assertThat(deleted).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        authenticatedUserStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
