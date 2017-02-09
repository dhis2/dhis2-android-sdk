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
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.option.CreateOptionSetUtils;
import org.hisp.dhis.android.core.option.OptionSetModel;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeModel.Columns;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityAttributeUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramTrackedEntityAttributeStoreTests extends AbsStoreTestCase {
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
            Columns.MANDATORY,
            Columns.TRACKED_ENTITY_ATTRIBUTE,
            Columns.VALUE_TYPE,
            Columns.ALLOW_FUTURE_DATES,
            Columns.DISPLAY_IN_LIST,
            Columns.PROGRAM
    };
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
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
    private static final Long ID = 2L;
    private static final String PROGRAM = "test_program_uid";
    private static final String OPTION_SET = "test_option_set_uid";
    private ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;

    private final Date date;
    private final String dateString;

    public ProgramTrackedEntityAttributeStoreTests() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        programTrackedEntityAttributeStore = new ProgramTrackedEntityAttributeStoreImpl(databaseAdapter());
        // insert test OptionSet to comply with foreign key constraint of TrackedEntityAttribute
        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);
        ContentValues optionSet = CreateOptionSetUtils.create(99L, "test_option_set_uid");
        database().insert(OptionSetModel.TABLE, null, optionSet);
        // insert test TrackedEntityAttribute to comply with foreign key constraint of ProgramTrackedEntityAttribute
        ContentValues trackedEntityAttribute = CreateTrackedEntityAttributeUtils.create(
                TRACKED_ENTITY_ATTRIBUTE_ID, TRACKED_ENTITY_ATTRIBUTE, null);
        database().insert(TrackedEntityAttributeModel.TABLE, null, trackedEntityAttribute);
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        long rowId = programTrackedEntityAttributeStore.insert(
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
                MANDATORY,
                TRACKED_ENTITY_ATTRIBUTE,
                VALUE_TYPE,
                ALLOW_FUTURE_DATES,
                DISPLAY_IN_LIST,
                PROGRAM
        );
        Cursor cursor = database().query(ProgramTrackedEntityAttributeModel.TABLE,
                PROJECTION, null, null, null, null, null);

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
                toInteger(MANDATORY),
                TRACKED_ENTITY_ATTRIBUTE,
                VALUE_TYPE,
                toInteger(ALLOW_FUTURE_DATES),
                toInteger(DISPLAY_IN_LIST),
                PROGRAM
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistDeferrableRowInDatabase() {
        final String deferredTrackedEntityAttribute = "deferredTrackedEntityAttribute";
        final String deferredProgram = "deferredProgram";

        database().beginTransaction();
        long rowId = programTrackedEntityAttributeStore.insert(UID, CODE, NAME, DISPLAY_NAME, date, date,
                SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, MANDATORY,
                deferredTrackedEntityAttribute,
                VALUE_TYPE, ALLOW_FUTURE_DATES, DISPLAY_IN_LIST,
                deferredProgram
        );
        ContentValues program = CreateProgramUtils.create(3L, deferredProgram, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);
        ContentValues trackedEntityAttribute = CreateTrackedEntityAttributeUtils.create(
                3L, deferredTrackedEntityAttribute, null);
        database().insert(TrackedEntityAttributeModel.TABLE, null, trackedEntityAttribute);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(ProgramTrackedEntityAttributeModel.TABLE,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, CODE, NAME, DISPLAY_NAME, dateString, dateString, SHORT_NAME,
                DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, toInteger(MANDATORY),
                deferredTrackedEntityAttribute,
                VALUE_TYPE, toInteger(ALLOW_FUTURE_DATES), toInteger(DISPLAY_IN_LIST),
                deferredProgram
        ).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertWithoutTrackedEntityAttributeForeignKey_shouldThrowException() {
        programTrackedEntityAttributeStore.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME,
                DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, MANDATORY,
                "wrong",
                VALUE_TYPE, ALLOW_FUTURE_DATES, DISPLAY_IN_LIST, PROGRAM
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertWithoutProgramForeignKey_shouldThrowException() {
        programTrackedEntityAttributeStore.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME,
                DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, MANDATORY, TRACKED_ENTITY_ATTRIBUTE,
                VALUE_TYPE, ALLOW_FUTURE_DATES, DISPLAY_IN_LIST,
                "wrong"
        );
    }

    @Test
    public void delete_shouldDeleteProgramTrackedEntityAttributeWhenDeletingProgram() {
        ContentValues programTrackedEntityAttribute = new ContentValues();
        programTrackedEntityAttribute.put(Columns.ID, ID);
        programTrackedEntityAttribute.put(Columns.UID, UID);
        programTrackedEntityAttribute.put(Columns.PROGRAM, PROGRAM);
        programTrackedEntityAttribute.put(Columns.TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_ATTRIBUTE);

        database().insert(ProgramTrackedEntityAttributeModel.TABLE, null, programTrackedEntityAttribute);
        String[] projection = {Columns.ID, Columns.UID, Columns.PROGRAM, Columns.TRACKED_ENTITY_ATTRIBUTE};
        Cursor cursor = database().query(ProgramTrackedEntityAttributeModel.TABLE, projection,
                null, null, null, null, null);
        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM, TRACKED_ENTITY_ATTRIBUTE);
        database().delete(ProgramModel.TABLE, ProgramModel.Columns.UID + " =?", new String[]{PROGRAM});
        cursor = database().query(ProgramTrackedEntityAttributeModel.TABLE, projection, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteProgramTrackedEntityAttributeWhenDeletingTrackedEntityAttribute() {
        ContentValues programTrackedEntityAttribute = new ContentValues();
        programTrackedEntityAttribute.put(Columns.ID, ID);
        programTrackedEntityAttribute.put(Columns.UID, UID);
        programTrackedEntityAttribute.put(Columns.PROGRAM, PROGRAM);
        programTrackedEntityAttribute.put(Columns.TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_ATTRIBUTE);

        database().insert(ProgramTrackedEntityAttributeModel.TABLE, null, programTrackedEntityAttribute);
        String[] projection = {Columns.ID, Columns.UID, Columns.PROGRAM, Columns.TRACKED_ENTITY_ATTRIBUTE};
        Cursor cursor = database().query(ProgramTrackedEntityAttributeModel.TABLE, projection,
                null, null, null, null, null);
        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM, TRACKED_ENTITY_ATTRIBUTE);

        database().delete(TrackedEntityAttributeModel.TABLE, TrackedEntityAttributeModel.Columns.UID + " =?",
                new String[]{TRACKED_ENTITY_ATTRIBUTE});
        cursor = database().query(ProgramTrackedEntityAttributeModel.TABLE, projection, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteProgramTrackedEntityAttributeWhenDeletingOptionSetNestedForeignKey() {
        ContentValues programTrackedEntityAttribute = new ContentValues();
        programTrackedEntityAttribute.put(Columns.ID, ID);
        programTrackedEntityAttribute.put(Columns.UID, UID);
        programTrackedEntityAttribute.put(Columns.PROGRAM, PROGRAM);
        programTrackedEntityAttribute.put(Columns.TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_ATTRIBUTE);

        database().insert(ProgramTrackedEntityAttributeModel.TABLE, null, programTrackedEntityAttribute);

        String[] projection = {Columns.ID, Columns.UID, Columns.PROGRAM, Columns.TRACKED_ENTITY_ATTRIBUTE};

        Cursor cursor = database().query(ProgramTrackedEntityAttributeModel.TABLE, projection,
                null, null, null, null, null);
        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM, TRACKED_ENTITY_ATTRIBUTE);
        database().delete(OptionSetModel.TABLE, OptionSetModel.Columns.UID + " =?", new String[]{OPTION_SET});
        database().delete(ProgramModel.TABLE, ProgramModel.Columns.UID + "=?", new String[]{PROGRAM});

        cursor = database().query(ProgramTrackedEntityAttributeModel.TABLE, projection, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    // ToDo: consider introducing conflict resolution strategy
}
