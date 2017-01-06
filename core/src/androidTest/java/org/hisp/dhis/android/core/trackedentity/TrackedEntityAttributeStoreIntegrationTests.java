/*
 * Copyright (c) 2016, University of Oslo
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
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.option.OptionSetModelIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
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
            TrackedEntityAttributeContract.Columns.UID,
            TrackedEntityAttributeContract.Columns.CODE,
            TrackedEntityAttributeContract.Columns.NAME,
            TrackedEntityAttributeContract.Columns.DISPLAY_NAME,
            TrackedEntityAttributeContract.Columns.CREATED,
            TrackedEntityAttributeContract.Columns.LAST_UPDATED,
            TrackedEntityAttributeContract.Columns.SHORT_NAME,
            TrackedEntityAttributeContract.Columns.DISPLAY_SHORT_NAME,
            TrackedEntityAttributeContract.Columns.DESCRIPTION,
            TrackedEntityAttributeContract.Columns.DISPLAY_DESCRIPTION,
            TrackedEntityAttributeContract.Columns.PATTERN,
            TrackedEntityAttributeContract.Columns.SORT_ORDER_IN_LIST_NO_PROGRAM,
            TrackedEntityAttributeContract.Columns.OPTION_SET,
            TrackedEntityAttributeContract.Columns.VALUE_TYPE,
            TrackedEntityAttributeContract.Columns.EXPRESSION,
            TrackedEntityAttributeContract.Columns.SEARCH_SCOPE,
            TrackedEntityAttributeContract.Columns.PROGRAM_SCOPE,
            TrackedEntityAttributeContract.Columns.DISPLAY_IN_LIST_NO_PROGRAM,
            TrackedEntityAttributeContract.Columns.GENERATED,
            TrackedEntityAttributeContract.Columns.DISPLAY_ON_VISIT_SCHEDULE,
            TrackedEntityAttributeContract.Columns.ORG_UNIT_SCOPE,
            TrackedEntityAttributeContract.Columns.UNIQUE,
            TrackedEntityAttributeContract.Columns.INHERIT
    };

    private TrackedEntityAttributeStore trackedEntityAttributeStore;

    public static ContentValues create(long id, String uid) {

        ContentValues trackedEntity = new ContentValues();
        trackedEntity.put(TrackedEntityAttributeContract.Columns.ID, id);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.UID, uid);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.CODE, CODE);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.NAME, NAME);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.DISPLAY_NAME, DISPLAY_NAME);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.CREATED, BaseIdentifiableObject.DATE_FORMAT.format(CREATED));
        trackedEntity.put(TrackedEntityAttributeContract.Columns.LAST_UPDATED, BaseIdentifiableObject.DATE_FORMAT.format(LAST_UPDATED));
        trackedEntity.put(TrackedEntityAttributeContract.Columns.SHORT_NAME, SHORT_NAME);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.DISPLAY_SHORT_NAME, DISPLAY_SHORT_NAME);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.DESCRIPTION, DESCRIPTION);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.DISPLAY_DESCRIPTION, DISPLAY_DESCRIPTION);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.PATTERN, PATTERN);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.SORT_ORDER_IN_LIST_NO_PROGRAM, SORT_ORDER_IN_LIST_NO_PROGRAM);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.OPTION_SET, OPTION_SET_UID);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.VALUE_TYPE, VALUE_TYPE.toString());
        trackedEntity.put(TrackedEntityAttributeContract.Columns.EXPRESSION, EXPRESSION);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.SEARCH_SCOPE, SEARCH_SCOPE.toString());
        trackedEntity.put(TrackedEntityAttributeContract.Columns.PROGRAM_SCOPE, PROGRAM_SCOPE);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.DISPLAY_IN_LIST_NO_PROGRAM, DISPLAY_IN_LIST_NO_PROGRAM);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.GENERATED, GENERATED);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.DISPLAY_ON_VISIT_SCHEDULE, DISPLAY_ON_VISIT_SCHEDULE);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.ORG_UNIT_SCOPE, ORG_UNIT_SCOPE);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.UNIQUE, UNIQUE);
        trackedEntity.put(TrackedEntityAttributeContract.Columns.INHERIT, INHERIT);
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
                OptionSetModelIntegrationTest.create(OPTION_SET_ID, OPTION_SET_UID);

        database().insert(DbOpenHelper.Tables.OPTION_SET, null, optionSet);

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

        Cursor cursor = database().query(DbOpenHelper.Tables.TRACKED_ENTITY_ATTRIBUTE,
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
                        getIntegerFromBoolean(PROGRAM_SCOPE),
                        getIntegerFromBoolean(DISPLAY_IN_LIST_NO_PROGRAM),
                        getIntegerFromBoolean(GENERATED),
                        getIntegerFromBoolean(DISPLAY_ON_VISIT_SCHEDULE),
                        getIntegerFromBoolean(ORG_UNIT_SCOPE),
                        getIntegerFromBoolean(UNIQUE),
                        getIntegerFromBoolean(INHERIT))
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

    // ToDo: consider introducing conflict resolution strategy

    @Test
    public void close_shouldNotCloseDatabase() {
        trackedEntityAttributeStore.close();

        assertThat(database().isOpen()).isTrue();
    }

    @NonNull
    private Integer getIntegerFromBoolean(Boolean bool) {
        if (bool) {
            return 1;
        }

        return 0;
    }

}
