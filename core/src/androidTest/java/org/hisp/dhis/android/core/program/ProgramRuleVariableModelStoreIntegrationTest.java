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
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.dataelement.CreateDataElementUtils;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.program.ProgramRuleVariableModel.Columns;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityAttributeUtils;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramRuleVariableModelStoreIntegrationTest extends AbsStoreTestCase {
    private static final long ID = 2L;

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    // timestamp
    private static final String DATE = "2017-01-09T14:48:00.000";

    private static final Boolean USE_CODE_FOR_OPTION_SET = Boolean.TRUE;
    private static final String PROGRAM = "test_program";
    private static final String PROGRAM_STAGE = "test_programStage";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_trackedEntityAttribute";
    private static final String DATA_ELEMENT = "test_dataElement";

    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";


    private static final ProgramRuleVariableSourceType PROGRAM_RULE_VARIABLE_SOURCE_TYPE =
            ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM;

    private static final String[] PROGRAM_RULE_VARIABLE_PROJECTION = {
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.USE_CODE_FOR_OPTION_SET,
            Columns.PROGRAM,
            Columns.PROGRAM_STAGE,
            Columns.DATA_ELEMENT,
            Columns.TRACKED_ENTITY_ATTRIBUTE,
            Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE
    };

    private ProgramRuleVariableModelStore programRuleVariableModelStore;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        programRuleVariableModelStore = new ProgramRuleVariableModelStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistProgramRuleVariableInDatabase() throws ParseException {
        insertProgramForeignKey();

        ContentValues programStage = ProgramStageModelIntegrationTest.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.PROGRAM_STAGE, null, programStage);

        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(DataElementModel.DATA_ELEMENT, null, dataElement);

        ContentValues trackedEntityAttribute =
                CreateTrackedEntityAttributeUtils.create(ID, TRACKED_ENTITY_ATTRIBUTE, null);

        database().insert(TrackedEntityAttributeModel.TRACKED_ENTITY_ATTRIBUTE, null, trackedEntityAttribute);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programRuleVariableModelStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                timeStamp,
                timeStamp,
                USE_CODE_FOR_OPTION_SET,
                PROGRAM,
                PROGRAM_STAGE,
                DATA_ELEMENT,
                TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        );

        Cursor cursor = database().query(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, PROGRAM_RULE_VARIABLE_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                DATE,
                DATE,
                1, // USE_CODE_FOR_OPTION_SET = Boolean.TRUE
                PROGRAM,
                PROGRAM_STAGE,
                DATA_ELEMENT,
                TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        ).isExhausted();

    }

    @Test
    public void insert_shouldPersistProgramRuleVariableInDatabaseWithProgramForeignKey() throws ParseException {
        insertProgramForeignKey();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programRuleVariableModelStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                timeStamp,
                timeStamp,
                USE_CODE_FOR_OPTION_SET,
                PROGRAM,
                null,
                null,
                null,
                PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        );

        Cursor cursor = database().query(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, PROGRAM_RULE_VARIABLE_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                DATE,
                DATE,
                1, // USE_CODE_FOR_OPTION_SET = Boolean.TRUE
                PROGRAM,
                null,
                null,
                null,
                PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        ).isExhausted();

    }

    @Test
    public void delete_shouldDeleteProgramRuleVariableWhenDeletingProgram() {
        insertProgramForeignKey();

        ContentValues programRuleVariable = new ContentValues();
        programRuleVariable.put(Columns.ID, ID);
        programRuleVariable.put(Columns.UID, UID);
        programRuleVariable.put(Columns.PROGRAM, PROGRAM);

        database().insert(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, null, programRuleVariable);

        String[] PROJECTION = {Columns.ID, Columns.UID, Columns.PROGRAM};

        Cursor cursor = database().query(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM).isExhausted();

        database().delete(ProgramModel.PROGRAM, ProgramModel.Columns.UID + " =?", new String[]{PROGRAM});

        cursor = database().query(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldNotDeleteProgramRuleVariableWhenDeletingProgramStage() {
        insertProgramForeignKey();

        ContentValues programStage = CreateProgramStageUtils.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.PROGRAM_STAGE, null, programStage);

        ContentValues programRuleVariable = new ContentValues();
        programRuleVariable.put(Columns.ID, ID);
        programRuleVariable.put(Columns.UID, UID);
        programRuleVariable.put(Columns.PROGRAM, PROGRAM);
        programRuleVariable.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);

        database().insert(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, null, programRuleVariable);

        String[] PROJECTION = {Columns.ID, Columns.UID, Columns.PROGRAM, Columns.PROGRAM_STAGE};

        Cursor cursor = database().query(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM, PROGRAM_STAGE);

        database().delete(ProgramStageModel.PROGRAM_STAGE, ProgramStageModel.Columns.UID + " =?", new String[]{PROGRAM_STAGE});

        cursor = database().query(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, PROJECTION,
                null, null, null, null, null);

        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldNotDeleteProgramRuleVariableWhenDeletingDataElement() {
        insertProgramForeignKey();

        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(DataElementModel.DATA_ELEMENT, null, dataElement);

        ContentValues programRuleVariable = new ContentValues();
        programRuleVariable.put(Columns.ID, ID);
        programRuleVariable.put(Columns.UID, UID);
        programRuleVariable.put(Columns.PROGRAM, PROGRAM);
        programRuleVariable.put(Columns.DATA_ELEMENT, DATA_ELEMENT);

        database().insert(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, null, programRuleVariable);

        String[] PROJECTION = {Columns.ID, Columns.UID, Columns.PROGRAM, Columns.DATA_ELEMENT};

        Cursor cursor = database().query(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM, DATA_ELEMENT);

        database().delete(DataElementModel.DATA_ELEMENT, DataElementModel.Columns.UID + " =?", new String[]{DATA_ELEMENT});

        cursor = database().query(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, PROJECTION,
                null, null, null, null, null);

        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldNotDeleteProgramRuleVariableWhenDeletingTrackedEntityAttribute() {
        insertProgramForeignKey();

        ContentValues trackedEntityAttribute = CreateTrackedEntityAttributeUtils.create(ID, TRACKED_ENTITY_ATTRIBUTE, null);
        database().insert(TrackedEntityAttributeModel.TRACKED_ENTITY_ATTRIBUTE, null, trackedEntityAttribute);

        ContentValues programRuleVariable = new ContentValues();
        programRuleVariable.put(Columns.ID, ID);
        programRuleVariable.put(Columns.UID, UID);
        programRuleVariable.put(Columns.PROGRAM, PROGRAM);
        programRuleVariable.put(Columns.TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_ATTRIBUTE);

        database().insert(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, null, programRuleVariable);

        String[] PROJECTION = {Columns.ID, Columns.UID, Columns.PROGRAM, Columns.TRACKED_ENTITY_ATTRIBUTE};

        Cursor cursor = database().query(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM, TRACKED_ENTITY_ATTRIBUTE);

        database().delete(TrackedEntityAttributeModel.TRACKED_ENTITY_ATTRIBUTE,
                TrackedEntityAttributeModel.Columns.UID + " =?", new String[]{TRACKED_ENTITY_ATTRIBUTE});

        cursor = database().query(ProgramRuleVariableModel.PROGRAM_RULE_VARIABLE, PROJECTION,
                null, null, null, null, null);

        assertThatCursor(cursor).isExhausted();

    }

    @Test
    public void close_shouldNotCloseDatabase() {
        programRuleVariableModelStore.close();

        assertThat(database().isOpen()).isTrue();
    }

    private void insertProgramForeignKey() {
        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);

        database().insert(TrackedEntityModel.TRACKED_ENTITY, null, trackedEntity);
        database().insert(RelationshipTypeModel.RELATIONSHIP_TYPE, null, relationshipType);
        database().insert(ProgramModel.PROGRAM, null, program);
    }
}

