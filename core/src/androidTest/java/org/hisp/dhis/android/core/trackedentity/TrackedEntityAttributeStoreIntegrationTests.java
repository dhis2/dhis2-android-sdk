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
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
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
public class TrackedEntityAttributeStoreIntegrationTests extends AbsStoreTestCase {
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
    private static final String PATTERN = "test_pattern";
    private static final String SORT_ORDER_IN_LIST_NO_PROGRAM = "test_sort_order_in_list_no_program";
    private static final ValueType VALUE_TYPE = ValueType.BOOLEAN;
    private static final String EXPRESSION = "test_expression";
    private static final TrackedEntityAttributeSearchScope SEARCH_SCOPE = TrackedEntityAttributeSearchScope.SEARCH_ORG_UNITS;
    private static final Boolean PROGRAM_SCOPE = true;
    private static final Boolean DISPLAY_IN_LIST_NO_PROGRAM = false;
    private static final Boolean GENERATED = true;
    private static final Boolean DISPLAY_ON_VISIT_SCHEDULE = false;
    private static final Boolean ORG_UNIT_SCOPE = true;
    private static final Boolean UNIQUE = false;
    private static final Boolean INHERIT = true;

    private static final long OPTION_SET_ID = 99L;
    private static final String OPTION_SET_UID = "test_option_set_uid";

    public static final String[] TRACKED_ENTITY_ATTRIBUTE_PROJECTION = {
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

    private TrackedEntityAttributeStore trackedEntityAttributeStore;

    public static ContentValues create(long id, String uid) {

        ContentValues trackedEntity = new ContentValues();
        trackedEntity.put(Columns.ID, id);
        trackedEntity.put(Columns.UID, uid);
        trackedEntity.put(Columns.CODE, CODE);
        trackedEntity.put(Columns.NAME, NAME);
        trackedEntity.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
        trackedEntity.put(Columns.CREATED, BaseIdentifiableObject.DATE_FORMAT.format(CREATED));
        trackedEntity.put(Columns.LAST_UPDATED, BaseIdentifiableObject.DATE_FORMAT.format(LAST_UPDATED));
        trackedEntity.put(Columns.SHORT_NAME, SHORT_NAME);
        trackedEntity.put(Columns.DISPLAY_SHORT_NAME, DISPLAY_SHORT_NAME);
        trackedEntity.put(Columns.DESCRIPTION, DESCRIPTION);
        trackedEntity.put(Columns.DISPLAY_DESCRIPTION, DISPLAY_DESCRIPTION);
        trackedEntity.put(Columns.PATTERN, PATTERN);
        trackedEntity.put(Columns.SORT_ORDER_IN_LIST_NO_PROGRAM, SORT_ORDER_IN_LIST_NO_PROGRAM);
        trackedEntity.put(Columns.OPTION_SET, OPTION_SET_UID);
        trackedEntity.put(Columns.VALUE_TYPE, VALUE_TYPE.toString());
        trackedEntity.put(Columns.EXPRESSION, EXPRESSION);
        trackedEntity.put(Columns.SEARCH_SCOPE, SEARCH_SCOPE.toString());
        trackedEntity.put(Columns.PROGRAM_SCOPE, PROGRAM_SCOPE);
        trackedEntity.put(Columns.DISPLAY_IN_LIST_NO_PROGRAM, DISPLAY_IN_LIST_NO_PROGRAM);
        trackedEntity.put(Columns.GENERATED, GENERATED);
        trackedEntity.put(Columns.DISPLAY_ON_VISIT_SCHEDULE, DISPLAY_ON_VISIT_SCHEDULE);
        trackedEntity.put(Columns.ORG_UNIT_SCOPE, ORG_UNIT_SCOPE);
        trackedEntity.put(Columns.UNIQUE, UNIQUE);
        trackedEntity.put(Columns.INHERIT, INHERIT);
        return trackedEntity;
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        trackedEntityAttributeStore = new TrackedEntityAttributeStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        ContentValues optionSet =
                CreateOptionSetUtils.create(OPTION_SET_ID, OPTION_SET_UID);

        database().insert(Tables.OPTION_SET, null, optionSet);

        long rowId = trackedEntityAttributeStore.insert(
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

        Cursor cursor = database().query(Tables.TRACKED_ENTITY_ATTRIBUTE,
                TRACKED_ENTITY_ATTRIBUTE_PROJECTION, null, null, null, null, null);

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
                        toInteger(INHERIT))
                .isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertWithoutForeignKey_shouldThrowException() throws ParseException {
        trackedEntityAttributeStore.insert(
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
    }

    @Test
    public void delete_shouldDeleteTrackedEntityAttributeWhenDeletingOptionSet() throws Exception {
        ContentValues optionSet = CreateOptionSetUtils.create(OPTION_SET_ID, OPTION_SET_UID);
        database().insert(Tables.OPTION_SET, null, optionSet);

        ContentValues trackedEntityAttribute = new ContentValues();
        trackedEntityAttribute.put(Columns.ID, 1L);
        trackedEntityAttribute.put(Columns.UID, UID);
        trackedEntityAttribute.put(Columns.OPTION_SET, OPTION_SET_UID);

        database().insert(Tables.TRACKED_ENTITY_ATTRIBUTE, null, trackedEntityAttribute);

        String[] projection = {Columns.ID, Columns.UID, Columns.OPTION_SET};
        Cursor cursor = database().query(Tables.TRACKED_ENTITY_ATTRIBUTE, projection, null, null, null, null, null);
        // checking that tracked entity attribute is successfully inserted
        assertThatCursor(cursor).hasRow(1L, UID, OPTION_SET_UID);

        database().delete(Tables.OPTION_SET, OptionSetModel.Columns.UID + " =?", new String[]{OPTION_SET_UID});
        cursor = database().query(Tables.TRACKED_ENTITY_ATTRIBUTE, projection, null, null, null, null, null);

        // checking that tracked entity attribute is deleted
        assertThatCursor(cursor).isExhausted();

    }

    // ToDo: consider introducing conflict resolution strategy

    @Test
    public void close_shouldNotCloseDatabase() {
        trackedEntityAttributeStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
