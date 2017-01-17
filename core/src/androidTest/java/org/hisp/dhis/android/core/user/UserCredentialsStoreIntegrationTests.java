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
            UserCredentialsModel.Columns.UID,
            UserCredentialsModel.Columns.CODE,
            UserCredentialsModel.Columns.NAME,
            UserCredentialsModel.Columns.DISPLAY_NAME,
            UserCredentialsModel.Columns.CREATED,
            UserCredentialsModel.Columns.LAST_UPDATED,
            UserCredentialsModel.Columns.USERNAME,
            UserCredentialsModel.Columns.USER,
    };

    private UserCredentialsStore userCredentialsStore;

    public static ContentValues create(long id, String uid, String user) {
        ContentValues userCredentials = new ContentValues();
        userCredentials.put(UserCredentialsModel.Columns.ID, id);
        userCredentials.put(UserCredentialsModel.Columns.UID, uid);
        userCredentials.put(UserCredentialsModel.Columns.CODE, "test_code");
        userCredentials.put(UserCredentialsModel.Columns.NAME, "test_name");
        userCredentials.put(UserCredentialsModel.Columns.DISPLAY_NAME, "test_display_name");
        userCredentials.put(UserCredentialsModel.Columns.CREATED, "test_created");
        userCredentials.put(UserCredentialsModel.Columns.LAST_UPDATED, "test_lastUpdated");
        userCredentials.put(UserCredentialsModel.Columns.USERNAME, "test_username");
        userCredentials.put(UserCredentialsModel.Columns.USER, user);
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
