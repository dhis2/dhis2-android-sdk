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
public class ProgramStageDataElementStoreTests extends AbsStoreTestCase {
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

    private ProgramStageDataElementStore programStageDataElementStore;

    private final Date date;
    private final String dateString;

    public ProgramStageDataElementStoreTests() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.programStageDataElementStore = new ProgramStageDataElementStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistProgramStageDataElementInDatabase() {
        // inserting necessary foreign key
        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(DataElementModel.TABLE, null, dataElement);

        long rowId = programStageDataElementStore.insert(
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
                null
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistDeferrableProgramStageDataElementInDatabase() {
        final String deferredDataElementUid = "deferredDataElementUid";
        final String deferredProgramStageSectionUid = "deferredProgramStageSectionUid";

        database().beginTransaction();
        long rowId = programStageDataElementStore.insert(UID, CODE, NAME, DISPLAY_NAME, date, date,
                DISPLAY_IN_REPORTS, COMPULSORY, ALLOW_PROVIDED_ELSEWHERE, SORT_ORDER, ALLOW_FUTURE_DATE,
                deferredDataElementUid,
                deferredProgramStageSectionUid
        );
        ContentValues dataElement = CreateDataElementUtils.create(ID, deferredDataElementUid, null);
        ContentValues programStage = CreateProgramStageUtils.create(1L , PROGRAM_STAGE, PROGRAM);
        ContentValues programStageSection = CreateProgramStageSectionUtils.create(
                ID, deferredProgramStageSectionUid, PROGRAM_STAGE);
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
                deferredProgramStageSectionUid
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistProgramStageDataElementInDatabaseWithOptionSet() {
        // inserting necessary foreign key
        ContentValues optionSet = CreateOptionSetUtils.create(ID, OPTION_SET);
        database().insert(OptionSetModel.TABLE, null, optionSet);

        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, OPTION_SET);
        database().insert(DataElementModel.TABLE, null, dataElement);


        long rowId = programStageDataElementStore.insert(
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
                null
        ).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistProgramStageDataElementWithInvalidForeignKey() {
        String fakeDataElementId = "fake_data_element_id";
        programStageDataElementStore.insert(
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
                null
        );
    }

    @Test
    public void delete_shouldDeleteProgramStageDataElementWhenDeletingDataElement() {
        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(DataElementModel.TABLE, null, dataElement);

        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.ID, ID);
        programStageDataElement.put(Columns.UID, UID);
        programStageDataElement.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
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
    public void delete_shouldDeleteProgramStageDataElementWhenDeletingOptionSetNestedForeignKey() {
        ContentValues optionSet = CreateOptionSetUtils.create(ID, OPTION_SET);
        database().insert(OptionSetModel.TABLE, null, optionSet);

        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, OPTION_SET);
        database().insert(DataElementModel.TABLE, null, dataElement);

        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.ID, ID);
        programStageDataElement.put(Columns.UID, UID);
        programStageDataElement.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
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
    public void delete_shouldDeleteProgramStageDataElementWhenDeletingProgramStageSection() {
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
    public void close_shouldNotCloseDatabase() {
        programStageDataElementStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
