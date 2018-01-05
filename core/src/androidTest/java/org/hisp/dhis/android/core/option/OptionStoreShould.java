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

package org.hisp.dhis.android.core.option;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.option.OptionModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class OptionStoreShould extends AbsStoreTestCase {

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final long OPTION_SET_ID = 53L;
    private static final String OPTION_SET_UID = "test_option_set_uid";

    private static final String[] OPTION_PROJECTION = {
            Columns.UID, Columns.CODE, Columns.NAME,
            Columns.DISPLAY_NAME, Columns.CREATED,
            Columns.LAST_UPDATED, Columns.OPTION_SET
    };

    private final Date date;
    private final String dateString;

    public OptionStoreShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    private OptionStore store;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.store = new OptionStoreImpl(databaseAdapter());
    }

    @Test
    public void insert_in_data_base_option_when_insert() {
        // INSERT TABLE SETS
        ContentValues optionSet =
                CreateOptionSetUtils.create(OPTION_SET_ID, OPTION_SET_UID);

        database().insert(OptionSetModel.TABLE, null, optionSet);

        long rowId = store.insert(
                UID, CODE, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID
        );

        Cursor cursor = database().query(OptionModel.TABLE, OPTION_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID, CODE, NAME,
                DISPLAY_NAME, BaseIdentifiableObject.DATE_FORMAT.format(date),
                BaseIdentifiableObject.DATE_FORMAT.format(date), OPTION_SET_UID)
                .isExhausted();
    }

    @Test
    public void insert_in_data_base_deferrable_option_when_insert() {

        database().beginTransaction();
        long rowId = store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID);
        ContentValues optionSet = CreateOptionSetUtils.create(OPTION_SET_ID, OPTION_SET_UID);
        database().insert(OptionSetModel.TABLE, null, optionSet);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(OptionModel.TABLE, OPTION_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID, CODE, NAME,
                DISPLAY_NAME, BaseIdentifiableObject.DATE_FORMAT.format(date),
                BaseIdentifiableObject.DATE_FORMAT.format(date), OPTION_SET_UID)
                .isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_persist_option_without_foreign_key() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID);
    }

    @Test
    public void delete_options_in_data_base_when_delete_option_set() {
        ContentValues optionSet = CreateOptionSetUtils.create(OPTION_SET_ID, OPTION_SET_UID);
        database().insert(OptionSetModel.TABLE, null, optionSet);

        ContentValues option = new ContentValues();
        option.put(Columns.ID, 1L);
        option.put(Columns.UID, UID);
        option.put(Columns.OPTION_SET, OPTION_SET_UID);

        String option1Uid = "test_option1_uid";

        ContentValues option1 = new ContentValues();
        option1.put(Columns.ID, 2L);
        option1.put(Columns.UID, option1Uid);
        option1.put(Columns.OPTION_SET, OPTION_SET_UID);

        database().insert(OptionModel.TABLE, null, option);
        database().insert(OptionModel.TABLE, null, option1);

        String[] projection = {Columns.ID, Columns.UID, Columns.OPTION_SET};

        Cursor cursor = database().query(OptionModel.TABLE, projection, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(1L, UID, OPTION_SET_UID);
        assertThatCursor(cursor).hasRow(2L, option1Uid, OPTION_SET_UID).isExhausted();

        database().delete(OptionSetModel.TABLE, OptionSetModel.Columns.UID + " =?", new String[]{OPTION_SET_UID});

        cursor = database().query(OptionModel.TABLE, projection, null, null, null, null, null);

        assertThatCursor(cursor).isExhausted();

    }

    @Test
    public void update_option_in_data_base_when_update() throws Exception {
        ContentValues optionSet = CreateOptionSetUtils.create(OPTION_SET_ID, OPTION_SET_UID);
        database().insert(OptionSetModel.TABLE, null, optionSet);

        long insert = store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID);
        assertThat(insert).isEqualTo(1L);

        String[] projection = {Columns.UID, Columns.CODE, Columns.OPTION_SET};

        Cursor cursor = database().query(OptionModel.TABLE, projection, null, null, null, null, null);

        // check that option was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID, CODE, OPTION_SET_UID);

        String newCode = "abc123";
        store.update(UID, newCode, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID, UID);

        cursor = database().query(OptionModel.TABLE, projection, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(UID, newCode, OPTION_SET_UID).isExhausted();

    }

    @Test
    public void delete_option_in_data_base_when_delete() throws Exception {
        ContentValues optionSet = CreateOptionSetUtils.create(OPTION_SET_ID, OPTION_SET_UID);
        database().insert(OptionSetModel.TABLE, null, optionSet);

        long insert = store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID);
        assertThat(insert).isEqualTo(1L);

        String[] projection = {Columns.UID, Columns.CODE, Columns.OPTION_SET};

        Cursor cursor = database().query(OptionModel.TABLE, projection, null, null, null, null, null);

        // check that option was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID, CODE, OPTION_SET_UID);

        // delete option
        store.delete(UID);
        cursor = database().query(OptionModel.TABLE, projection, null, null, null, null, null);

        // check that option is deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        store.insert(null, CODE, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_optionSet() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_uid() {
        store.update(null, CODE, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_OptionSet() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, null, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exceptioN_when_update_null_whereUid() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_delete_null_uid() {
        store.delete(null);
    }
}
