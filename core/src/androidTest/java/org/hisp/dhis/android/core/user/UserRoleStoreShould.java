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
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.user.UserRoleModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class UserRoleStoreShould extends AbsStoreTestCase {

    private static final String[] PROJECTION = {
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED
    };

    //BaseIdentifiableModel attributes:
    private static final Long ID = 1L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";

    private final Date date;
    private final String dateString;

    private static final String DISPLAY_NAME = "test_display_name";
    private UserRoleStore userRoleStore;

    public UserRoleStoreShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        userRoleStore = new UserRoleStoreImpl(databaseAdapter());
    }

    @Test
    @MediumTest
    public void insert_in_data_base_when_insert() {
        long rowId = userRoleStore.insert(UID, CODE, NAME, DISPLAY_NAME, date, date);
        Cursor cursor = database().query(UserRoleModel.TABLE, PROJECTION, null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, CODE, NAME, DISPLAY_NAME, dateString, dateString).isExhausted();
    }

    @Test
    @MediumTest
    public void update_in_data_base_when_update() throws Exception {
        ContentValues userRole = new ContentValues();
        userRole.put(Columns.ID, ID);
        userRole.put(Columns.UID, UID);
        userRole.put(Columns.CODE, CODE);
        database().insert(UserRoleModel.TABLE, null, userRole);

        String[] projection = {Columns.ID, Columns.UID, Columns.CODE};
        Cursor cursor = database().query(UserRoleModel.TABLE, projection, null, null, null, null, null);

        // checking that userRole was successfully inserted
        assertThatCursor(cursor).hasRow(ID, UID, CODE).isExhausted();

        int updatedRow = userRoleStore.update(UID, "new code", NAME, DISPLAY_NAME, date, date, UID);

        cursor = database().query(UserRoleModel.TABLE, projection, null, null, null, null, null);
        // checking that code property is successfully updated for userRole
        assertThat(updatedRow).isEqualTo(1);
        assertThatCursor(cursor).hasRow(ID, UID, "new code").isExhausted();
    }

    @Test
    @MediumTest
    public void delete_in_data_base_when_delete() throws Exception {
        ContentValues userRole = new ContentValues();
        userRole.put(Columns.ID, ID);
        userRole.put(Columns.UID, UID);
        userRole.put(Columns.CODE, CODE);
        database().insert(UserRoleModel.TABLE, null, userRole);

        String[] projection = {Columns.ID, Columns.UID, Columns.CODE};
        Cursor cursor = database().query(UserRoleModel.TABLE, projection, null, null, null, null, null);

        // checking that userRole was successfully inserted
        assertThatCursor(cursor).hasRow(ID, UID, CODE).isExhausted();

        int deletedRow = userRoleStore.delete(UID);

        cursor = database().query(UserRoleModel.TABLE, projection, null, null, null, null, null);
        // checking that userRole was deleted
        assertThat(deletedRow).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();

    }

    @Test (expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        userRoleStore.insert(null, CODE, NAME, DISPLAY_NAME, date, date);
    }

    @Test (expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_uid() {
        userRoleStore.update(null, CODE, NAME, DISPLAY_NAME, date, date, UID);
    }

    @Test (expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_where() {
        userRoleStore.update(UID, CODE, NAME, DISPLAY_NAME, date, date, null);
    }

    @Test (expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_delete_null_arg() {
        userRoleStore.delete(null);
    }
}