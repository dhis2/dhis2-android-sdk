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
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.program.ProgramStageSectionModel.Columns;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramStageSectionStoreShould extends AbsStoreTestCase {
    private static final String[] PROJECTION = {
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.SORT_ORDER,
            Columns.PROGRAM_STAGE
    };
    private static final long ID = 2L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Integer SORT_ORDER = 7;
    private static final String PROGRAM_STAGE = "test_program_stage";
    // nested foreign key
    private static final String PROGRAM = "test_program";
    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 1L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";

    private final Date date;
    private final String dateString;

    private ProgramStageSectionStore store;

    public ProgramStageSectionStoreShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.store = new ProgramStageSectionStoreImpl(databaseAdapter());

        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);

        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);
        //Create ProgramStage & insert a row in the table:
        ContentValues programStage = CreateProgramStageUtils.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);
    }

    @Test
    public void insert_program_stage_section_in_data_base_when_insert() {

        long rowId = store.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date,
                date,
                SORT_ORDER,
                PROGRAM_STAGE
        );

        Cursor cursor = database().query(ProgramStageSectionModel.TABLE,
                PROJECTION, null, null, null, null, null);

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
                SORT_ORDER,
                PROGRAM_STAGE
        ).isExhausted();
    }

    @Test
    public void insert_deferrable_program_stage_section_in_data_base_when_insert() {
        final String deferredProgramStage = "deferredProgramStage";

        database().beginTransaction();
        long rowId = store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, SORT_ORDER,
                deferredProgramStage
        );
        ContentValues programStage = CreateProgramStageUtils.create(3L, deferredProgramStage, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(ProgramStageSectionModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, CODE, NAME, DISPLAY_NAME, dateString, dateString, SORT_ORDER,
                deferredProgramStage
        ).isExhausted();
    }

    @Test
    public void delete_program_stage_section_in_data_base_when_delete_program_stage() {

        ContentValues programStageSection = new ContentValues();
        programStageSection.put(Columns.ID, ID);
        programStageSection.put(Columns.UID, UID);
        programStageSection.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        database().insert(ProgramStageSectionModel.TABLE, null, programStageSection);

        String[] projection = {Columns.ID, Columns.UID, Columns.PROGRAM_STAGE};
        Cursor cursor = database().query(ProgramStageSectionModel.TABLE, projection,
                null, null, null, null, null);
        // checking that program stage section was successfully inserted
        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM_STAGE).isExhausted();

        // deleting foreign key reference
        database().delete(ProgramStageModel.TABLE,
                ProgramStageModel.Columns.UID + "=?", new String[]{PROGRAM_STAGE});

        cursor = database().query(ProgramStageSectionModel.TABLE, projection,
                null, null, null, null, null);
        // checking that program stage section is deleted.
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_program_stage_section_with_invalid_foreign_key() {
        String WRONG_UID = "wrong";
        store.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date,
                date,
                SORT_ORDER,
                WRONG_UID // supplying wrong uid
        );
    }

    @Test
    public void update_in_data_base_when_update() throws Exception {
        // insertion of foreign key: program stage happens in the setUp method

        // insert program stage section into database
        ContentValues programStageSection = new ContentValues();
        programStageSection.put(Columns.UID, UID);
        programStageSection.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
        programStageSection.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        database().insert(ProgramStageSectionModel.TABLE, null, programStageSection);

        String[] projection = {Columns.UID, Columns.DISPLAY_NAME};
        Cursor cursor = database().query(ProgramStageSectionModel.TABLE, projection, null, null, null, null, null);

        // check that row was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID, DISPLAY_NAME);
        String updatedDisplayName = "updated_display_name";

        // update program stage section with new display name
        int update = store.update(
                UID, CODE, NAME, updatedDisplayName,
                date, date, SORT_ORDER, PROGRAM_STAGE, UID
        );

        // check that store returns 1 after successful update
        assertThat(update).isEqualTo(1);
        cursor = database().query(ProgramStageSectionModel.TABLE, projection, null, null, null, null, null);

        // check that row was updated
        assertThatCursor(cursor).hasRow(UID, updatedDisplayName);
    }

    @Test
    public void delete_in_data_base_when_delete() throws Exception {
        // insertion of foreign key: program stage happens in the setUp method

        // insert program stage section into database
        ContentValues programStageSection = new ContentValues();
        programStageSection.put(Columns.UID, UID);
        programStageSection.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
        programStageSection.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        database().insert(ProgramStageSectionModel.TABLE, null, programStageSection);

        String[] projection = {Columns.UID};

        Cursor cursor = database().query(ProgramStageSectionModel.TABLE, projection, null, null, null, null, null);
        // check that program stage section was successfully inserted
        assertThatCursor(cursor).hasRow(UID);

        // delete program stage section
        int delete = store.delete(UID);

        // check that store returns 1 (deletion happen)
        assertThat(delete).isEqualTo(1);

        cursor = database().query(ProgramStageSectionModel.TABLE, projection, null, null, null, null, null);

        // check that row doesn't exist in database
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        store.insert(null, CODE, NAME, DISPLAY_NAME, date, date, SORT_ORDER, PROGRAM_STAGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_program_stage() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, SORT_ORDER, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_uid() {
        store.update(null, CODE, NAME, DISPLAY_NAME, date, date, SORT_ORDER, PROGRAM_STAGE, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_program_stage() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, SORT_ORDER, null, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_where_uid() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, SORT_ORDER, PROGRAM_STAGE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_delete_null_uid() {
        store.delete(null);
    }
}
