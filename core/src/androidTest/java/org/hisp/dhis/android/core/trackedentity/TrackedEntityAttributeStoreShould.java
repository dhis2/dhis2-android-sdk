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

package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.option.CreateOptionSetUtils;
import org.hisp.dhis.android.core.option.OptionSetModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel.Columns;
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
public class TrackedEntityAttributeStoreShould extends AbsStoreTestCase {
    public static final String[] PROJECTION = {
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
            Columns.PATTERN,
            Columns.SORT_ORDER_IN_LIST_NO_PROGRAM,
            Columns.OPTION_SET,
            Columns.VALUE_TYPE,
            Columns.EXPRESSION,
            Columns.SEARCH_SCOPE,
            Columns.PROGRAM_SCOPE,
            Columns.DISPLAY_IN_LIST_NO_PROGRAM,
            Columns.GENERATED,
            Columns.DISPLAY_ON_VISIT_SCHEDULE,
            Columns.ORG_UNIT_SCOPE,
            Columns.UNIQUE,
            Columns.INHERIT
    };
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";
    private static final String PATTERN = "test_pattern";
    private static final Integer SORT_ORDER_IN_LIST_NO_PROGRAM = 2;
    private static final ValueType VALUE_TYPE = ValueType.BOOLEAN;
    private static final String EXPRESSION = "test_expression";
    private static final TrackedEntityAttributeSearchScope SEARCH_SCOPE =
            TrackedEntityAttributeSearchScope.SEARCH_ORG_UNITS;
    private static final Boolean PROGRAM_SCOPE = true;
    private static final Boolean DISPLAY_IN_LIST_NO_PROGRAM = false;
    private static final Boolean GENERATED = true;
    private static final Boolean DISPLAY_ON_VISIT_SCHEDULE = false;
    private static final Boolean ORG_UNIT_SCOPE = true;
    private static final Boolean UNIQUE = false;
    private static final Boolean INHERIT = true;
    private static final long OPTION_SET_ID = 99L;
    private static final String OPTION_SET_UID = "test_option_set_uid";
    private TrackedEntityAttributeStore store;

    private final Date date;
    private final String dateString;

    public TrackedEntityAttributeStoreShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        store = new TrackedEntityAttributeStoreImpl(databaseAdapter());
        ContentValues optionSet = CreateOptionSetUtils.create(OPTION_SET_ID, OPTION_SET_UID);
        database().insert(OptionSetModel.TABLE, null, optionSet);
    }

    @Test
    @MediumTest
    public void insert_in_data_base_when_insert() {
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
                PATTERN,
                SORT_ORDER_IN_LIST_NO_PROGRAM,
                OPTION_SET_UID,
                VALUE_TYPE,
                EXPRESSION,
                SEARCH_SCOPE,
                PROGRAM_SCOPE,
                DISPLAY_IN_LIST_NO_PROGRAM,
                GENERATED,
                DISPLAY_ON_VISIT_SCHEDULE,
                ORG_UNIT_SCOPE,
                UNIQUE,
                INHERIT
        );
        Cursor cursor = database().query(TrackedEntityAttributeModel.TABLE, PROJECTION, null, null, null, null, null);

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
                PATTERN,
                SORT_ORDER_IN_LIST_NO_PROGRAM,
                OPTION_SET_UID,
                VALUE_TYPE,
                EXPRESSION,
                SEARCH_SCOPE,
                toInteger(PROGRAM_SCOPE),
                toInteger(DISPLAY_IN_LIST_NO_PROGRAM),
                toInteger(GENERATED),
                toInteger(DISPLAY_ON_VISIT_SCHEDULE),
                toInteger(ORG_UNIT_SCOPE),
                toInteger(UNIQUE),
                toInteger(INHERIT)
        ).isExhausted();
    }

    @Test
    @MediumTest
    public void insert_deferred_in_data_base_when_insert() {
        final String deferredOptionSet = "deferredOptionSet";
        database().beginTransaction();
        long rowId = store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME,
                DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, PATTERN, SORT_ORDER_IN_LIST_NO_PROGRAM,
                deferredOptionSet,
                VALUE_TYPE, EXPRESSION, SEARCH_SCOPE, PROGRAM_SCOPE, DISPLAY_IN_LIST_NO_PROGRAM, GENERATED,
                DISPLAY_ON_VISIT_SCHEDULE, ORG_UNIT_SCOPE, UNIQUE, INHERIT
        );
        ContentValues optionSet = CreateOptionSetUtils.create(3L, deferredOptionSet);
        database().insert(OptionSetModel.TABLE, null, optionSet);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(TrackedEntityAttributeModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, CODE, NAME, DISPLAY_NAME, dateString, dateString, SHORT_NAME,
                DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, PATTERN, SORT_ORDER_IN_LIST_NO_PROGRAM,
                deferredOptionSet,
                VALUE_TYPE, EXPRESSION, SEARCH_SCOPE, toInteger(PROGRAM_SCOPE), toInteger(DISPLAY_IN_LIST_NO_PROGRAM),
                toInteger(GENERATED), toInteger(DISPLAY_ON_VISIT_SCHEDULE), toInteger(ORG_UNIT_SCOPE),
                toInteger(UNIQUE), toInteger(INHERIT)
        ).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    @MediumTest
    public void throw_sqlite_constraint_exception_when_insert_without_foreign_key() throws ParseException {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME, DISPLAY_SHORT_NAME,
                DESCRIPTION, DISPLAY_DESCRIPTION, PATTERN, SORT_ORDER_IN_LIST_NO_PROGRAM,
                "wrong",
                VALUE_TYPE, EXPRESSION, SEARCH_SCOPE, PROGRAM_SCOPE, DISPLAY_IN_LIST_NO_PROGRAM, GENERATED,
                DISPLAY_ON_VISIT_SCHEDULE, ORG_UNIT_SCOPE, UNIQUE, INHERIT
        );
    }

    @Test
    @MediumTest
    public void delete_tea_when_delete_option_set() throws Exception {
        ContentValues trackedEntityAttribute = new ContentValues();
        trackedEntityAttribute.put(Columns.ID, 1L);
        trackedEntityAttribute.put(Columns.UID, UID);
        trackedEntityAttribute.put(Columns.OPTION_SET, OPTION_SET_UID);

        database().insert(TrackedEntityAttributeModel.TABLE, null, trackedEntityAttribute);

        String[] projection = {Columns.ID, Columns.UID, Columns.OPTION_SET};
        Cursor cursor = database().query(TrackedEntityAttributeModel.TABLE, projection, null, null, null, null, null);
        // checking that tracked entity attribute is successfully inserted
        assertThatCursor(cursor).hasRow(1L, UID, OPTION_SET_UID);

        database().delete(OptionSetModel.TABLE, OptionSetModel.Columns.UID + " =?", new String[]{OPTION_SET_UID});
        cursor = database().query(TrackedEntityAttributeModel.TABLE, projection, null, null, null, null, null);

        // checking that tracked entity attribute is deleted
        assertThatCursor(cursor).isExhausted();

    }

    @Test
    @MediumTest
    public void update_tea_in_data_base_when_update() throws Exception {
        ContentValues trackedEntityAttribute = new ContentValues();
        trackedEntityAttribute.put(Columns.UID, UID);
        trackedEntityAttribute.put(Columns.EXPRESSION, EXPRESSION);
        trackedEntityAttribute.put(Columns.OPTION_SET, OPTION_SET_UID);

        database().insert(TrackedEntityAttributeModel.TABLE, null, trackedEntityAttribute);

        String[] projection = {Columns.UID, Columns.EXPRESSION};
        Cursor cursor = database().query(TrackedEntityAttributeModel.TABLE, projection, null, null, null, null, null);

        // check that tracked entity attribute was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID, EXPRESSION);

        String updatedExpression = "updated_expression";

        int update = store.update(
                UID, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME,
                DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, PATTERN, SORT_ORDER_IN_LIST_NO_PROGRAM,
                OPTION_SET_UID, VALUE_TYPE, updatedExpression, SEARCH_SCOPE, PROGRAM_SCOPE, DISPLAY_IN_LIST_NO_PROGRAM,
                GENERATED, DISPLAY_ON_VISIT_SCHEDULE, ORG_UNIT_SCOPE, UNIQUE, INHERIT, UID
        );

        // check that store returns 1 on successful update
        assertThat(update).isEqualTo(1);

        cursor = database().query(TrackedEntityAttributeModel.TABLE, projection, null, null, null, null, null);

        // check that tracked entity attribute is updated in database
        assertThatCursor(cursor).hasRow(UID, updatedExpression).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_tea_when_delete() throws Exception {
        ContentValues trackedEntityAttribute = new ContentValues();
        trackedEntityAttribute.put(Columns.UID, UID);

        database().insert(TrackedEntityAttributeModel.TABLE, null, trackedEntityAttribute);

        String[] projection = {Columns.UID};
        Cursor cursor = database().query(TrackedEntityAttributeModel.TABLE, projection, null, null, null, null, null);

        // check that tracked entity attribute was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID);

        int delete = store.delete(UID);

        assertThat(delete).isEqualTo(1);

        cursor = database().query(TrackedEntityAttributeModel.TABLE, projection, null, null, null, null, null);

        // check that tracked entity attribute doesn't exist in database
        assertThatCursor(cursor).isExhausted();
    }

    // ToDo: consider introducing conflict resolution strategy

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        store.insert(null, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME, DISPLAY_SHORT_NAME,
                DESCRIPTION, DISPLAY_DESCRIPTION, PATTERN, SORT_ORDER_IN_LIST_NO_PROGRAM, OPTION_SET_UID, VALUE_TYPE,
                EXPRESSION, SEARCH_SCOPE, PROGRAM_SCOPE, DISPLAY_IN_LIST_NO_PROGRAM, GENERATED,
                DISPLAY_ON_VISIT_SCHEDULE, ORG_UNIT_SCOPE, UNIQUE, INHERIT
        );
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_uid() {
        store.update(
                null, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME,
                DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, PATTERN, SORT_ORDER_IN_LIST_NO_PROGRAM,
                OPTION_SET_UID, VALUE_TYPE, EXPRESSION, SEARCH_SCOPE, PROGRAM_SCOPE, DISPLAY_IN_LIST_NO_PROGRAM,
                GENERATED, DISPLAY_ON_VISIT_SCHEDULE, ORG_UNIT_SCOPE, UNIQUE, INHERIT, UID
        );
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_whereUid() {
        store.update(
                UID, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME,
                DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, PATTERN, SORT_ORDER_IN_LIST_NO_PROGRAM,
                OPTION_SET_UID, VALUE_TYPE, EXPRESSION, SEARCH_SCOPE, PROGRAM_SCOPE, DISPLAY_IN_LIST_NO_PROGRAM,
                GENERATED, DISPLAY_ON_VISIT_SCHEDULE, ORG_UNIT_SCOPE, UNIQUE, INHERIT, null
        );
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_delete_null_uid() {
        store.delete(null);
    }
}
