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

package org.hisp.dhis.android.core.dataelement;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.dataelement.DataElementModel.Columns;
import org.hisp.dhis.android.core.option.CreateOptionSetUtils;
import org.hisp.dhis.android.core.option.OptionSetModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class DataElementStoreShould extends AbsStoreTestCase {

    private static final String[] DATA_ELEMENT_PROJECTION = {
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
            Columns.VALUE_TYPE,
            Columns.ZERO_IS_SIGNIFICANT,
            Columns.AGGREGATION_TYPE,
            Columns.FORM_NAME,
            Columns.NUMBER_TYPE,
            Columns.DOMAIN_TYPE,
            Columns.DIMENSION,
            Columns.DISPLAY_FORM_NAME,
            Columns.OPTION_SET
    };

    private static final long ID = 21L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";

    private static final String DISPLAY_NAME = "test_display_name";
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";

    private static final String DISPLAY_DESCRIPTION = "test_display_description";
    private static final ValueType VALUE_TYPE = ValueType.TEXT;
    private static final Boolean ZERO_IS_SIGNIFICANT = Boolean.FALSE;
    private static final String AGGREGATION_OPERATOR = "test_aggregationOperator";
    private static final String FORM_NAME = "test_formName";
    private static final String NUMBER_TYPE = "test_numberType";
    private static final String DOMAIN_TYPE = "test_domainType";
    private static final String DIMENSION = "test_dimension";
    private static final String DISPLAY_FORM_NAME = "test_displayFormName";
    private static final String OPTION_SET = "test_optionSet";

    private DataElementStore store;

    private final Date date;
    private final String dateString;

    public DataElementStoreShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.store = new DataElementStoreImpl(databaseAdapter());
    }

    @Test
    public void persist_data_element_in_database_when_insert() {
        ContentValues optionSet = CreateOptionSetUtils.create(ID, OPTION_SET);
        database().insert(OptionSetModel.TABLE, null, optionSet);

        long rowId = store.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date,
                date,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VALUE_TYPE,
                ZERO_IS_SIGNIFICANT,
                AGGREGATION_OPERATOR,
                FORM_NAME,
                NUMBER_TYPE,
                DOMAIN_TYPE,
                DIMENSION,
                DISPLAY_FORM_NAME,
                OPTION_SET
        );
        Cursor cursor = database().query(DataElementModel.TABLE, DATA_ELEMENT_PROJECTION,
                null, null, null, null, null);
        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                dateString,
                dateString,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VALUE_TYPE,
                0, // ZERO_IS_SIGNIFICANT = Boolean.FALSE
                AGGREGATION_OPERATOR,
                FORM_NAME,
                NUMBER_TYPE,
                DOMAIN_TYPE,
                DIMENSION,
                DISPLAY_FORM_NAME,
                OPTION_SET
        ).isExhausted();
    }

    @Test
    public void persist_deferrable_data_element_in_database_when_insert() {
        final String deferredOptionSetUid = "deferredOptionSetUid";

        database().beginTransaction();
        long rowId = store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME,
                DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, VALUE_TYPE, ZERO_IS_SIGNIFICANT,
                AGGREGATION_OPERATOR, FORM_NAME, NUMBER_TYPE, DOMAIN_TYPE, DIMENSION, DISPLAY_FORM_NAME,
                deferredOptionSetUid
        );
        ContentValues optionSet = CreateOptionSetUtils.create(2L, deferredOptionSetUid);
        database().insert(OptionSetModel.TABLE, null, optionSet);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(DataElementModel.TABLE, DATA_ELEMENT_PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, CODE, NAME, DISPLAY_NAME, dateString, dateString, SHORT_NAME,
                DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, VALUE_TYPE, 0, AGGREGATION_OPERATOR,
                FORM_NAME, NUMBER_TYPE, DOMAIN_TYPE, DIMENSION, DISPLAY_FORM_NAME,
                deferredOptionSetUid
        ).isExhausted();
    }

    @Test
    public void persist_data_element_in_database_without_option_set_when_insert() {
        long rowId = store.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date,
                date,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VALUE_TYPE,
                ZERO_IS_SIGNIFICANT,
                AGGREGATION_OPERATOR,
                FORM_NAME,
                NUMBER_TYPE,
                DOMAIN_TYPE,
                DIMENSION,
                DISPLAY_FORM_NAME,
                null
        );

        Cursor cursor = database().query(DataElementModel.TABLE, DATA_ELEMENT_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                dateString,
                dateString,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VALUE_TYPE,
                0, // ZERO_IS_SIGNIFICANT = Boolean.FALSE
                AGGREGATION_OPERATOR,
                FORM_NAME,
                NUMBER_TYPE,
                DOMAIN_TYPE,
                DIMENSION,
                DISPLAY_FORM_NAME,
                null
        ).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_persist_a_data_element_with_invalid_foreign_key() {
        String fakeOptionSetUid = "fake_option_set_uid";
        store.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date,
                date,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VALUE_TYPE,
                ZERO_IS_SIGNIFICANT,
                AGGREGATION_OPERATOR,
                FORM_NAME,
                NUMBER_TYPE,
                DOMAIN_TYPE,
                DIMENSION,
                DISPLAY_FORM_NAME,
                fakeOptionSetUid
        );
    }

    @Test
    public void delete_data_element_in_database_when_deleting_options_set_foreign_key() {
        ContentValues optionSet = CreateOptionSetUtils.create(ID, OPTION_SET);
        database().insert(OptionSetModel.TABLE, null, optionSet);

        ContentValues dataElement = new ContentValues();
        dataElement.put(Columns.ID, ID);
        dataElement.put(Columns.UID, UID);
        dataElement.put(Columns.OPTION_SET, OPTION_SET);

        database().insert(DataElementModel.TABLE, null, dataElement);

        String[] PROJECTION = {Columns.ID, Columns.UID, Columns.OPTION_SET};

        Cursor cursor = database().query(DataElementModel.TABLE, PROJECTION, null, null, null, null, null);

        // checking that dataElement was successfully inserted
        assertThatCursor(cursor).hasRow(ID, UID, OPTION_SET).isExhausted();

        // deleting option set
        database().delete(OptionSetModel.TABLE, OptionSetModel.Columns.UID + "=?", new String[]{OPTION_SET});

        cursor = database().query(DataElementModel.TABLE, PROJECTION, null, null, null, null, null);

        // checking that dataElement was deleted by option set on delete cascade
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void update_data_element_in_database_when_update() throws Exception {
        // insert dataElement into database
        ContentValues dataElement = new ContentValues();
        dataElement.put(Columns.UID, UID);
        dataElement.put(Columns.CODE, CODE);
        dataElement.put(Columns.NAME, NAME);
        database().insert(DataElementModel.TABLE, null, dataElement);

        String[] projection = {Columns.UID, Columns.CODE, Columns.NAME};

        Cursor cursor = database().query(DataElementModel.TABLE, projection, null, null, null, null, null);

        // checking if data element was successfully inserted
        assertThatCursor(cursor).hasRow(UID, CODE, NAME).isExhausted();

        String updatedName = "new_updated_data_element_name";
        int update = store.update(UID, CODE, updatedName, DISPLAY_NAME, date, date, null, null,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VALUE_TYPE,
                ZERO_IS_SIGNIFICANT,
                AGGREGATION_OPERATOR,
                FORM_NAME,
                NUMBER_TYPE,
                DOMAIN_TYPE,
                DIMENSION,
                DISPLAY_FORM_NAME,
                null, // null OptionSetUid
                UID);

        // checking that update was successful
        assertThat(update).isEqualTo(1);

        cursor = database().query(DataElementModel.TABLE, projection, null, null, null, null, null);

        // checking that row was updated
        assertThatCursor(cursor).hasRow(UID, CODE, updatedName).isExhausted();

    }

    @Test
    public void delete_data_element_in_database_when_delete() throws Exception {

        // insert dataElement into database
        ContentValues dataElement = new ContentValues();
        dataElement.put(Columns.UID, UID);
        database().insert(DataElementModel.TABLE, null, dataElement);

        String[] projection = {Columns.UID};

        Cursor cursor = database().query(DataElementModel.TABLE, projection, null, null, null, null, null);

        // checking if data element was successfully inserted
        assertThatCursor(cursor).hasRow(UID).isExhausted();

        int delete = store.delete(UID);
        // checking that store returns 1 (deletion happens)
        assertThat(delete).isEqualTo(1);
        cursor = database().query(DataElementModel.TABLE, projection, null, null, null, null, null);

        // check that row is deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        store.insert(null, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION,
                DISPLAY_DESCRIPTION, VALUE_TYPE, ZERO_IS_SIGNIFICANT, AGGREGATION_OPERATOR, FORM_NAME, NUMBER_TYPE,
                DOMAIN_TYPE, DIMENSION, DISPLAY_FORM_NAME, OPTION_SET);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_uid() {
        store.update(null, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION,
                DISPLAY_DESCRIPTION, VALUE_TYPE, ZERO_IS_SIGNIFICANT, AGGREGATION_OPERATOR, FORM_NAME, NUMBER_TYPE,
                DOMAIN_TYPE, DIMENSION, DISPLAY_FORM_NAME, OPTION_SET, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_whereUid() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION,
                DISPLAY_DESCRIPTION, VALUE_TYPE, ZERO_IS_SIGNIFICANT, AGGREGATION_OPERATOR, FORM_NAME, NUMBER_TYPE,
                DOMAIN_TYPE, DIMENSION, DISPLAY_FORM_NAME, OPTION_SET, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_delete_null_uid() {
        store.delete(null);
    }
}
