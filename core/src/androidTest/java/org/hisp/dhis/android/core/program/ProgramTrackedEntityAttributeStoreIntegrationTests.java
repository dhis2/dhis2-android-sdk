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

package org.hisp.dhis.android.core.program;

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
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModelIntegrationTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;


@RunWith(AndroidJUnit4.class)
public class ProgramTrackedEntityAttributeStoreIntegrationTests extends AbsStoreTestCase {
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
    private static final Boolean MANDATORY = true;
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_tracked_entity_attribute_uid";
    private static final ValueType VALUE_TYPE = ValueType.BOOLEAN;
    private static final Boolean ALLOW_FUTURE_DATES = false;
    private static final Boolean DISPLAY_IN_LIST = true;

    private static final long TRACKED_ENTITY_ATTRIBUTE_ID = 1L;

    public static final String[] PROGRAM_TRACKED_ENTITY_ATTRIBUTE_PROJECTION = {
            ProgramTrackedEntityAttributeContract.Columns.UID,
            ProgramTrackedEntityAttributeContract.Columns.CODE,
            ProgramTrackedEntityAttributeContract.Columns.NAME,
            ProgramTrackedEntityAttributeContract.Columns.DISPLAY_NAME,
            ProgramTrackedEntityAttributeContract.Columns.CREATED,
            ProgramTrackedEntityAttributeContract.Columns.LAST_UPDATED,
            ProgramTrackedEntityAttributeContract.Columns.SHORT_NAME,
            ProgramTrackedEntityAttributeContract.Columns.DISPLAY_SHORT_NAME,
            ProgramTrackedEntityAttributeContract.Columns.DESCRIPTION,
            ProgramTrackedEntityAttributeContract.Columns.DISPLAY_DESCRIPTION,
            ProgramTrackedEntityAttributeContract.Columns.MANDATORY,
            ProgramTrackedEntityAttributeContract.Columns.TRACKED_ENTITY_ATTRIBUTE,
            ProgramTrackedEntityAttributeContract.Columns.VALUE_TYPE,
            ProgramTrackedEntityAttributeContract.Columns.ALLOW_FUTURE_DATES,
            ProgramTrackedEntityAttributeContract.Columns.DISPLAY_IN_LIST
    };

    private ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        programTrackedEntityAttributeStore = new ProgramTrackedEntityAttributeStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() throws ParseException {

        // insert test OptionSet to comply with foreign key constraint of TrackedEntityAttribute
        ContentValues optionSet =
                OptionSetModelIntegrationTest.create(99L, "test_option_set_uid");
        database().insert(DbOpenHelper.Tables.OPTION_SET, null, optionSet);


        // insert test TrackedEntityAttribute to comply with foreign key constraint of ProgramTrackedEntityAttribute
        ContentValues trackedEntityAttribute =
                TrackedEntityAttributeModelIntegrationTests.create(TRACKED_ENTITY_ATTRIBUTE_ID, TRACKED_ENTITY_ATTRIBUTE);
        database().insert(DbOpenHelper.Tables.TRACKED_ENTITY_ATTRIBUTE, null, trackedEntityAttribute);


        long rowId = programTrackedEntityAttributeStore.insert(
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
                MANDATORY,
                TRACKED_ENTITY_ATTRIBUTE,
                VALUE_TYPE,
                ALLOW_FUTURE_DATES,
                DISPLAY_IN_LIST
        );

        Cursor cursor = database().query(DbOpenHelper.Tables.PROGRAM_TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_TRACKED_ENTITY_ATTRIBUTE_PROJECTION, null, null, null, null, null);

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
                        getIntegerFromBoolean(MANDATORY),
                        TRACKED_ENTITY_ATTRIBUTE,
                        VALUE_TYPE,
                        getIntegerFromBoolean(ALLOW_FUTURE_DATES),
                        getIntegerFromBoolean(DISPLAY_IN_LIST))
                .isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertWithoutForeignKey_shouldThrowException() throws ParseException {
        programTrackedEntityAttributeStore.insert(
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
                MANDATORY,
                TRACKED_ENTITY_ATTRIBUTE,
                VALUE_TYPE,
                ALLOW_FUTURE_DATES,
                DISPLAY_IN_LIST
        );
    }

    // ToDo: test cascade deletion: on option set referenced with foreign key delete -> TEA delete

    // ToDo: consider introducing conflict resolution strategy

    @Test
    public void close_shouldNotCloseDatabase() {
        programTrackedEntityAttributeStore.close();

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
