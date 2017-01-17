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
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.program.ProgramStageModel.Columns;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramStageModelStoreIntegrationTest extends AbsStoreTestCase {
    public static final String[] PROGRAM_STAGE_PROJECTION = {
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.EXECUTION_DATE_LABEL,
            Columns.ALLOW_GENERATE_NEXT_VISIT,
            Columns.VALID_COMPLETE_ONLY,
            Columns.REPORT_DATE_TO_USE,
            Columns.OPEN_AFTER_ENROLLMENT,
            Columns.REPEATABLE,
            Columns.CAPTURE_COORDINATES,
            Columns.FORM_TYPE,
            Columns.DISPLAY_GENERATE_EVENT_BOX,
            Columns.GENERATED_BY_ENROLMENT_DATE,
            Columns.AUTO_GENERATE_EVENT,
            Columns.SORT_ORDER,
            Columns.HIDE_DUE_DATE,
            Columns.BLOCK_ENTRY_FORM,
            Columns.MIN_DAYS_FROM_START,
            Columns.STANDARD_INTERVAL,
            Columns.PROGRAM
    };

    private ProgramStageStore programStageStore;

    private static final long ID = 3L;

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final String EXECUTION_DATE_LABEL = "test_executionDateLabel";
    private static final Boolean ALLOW_GENERATE_NEXT_VISIT = Boolean.FALSE;
    private static final Boolean VALID_COMPLETE_ONLY = Boolean.FALSE;
    private static final String REPORT_DATE_TO_USE = "test_reportDateToUse";
    private static final Boolean OPEN_AFTER_ENROLLMENT = Boolean.FALSE;
    private static final Boolean REPEATABLE = Boolean.TRUE;
    private static final Boolean CAPTURE_COORDINATES = Boolean.TRUE;
    private static final FormType FORM_TYPE = FormType.CUSTOM;
    private static final Boolean DISPLAY_GENERATE_EVENT_BOX = Boolean.FALSE;
    private static final Boolean GENERATED_BY_ENROLMENT_DATE = Boolean.TRUE;
    private static final Boolean AUTO_GENERATE_EVENT = Boolean.FALSE;
    private static final Integer SORT_ORDER = 0;
    private static final Boolean HIDE_DUE_DATE = Boolean.TRUE;
    private static final Boolean BLOCK_ENTRY_FORM = Boolean.FALSE;
    private static final Integer MIN_DAYS_FROM_START = 2;
    private static final Integer STANDARD_INTERVAL = 3;
    private static final String PROGRAM = "test_program";

    // timestamp
    private static final String DATE = "2017-01-05T10:40:00.000";

    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        programStageStore = new ProgramStageStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() throws ParseException {
        insertForeignKeys();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programStageStore.insert(
                UID, CODE, NAME, DISPLAY_NAME,
                timeStamp, timeStamp, EXECUTION_DATE_LABEL, ALLOW_GENERATE_NEXT_VISIT,
                VALID_COMPLETE_ONLY, REPORT_DATE_TO_USE, OPEN_AFTER_ENROLLMENT,
                REPEATABLE, CAPTURE_COORDINATES, FORM_TYPE, DISPLAY_GENERATE_EVENT_BOX,
                GENERATED_BY_ENROLMENT_DATE, AUTO_GENERATE_EVENT, SORT_ORDER,
                HIDE_DUE_DATE, BLOCK_ENTRY_FORM, MIN_DAYS_FROM_START, STANDARD_INTERVAL,
                PROGRAM
        );
        Cursor cursor = database().query(Tables.PROGRAM_STAGE, PROGRAM_STAGE_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                DATE, DATE,
                EXECUTION_DATE_LABEL,
                0, // ALLOW_GENERATE_NEXT_VISIT = Boolean.FALSE
                0, // VALID_COMPLETE_ONLY = Boolean.FALSE
                REPORT_DATE_TO_USE,
                0, // OPEN_AFTER_ENROLLMENT = Boolean.FALSE
                1, // REPEATABLE = Boolean.TRUE
                1, // CAPTURE_COORDINATES = Boolean.TRUE
                FORM_TYPE,
                0, // DISPLAY_GENERATE_EVENT_BOX = Boolean.FALSE
                1, // GENERATED_BY_ENROLMENT_DATE = Boolean.TRUE
                0, // AUTO_GENERATE_EVENT = Boolean.FALSE
                SORT_ORDER,
                1, // HIDE_DUE_DATE = Boolean.TRUE
                0, // BLOCK_ENTRY_FORM = Boolean.FALSE
                MIN_DAYS_FROM_START,
                STANDARD_INTERVAL,
                PROGRAM
        ).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_shouldNotPersistProgramStageInDatabaseWithoutProgram() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        programStageStore.insert(
                UID, CODE, NAME, DISPLAY_NAME,
                timeStamp, timeStamp, EXECUTION_DATE_LABEL, ALLOW_GENERATE_NEXT_VISIT,
                VALID_COMPLETE_ONLY, REPORT_DATE_TO_USE, OPEN_AFTER_ENROLLMENT,
                REPEATABLE, CAPTURE_COORDINATES, FORM_TYPE, DISPLAY_GENERATE_EVENT_BOX,
                GENERATED_BY_ENROLMENT_DATE, AUTO_GENERATE_EVENT, SORT_ORDER,
                HIDE_DUE_DATE, BLOCK_ENTRY_FORM, MIN_DAYS_FROM_START, STANDARD_INTERVAL,
                null
        );
    }

    @Test
    public void delete_shouldDeleteProgramStageWhenDeletingProgram() {
        insertForeignKeys();

        ContentValues programStage = new ContentValues();
        programStage.put(Columns.ID, ID);
        programStage.put(Columns.UID, UID);
        programStage.put(Columns.PROGRAM, PROGRAM);

        database().insert(Tables.PROGRAM_STAGE, null, programStage);

        String[] projection = {Columns.ID, Columns.UID, Columns.PROGRAM};

        Cursor cursor = database().query(Tables.PROGRAM_STAGE, projection, null, null, null, null, null);
        // checking that program stage was successfully inserted
        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM).isExhausted();

        database().delete(Tables.PROGRAM, ProgramModel.Columns.UID + "=?", new String[]{PROGRAM});

        cursor = database().query(Tables.PROGRAM_STAGE, projection, null, null, null, null, null);

        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        programStageStore.close();

        assertThat(database().isOpen()).isTrue();
    }

    /**
     * Inserts the rows necessary to satisfy the foreign keys:
     * Program needs TrackedEntity and RelationshipType.
     */
    private void insertForeignKeys() {
        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, TRACKED_ENTITY_UID);

        database().insert(DbOpenHelper.Tables.TRACKED_ENTITY, null, trackedEntity);
        database().insert(DbOpenHelper.Tables.RELATIONSHIP_TYPE, null, relationshipType);
        database().insert(DbOpenHelper.Tables.PROGRAM, null, program);
    }
}
