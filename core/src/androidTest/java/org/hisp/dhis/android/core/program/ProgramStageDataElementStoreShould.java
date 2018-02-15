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
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.dataelement.CreateDataElementUtils;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.option.CreateOptionSetUtils;
import org.hisp.dhis.android.core.option.OptionSetModel;
import org.hisp.dhis.android.core.program.ProgramStageDataElementModel.Columns;
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
public class ProgramStageDataElementStoreShould extends AbsStoreTestCase {
    private static final String[] PROJECTION = {
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.DISPLAY_IN_REPORTS,
            Columns.COMPULSORY,
            Columns.ALLOW_PROVIDED_ELSEWHERE,
            Columns.SORT_ORDER,
            Columns.ALLOW_FUTURE_DATE,
            Columns.DATA_ELEMENT,
            Columns.PROGRAM_STAGE,
            Columns.PROGRAM_STAGE_SECTION
    };

    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Boolean DISPLAY_IN_REPORTS = Boolean.TRUE;
    private static final Boolean COMPULSORY = Boolean.FALSE;
    private static final Boolean ALLOW_PROVIDED_ELSEWHERE = Boolean.FALSE;
    private static final Integer SORT_ORDER = 7;
    private static final Boolean ALLOW_FUTURE_DATE = Boolean.TRUE;
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String PROGRAM_STAGE_SECTION = "test_program_stage_section";
    // Nested foreign key
    private static final String OPTION_SET = "test_optionSet";
    private static final String PROGRAM = "test_program";
    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";
    private static final String PROGRAM_STAGE = "stageUid";

    private ProgramStageDataElementStore store;

    private final Date date;
    private final String dateString;

    public ProgramStageDataElementStoreShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.store = new ProgramStageDataElementStoreImpl(databaseAdapter());
    }

    @Test
    @MediumTest
    public void insert_persist_program_stage_data_element_in_data_base_when_insert() {
        // inserting necessary foreign key
        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(DataElementModel.TABLE, null, dataElement);

        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);

        long rowId = store.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date,
                date,
                DISPLAY_IN_REPORTS,
                COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE,
                SORT_ORDER,
                ALLOW_FUTURE_DATE,
                DATA_ELEMENT,
                PROGRAM_STAGE,
                null
        );

        Cursor cursor = database().query(ProgramStageDataElementModel.TABLE, PROJECTION,
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
                1, // DISPLAY_IN_REPORTS = Boolean.FALSE
                0, // COMPULSORY = Boolean.FALSE
                0, // ALLOW_PROVIDED_ELSEWHERE = Boolean.FALSE
                SORT_ORDER,
                1, // ALLOW_FUTURE_DATE = Boolean.TRUE
                DATA_ELEMENT,
                PROGRAM_STAGE,
                null

        ).isExhausted();
    }

    @Test
    @MediumTest
    public void insert_deferrable_program_stage_data_element_in_data_base_when_insert() {
        final String deferredDataElementUid = "deferredDataElementUid";
        final String deferredProgramStageUid = "deferredProgramStageUid";
        final String deferredProgramStageSectionUid = "deferredProgramStageSectionUid";

        database().beginTransaction();
        long rowId = store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date,
                DISPLAY_IN_REPORTS, COMPULSORY, ALLOW_PROVIDED_ELSEWHERE, SORT_ORDER, ALLOW_FUTURE_DATE,
                deferredDataElementUid,
                deferredProgramStageUid,
                deferredProgramStageSectionUid
        );
        ContentValues dataElement = CreateDataElementUtils.create(ID, deferredDataElementUid, null);
        ContentValues programStage = CreateProgramStageUtils.create(1L, deferredProgramStageUid, PROGRAM);
        ContentValues programStageSection = CreateProgramStageSectionUtils.create(
                ID, deferredProgramStageSectionUid, deferredProgramStageUid);
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(
                RELATIONSHIP_TYPE_ID, RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);

        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);
        database().insert(ProgramStageModel.TABLE, null, programStage);
        database().insert(ProgramStageSectionModel.TABLE, null, programStageSection);
        database().insert(DataElementModel.TABLE, null, dataElement);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(ProgramStageDataElementModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, CODE, NAME, DISPLAY_NAME, dateString, dateString, 1, 0, 0, SORT_ORDER, 1,
                deferredDataElementUid,
                deferredProgramStageUid,
                deferredProgramStageSectionUid
        ).isExhausted();
    }

    @Test
    @MediumTest
    public void insert_persist_program_stage_data_element_in_data_base_when_insert_with_option_set() {
        // inserting necessary foreign key
        ContentValues optionSet = CreateOptionSetUtils.create(ID, OPTION_SET);
        database().insert(OptionSetModel.TABLE, null, optionSet);

        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, OPTION_SET);
        database().insert(DataElementModel.TABLE, null, dataElement);

        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);

        long rowId = store.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date,
                date,
                DISPLAY_IN_REPORTS,
                COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE,
                SORT_ORDER,
                ALLOW_FUTURE_DATE,
                DATA_ELEMENT,
                PROGRAM_STAGE,
                null
        );

        Cursor cursor = database().query(ProgramStageDataElementModel.TABLE, PROJECTION,
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
                1, // DISPLAY_IN_REPORTS = Boolean.FALSE
                0, // COMPULSORY = Boolean.FALSE
                0, // ALLOW_PROVIDED_ELSEWHERE = Boolean.FALSE
                SORT_ORDER,
                1, // ALLOW_FUTURE_DATE = Boolean.TRUE
                DATA_ELEMENT,
                PROGRAM_STAGE,
                null
        ).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    @MediumTest
    public void throw_sqlite_constraint_exception_when_insert_program_stage_data_element_with_invalid_foreign_key() {
        String fakeDataElementId = "fake_data_element_id";
        store.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date,
                date,
                DISPLAY_IN_REPORTS,
                COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE,
                SORT_ORDER,
                ALLOW_FUTURE_DATE,
                fakeDataElementId,
                PROGRAM_STAGE,
                null
        );
    }

    @Test
    @MediumTest
    public void delete_program_stage_data_element_in_data_base_when_delete_data_element() {
        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(DataElementModel.TABLE, null, dataElement);

        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);

        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.ID, ID);
        programStageDataElement.put(Columns.UID, UID);
        programStageDataElement.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
        programStageDataElement.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        database().insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement);

        String[] projection = {Columns.ID, Columns.UID, Columns.DATA_ELEMENT};

        Cursor cursor = database().query(ProgramStageDataElementModel.TABLE, projection, null, null, null, null, null);
        // Checking that programStageDataElement was inserted
        assertThatCursor(cursor).hasRow(ID, UID, DATA_ELEMENT).isExhausted();

        // deleting data element
        database().delete(DataElementModel.TABLE, DataElementModel.Columns.UID + "=?", new String[]{DATA_ELEMENT});

        cursor = database().query(ProgramStageDataElementModel.TABLE, projection, null, null, null, null, null);

        // program stage data element should now be deleted when foreign key was deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_program_stage_data_element_in_data_base_when_delete_option_set_nested_foreign_key() {
        ContentValues optionSet = CreateOptionSetUtils.create(ID, OPTION_SET);
        database().insert(OptionSetModel.TABLE, null, optionSet);

        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, OPTION_SET);
        database().insert(DataElementModel.TABLE, null, dataElement);

        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);

        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.ID, ID);
        programStageDataElement.put(Columns.UID, UID);
        programStageDataElement.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
        programStageDataElement.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        database().insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement);

        String[] projection = {Columns.ID, Columns.UID, Columns.DATA_ELEMENT};

        Cursor cursor = database().query(ProgramStageDataElementModel.TABLE, projection, null, null, null, null, null);
        // Checking that programStageDataElement was inserted
        assertThatCursor(cursor).hasRow(ID, UID, DATA_ELEMENT).isExhausted();

        // deleting optionSet
        database().delete(OptionSetModel.TABLE, OptionSetModel.Columns.UID + "=?", new String[]{OPTION_SET});

        cursor = database().query(ProgramStageDataElementModel.TABLE, projection, null, null, null, null, null);

        // program stage data element should now be deleted when nested foreign key was deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_program_stage_data_element_in_data_base_when_delete_program_stage_section() {
        String programStageUid = "test_programStageUid";

        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);

        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(ID, programStageUid, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);

        ContentValues programStageSection =
                CreateProgramStageSectionUtils.create(ID, PROGRAM_STAGE_SECTION, programStageUid);

        database().insert(ProgramStageSectionModel.TABLE, null, programStageSection);

        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(DataElementModel.TABLE, null, dataElement);

        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.ID, ID);
        programStageDataElement.put(Columns.UID, UID);
        programStageDataElement.put(Columns.PROGRAM_STAGE_SECTION, PROGRAM_STAGE_SECTION);
        programStageDataElement.put(Columns.PROGRAM_STAGE, programStageUid);
        programStageDataElement.put(Columns.DATA_ELEMENT, DATA_ELEMENT);

        database().insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement);

        String[] projection = {Columns.ID, Columns.UID, Columns.PROGRAM_STAGE_SECTION, Columns.DATA_ELEMENT};

        Cursor cursor = database().query(ProgramStageDataElementModel.TABLE, projection, null, null, null, null, null);
        // Checking that programStageDataElement was inserted
        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM_STAGE_SECTION, DATA_ELEMENT).isExhausted();

        // deleting referenced program stage section
        database().delete(
                ProgramStageSectionModel.TABLE,
                ProgramStageSectionModel.Columns.UID + "=?", new String[]{PROGRAM_STAGE_SECTION});

        cursor = database().query(ProgramStageDataElementModel.TABLE, projection, null, null, null, null, null);

        // checking that program stage data element is deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void update__program_stage_data_element_in_data_base_without_program_stage_section_when_update() throws Exception {
        // inserting mandatory and nested foreign keys
        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(DataElementModel.TABLE, null, dataElement);

        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);

        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.UID, UID);
        programStageDataElement.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
        programStageDataElement.put(Columns.COMPULSORY, COMPULSORY);
        programStageDataElement.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
        programStageDataElement.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);

        database().insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement);

        String[] projection = {Columns.UID, Columns.DISPLAY_NAME, Columns.COMPULSORY};

        Cursor cursor = database().query(ProgramStageDataElementModel.TABLE, projection,
                null, null, null, null, null);

        // checking that psde was successfully inserted
        assertThatCursor(cursor).hasRow(UID, DISPLAY_NAME, 0); // 0 == Boolean.FALSE
        String updatedDisplayName = "updatedDisplayName";
        Boolean updatedCompulsory = Boolean.TRUE;

        int update = store.update(
                UID, CODE, NAME,
                updatedDisplayName, date, date,
                DISPLAY_IN_REPORTS, updatedCompulsory,
                ALLOW_PROVIDED_ELSEWHERE,
                SORT_ORDER,
                ALLOW_FUTURE_DATE,
                DATA_ELEMENT,
                PROGRAM_STAGE,
                UID);

        // checking that store returns 1 when update was successful
        assertThat(update).isEqualTo(1);

        cursor = database().query(ProgramStageDataElementModel.TABLE, projection,
                null, null, null, null, null);

        // checking that row was updated
        assertThatCursor(cursor).hasRow(UID, updatedDisplayName, 1); // 1 == Boolean.TRUE

    }


    @Test
    @MediumTest
    public void delete_program_stage_data_element_when_delete() throws Exception {
        // inserting necessary foreign keys
        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(DataElementModel.TABLE, null, dataElement);

        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);

        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.UID, UID);
        programStageDataElement.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
        programStageDataElement.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);

        database().insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement);

        String[] projection = {Columns.UID};

        Cursor cursor = database().query(ProgramStageDataElementModel.TABLE, projection, null, null, null, null, null);
        // checking that psde was successfully inserted
        assertThatCursor(cursor).hasRow(UID);

        int delete = store.delete(UID);

        // checking that store returns 1 when successful delete
        assertThat(delete).isEqualTo(1);

        cursor = database().query(ProgramStageDataElementModel.TABLE, projection, null, null, null, null, null);

        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void update_program_stage_data_element_with_program_stage_section_when_update() throws Exception {
        // inserting necessary foreign keys
        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(DataElementModel.TABLE, null, dataElement);

        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);

        ContentValues programStageSection = CreateProgramStageSectionUtils.create(
                1L, PROGRAM_STAGE_SECTION, PROGRAM_STAGE
        );
        database().insert(ProgramStageSectionModel.TABLE, null, programStageSection);

        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.UID, UID);
        programStageDataElement.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
        programStageDataElement.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        // null program stage section
        programStageDataElement.putNull(Columns.PROGRAM_STAGE_SECTION);

        database().insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement);

        String[] projection = {Columns.UID, Columns.DATA_ELEMENT, Columns.PROGRAM_STAGE_SECTION};

        Cursor cursor = database().query(ProgramStageDataElementModel.TABLE, projection, null, null, null, null, null);
        // checking that psde was successfully inserted
        assertThatCursor(cursor).hasRow(UID, DATA_ELEMENT, null);

        // update program stage data element programStageSection link based on data element
        store.updateWithProgramStageSectionLink(PROGRAM_STAGE_SECTION, DATA_ELEMENT);

        cursor = database().query(ProgramStageDataElementModel.TABLE, projection, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(UID, DATA_ELEMENT, PROGRAM_STAGE_SECTION);

    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_uid_and_null_program_stage_uid() {
        store.insert(null, CODE, NAME, DISPLAY_NAME, date, date, DISPLAY_IN_REPORTS, COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE, SORT_ORDER, ALLOW_FUTURE_DATE, DATA_ELEMENT, PROGRAM_STAGE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_with_null_dataElement_and_program_stage_uid() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, DISPLAY_IN_REPORTS, COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE, SORT_ORDER, ALLOW_FUTURE_DATE, null, PROGRAM_STAGE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_with_program_stage_uid_and_program_stage_section() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, DISPLAY_IN_REPORTS, COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE, SORT_ORDER, ALLOW_FUTURE_DATE, DATA_ELEMENT, null, null);
    }


    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_with_null_uid() {
        store.update(null, CODE, NAME, DISPLAY_NAME, date, date, DISPLAY_IN_REPORTS, COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE, SORT_ORDER, ALLOW_FUTURE_DATE, DATA_ELEMENT, PROGRAM_STAGE, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_with_null_data_element() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, DISPLAY_IN_REPORTS, COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE, SORT_ORDER, ALLOW_FUTURE_DATE, null, PROGRAM_STAGE, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_with_null_program_stage_uid() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, DISPLAY_IN_REPORTS, COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE, SORT_ORDER, ALLOW_FUTURE_DATE, DATA_ELEMENT, null, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_with_null_where_program_stage_data_element_uid() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, DISPLAY_IN_REPORTS, COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE, SORT_ORDER, ALLOW_FUTURE_DATE, DATA_ELEMENT, PROGRAM_STAGE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_delete_null_uid() {
        store.delete(null);
    }
}
