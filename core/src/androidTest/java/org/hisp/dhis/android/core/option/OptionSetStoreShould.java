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
import android.support.test.filters.MediumTest;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.option.OptionSetModel.Columns;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class OptionSetStoreShould extends AbsStoreTestCase {

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final ValueType VALUE_TYPE = ValueType.BOOLEAN;
    private static final int VERSION = 51;

    // timestamp
    private static final String DATE = "2016-12-20T16:26:00.007";
    private final Date date;

    private static final String[] OPTION_SET_PROJECTION = {
            Columns.UID, Columns.CODE, Columns.NAME,
            Columns.DISPLAY_NAME, Columns.CREATED,
            Columns.LAST_UPDATED, Columns.VERSION, Columns.VALUE_TYPE
    };

    private OptionSetStore store;

    public OptionSetStoreShould() throws ParseException {
        this.date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        this.store = new OptionSetStoreImpl(databaseAdapter());
    }

    @Test
    @MediumTest
    public void should_persist_option_set_in_data_base_when_persist() throws ParseException {
        long rowId = store.insert(
                UID, CODE, NAME, DISPLAY_NAME, date, date, VERSION, VALUE_TYPE);

        Cursor cursor = database().query(OptionSetModel.TABLE, OPTION_SET_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID, CODE, NAME,
                DISPLAY_NAME, BaseIdentifiableObject.DATE_FORMAT.format(date),
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                VERSION, VALUE_TYPE).isExhausted();
    }

    @Test
    @MediumTest
    public void update_option_set_in_data_base_when_update() throws Exception {
        ContentValues optionSet = new ContentValues();
        optionSet.put(Columns.ID, 1L);
        optionSet.put(Columns.UID, UID);
        optionSet.put(Columns.VERSION, VERSION);
        optionSet.put(Columns.NAME, NAME);
        optionSet.put(Columns.DISPLAY_NAME, DISPLAY_NAME);

        database().insert(OptionSetModel.TABLE, null, optionSet);

        String[] projection = {Columns.UID, Columns.NAME, Columns.DISPLAY_NAME};
        Cursor cursor = database().query(OptionSetModel.TABLE, projection, null, null, null, null, null);

        // checking that option set is successfully inserted
        assertThatCursor(cursor).hasRow(UID, NAME, DISPLAY_NAME).isExhausted();

        int updatedRow = store.update(
                UID, CODE, "new_name", "new_display_name", date, date, 5, VALUE_TYPE, UID
        );

        assertThat(updatedRow).isEqualTo(1);

        cursor = database().query(OptionSetModel.TABLE, projection, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(
                UID, "new_name", "new_display_name"
        ).isExhausted();

    }

    @Test
    @MediumTest
    public void delete_option_set_in_data_base_when_delete() throws Exception {
        ContentValues optionSet = new ContentValues();
        optionSet.put(Columns.ID, 1L);
        optionSet.put(Columns.UID, UID);
        optionSet.put(Columns.NAME, NAME);
        optionSet.put(Columns.DISPLAY_NAME, DISPLAY_NAME);

        database().insert(OptionSetModel.TABLE, null, optionSet);

        String[] projection = {Columns.UID, Columns.NAME, Columns.DISPLAY_NAME};
        Cursor cursor = database().query(OptionSetModel.TABLE, projection, null, null, null, null, null);

        // checking that option set is successfully inserted
        assertThatCursor(cursor).hasRow(UID, NAME, DISPLAY_NAME).isExhausted();

        // deleting the optionSet
        store.delete(UID);

        cursor = database().query(OptionSetModel.TABLE, projection, null, null, null, null, null);

        // checking that optionSet is deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_an_updated_option_set_in_data_base_when_delete() throws Exception {
        ContentValues optionSet = new ContentValues();
        optionSet.put(Columns.ID, 1L);
        optionSet.put(Columns.UID, UID);
        optionSet.put(Columns.NAME, NAME);
        optionSet.put(Columns.DISPLAY_NAME, DISPLAY_NAME);

        database().insert(OptionSetModel.TABLE, null, optionSet);

        String[] projection = {Columns.UID, Columns.NAME, Columns.DISPLAY_NAME};
        Cursor cursor = database().query(OptionSetModel.TABLE, projection, null, null, null, null, null);

        // checking that option set is successfully inserted
        assertThatCursor(cursor).hasRow(UID, NAME, DISPLAY_NAME).isExhausted();

        // updates the option set with new uid
        store.update(
                "new_uid", CODE, NAME, DISPLAY_NAME, date, date, 5, VALUE_TYPE, UID
        );

        cursor = database().query(OptionSetModel.TABLE, projection, null, null, null, null, null);

        // checking that optionSet was successfully updated
        assertThatCursor(cursor).hasRow("new_uid", NAME, DISPLAY_NAME).isExhausted();

        // deletes the option set
        store.delete("new_uid");

        cursor = database().query(OptionSetModel.TABLE, projection, null, null, null, null, null);

        // checking that the option set was successfully deleted
        assertThatCursor(cursor).isExhausted();

    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_after_insert_null_uid() {
        store.insert(null, CODE, NAME, DISPLAY_NAME, date, date, VERSION, VALUE_TYPE);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_after_update_null_uid() {
        store.update(null, CODE, NAME, DISPLAY_NAME, date, date, VERSION, VALUE_TYPE, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_after_update_null_whereUid() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, VERSION, VALUE_TYPE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_after_delete_null_uid() {
        store.delete(null);
    }

    //    @Test
//    public void persist_option_set_in_data_base_after_insert_or_replace() throws ParseException {
//        database().beginTransaction();
//        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
//
//        ContentValues optionSet = new ContentValues();
//        optionSet.put(Columns.ID, 1L);
//        optionSet.put(Columns.UID, UID);
//        optionSet.put(Columns.VERSION, VERSION);
//        optionSet.put(Columns.NAME, NAME);
//        optionSet.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
//
//        database().insert(OptionSetModel.TABLE, null, optionSet);
//
//        String[] projection = {Columns.UID, Columns.NAME, Columns.DISPLAY_NAME};
//        Cursor cursor = database().query(OptionSetModel.TABLE, projection, null, null, null, null, null);
//
//        // checking that option set is successfully inserted
//        assertThatCursor(cursor).hasRow(UID, NAME, DISPLAY_NAME).isExhausted();
//
//
//        // inserting two options linked to the option set
//        String optionUid = "option_uid";
//        ContentValues option = new ContentValues();
//        option.put(OptionModel.Columns.ID, 1L);
//        option.put(OptionModel.Columns.UID, optionUid);
//        option.put(OptionModel.Columns.OPTION_SET, UID);
//
//        database().insert(OptionModel.TABLE, null, option);
//
//        String option1Uid = "option1_uid";
//        ContentValues option1 = new ContentValues();
//        option1.put(OptionModel.Columns.ID, 2L);
//        option1.put(OptionModel.Columns.UID, option1Uid);
//        option1.put(OptionModel.Columns.OPTION_SET, UID);
//
//        database().insert(OptionModel.TABLE, null, option1);
//
//        String[] optionProjection = {OptionModel.Columns.UID, OptionModel.Columns.OPTION_SET};
//
//        cursor = database().query(OptionModel.TABLE, optionProjection, null, null, null, null, null);
//
//        assertThatCursor(cursor).hasRow(optionUid, UID);
//        assertThatCursor(cursor).hasRow(option1Uid, UID).isExhausted();
//
//        String newOptionSetName = "newOptionSetName";
//        String newOptionSetDisplayName = "newOptionSetDisplayName";
//
//        store.insertOrReplace(UID, CODE, newOptionSetName, newOptionSetDisplayName, date, date, VERSION,
// VALUE_TYPE);
//
//        cursor = database().query(OptionSetModel.TABLE, projection, null, null, null, null, null);
//
//        assertThatCursor(cursor).hasRow(UID, newOptionSetName, newOptionSetDisplayName).isExhausted();
//
//
//        cursor = database().query(OptionModel.TABLE, optionProjection, null, null, null, null, null);
//
//        assertThatCursor(cursor).hasRow(optionUid, UID);
//        assertThatCursor(cursor).hasRow(option1Uid, UID).isExhausted();
//
//        database().setTransactionSuccessful();
//
//        database().endTransaction();
//
//    }

}
