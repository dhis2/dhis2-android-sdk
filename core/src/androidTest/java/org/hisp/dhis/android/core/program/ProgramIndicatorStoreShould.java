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

package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.program.ProgramIndicatorModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramIndicatorStoreShould extends AbsStoreTestCase {
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Date CREATED = new Date();
    private static final Date LAST_UPDATED = CREATED;
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";
    private static final Boolean DISPLAY_IN_FORM = true;
    private static final String EXPRESSION = "test_expression";
    private static final String DIMENSION_ITEM = "test_dimension_item";
    private static final String FILTER = "test_filter";
    private static final String PROGRAM = "test_program";
    private static final Integer DECIMALS = 3;

    public static final String[] PROGRAM_INDICATOR_PROJECTION = {
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.SHORT_NAME,
            Columns.DISPLAY_SHORT_NAME,
            Columns.DESCRIPTION,
            Columns.DISPLAY_DESCRIPTION,
            Columns.DISPLAY_IN_FORM,
            Columns.EXPRESSION,
            Columns.DIMENSION_ITEM,
            Columns.FILTER,
            Columns.DECIMALS,
            Columns.PROGRAM
    };

    private ProgramIndicatorStore store;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        store = new ProgramIndicatorStoreImpl(databaseAdapter());
    }

    @Test
    @MediumTest
    public void persist_row_in_data_base_when_insert() throws ParseException {
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        long rowId = store.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                CREATED,
                LAST_UPDATED,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                DISPLAY_IN_FORM,
                EXPRESSION,
                DIMENSION_ITEM,
                FILTER,
                DECIMALS,
                PROGRAM
        );

        Cursor cursor = database().query(ProgramIndicatorModel.TABLE,
                PROGRAM_INDICATOR_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(
                        UID,
                        CODE,
                        NAME,
                        DISPLAY_NAME,
                        BaseIdentifiableObject.DATE_FORMAT.format(CREATED),
                        BaseIdentifiableObject.DATE_FORMAT.format(LAST_UPDATED),
                        SHORT_NAME,
                        DISPLAY_SHORT_NAME,
                        DESCRIPTION,
                        DISPLAY_DESCRIPTION,
                        toInteger(DISPLAY_IN_FORM),
                        EXPRESSION,
                        DIMENSION_ITEM,
                        FILTER,
                        DECIMALS,
                        PROGRAM)
                .isExhausted();
    }

    // ToDo: consider introducing conflict resolution strategy

    @Test
    @MediumTest
    public void update_program_indicator_in_data_base_when_update() throws Exception {
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programIndicator = new ContentValues();
        programIndicator.put(Columns.UID, UID);
        programIndicator.put(Columns.DECIMALS, DECIMALS);
        programIndicator.put(Columns.DISPLAY_IN_FORM, DISPLAY_IN_FORM);
        programIndicator.put(Columns.PROGRAM, PROGRAM);
        database().insert(ProgramIndicatorModel.TABLE, null, programIndicator);

        String[] projection = {Columns.UID, Columns.DECIMALS, Columns.DISPLAY_IN_FORM};
        Cursor cursor = database().query(ProgramIndicatorModel.TABLE, projection, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(UID, DECIMALS, 1); // DISPLAY_IN_FORM ==  Boolean.TRUE == 1 in database
        int updatedDecimals = 5;
        boolean updatedDisplayInForm = Boolean.FALSE; // Boolean.FALSE == 0 in database

        // update the program indicator
        int update = store.update(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED,
                SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, updatedDisplayInForm, EXPRESSION,
                DIMENSION_ITEM, FILTER, updatedDecimals, PROGRAM, UID);

        // check that store returns 1 after successful update
        assertThat(update).isEqualTo(1);

        cursor = database().query(ProgramIndicatorModel.TABLE, projection, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(UID, updatedDecimals, 0); // 0 == Boolean.FALSE
    }

    @Test
    @MediumTest
    public void delete_program_indicator_in_data_base_when_delete() throws Exception {
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programIndicator = new ContentValues();
        programIndicator.put(Columns.UID, UID);
        programIndicator.put(Columns.PROGRAM, PROGRAM);
        database().insert(ProgramIndicatorModel.TABLE, null, programIndicator);

        String[] projection = {Columns.UID};

        Cursor cursor = database().query(ProgramIndicatorModel.TABLE, projection, null, null, null, null, null);
        // check that program indicator was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID);

        // delete the program indicator
        int delete = store.delete(UID);

        // check that store returns 1 when successfully deleting
        assertThat(delete).isEqualTo(1);

        cursor = database().query(ProgramIndicatorModel.TABLE, projection, null, null, null, null, null);

        // check that program indicator is deleted in database
        assertThatCursor(cursor).isExhausted();

    }

    @Test
    @MediumTest
    public void delete_program_indicator_when_delete_program() throws Exception {

        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programIndicator = new ContentValues();
        programIndicator.put(Columns.UID, UID);
        programIndicator.put(Columns.PROGRAM, PROGRAM);
        database().insert(ProgramIndicatorModel.TABLE, null, programIndicator);

        String[] projection = {Columns.UID};

        Cursor cursor = database().query(ProgramIndicatorModel.TABLE, projection, null, null, null, null, null);

        // check that program indicator was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID);

        database().delete(ProgramModel.TABLE, ProgramModel.Columns.UID + " =?", new String[]{PROGRAM});

        cursor = database().query(ProgramIndicatorModel.TABLE, projection, null, null, null, null, null);

        // check that program indicator was deleted on cascade from program
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        store.insert(null, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED, SHORT_NAME, DISPLAY_SHORT_NAME,
                DESCRIPTION, DISPLAY_DESCRIPTION, DISPLAY_IN_FORM, EXPRESSION, DIMENSION_ITEM, FILTER, DECIMALS,
                PROGRAM);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_program() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED, SHORT_NAME, DISPLAY_SHORT_NAME,
                DESCRIPTION, DISPLAY_DESCRIPTION, DISPLAY_IN_FORM, EXPRESSION, DIMENSION_ITEM, FILTER, DECIMALS,
                null);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_uid() {
        store.update(null, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED, SHORT_NAME, DISPLAY_SHORT_NAME,
                DESCRIPTION, DISPLAY_DESCRIPTION, DISPLAY_IN_FORM, EXPRESSION, DIMENSION_ITEM, FILTER, DECIMALS,
                PROGRAM, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_program() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED, SHORT_NAME, DISPLAY_SHORT_NAME,
                DESCRIPTION, DISPLAY_DESCRIPTION, DISPLAY_IN_FORM, EXPRESSION, DIMENSION_ITEM, FILTER, DECIMALS,
                null, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_with_null_in_where_program_indicator_uid_field() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED, SHORT_NAME, DISPLAY_SHORT_NAME,
                DESCRIPTION, DISPLAY_DESCRIPTION, DISPLAY_IN_FORM, EXPRESSION, DIMENSION_ITEM, FILTER, DECIMALS,
                PROGRAM, null);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void  throw_illegal_argument_exception_when_delete_null_uid() {
        store.delete(null);
    }
}