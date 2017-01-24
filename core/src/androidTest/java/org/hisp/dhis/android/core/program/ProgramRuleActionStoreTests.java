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
import org.hisp.dhis.android.core.program.ProgramRuleActionModel.Columns;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
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
public class ProgramRuleActionStoreTests extends AbsStoreTestCase {
    private static final Long ID = 2L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Date CREATED = new Date();
    private static final Date LAST_UPDATED = CREATED;
    private static final String DATA = "test_data";
    private static final String CONTENT = "test_content";
    private static final String LOCATION = "test_location";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_trackedEntityAttribute";
    private static final String PROGRAM_INDICATOR = "test_programIndicator";
    private static final String PROGRAM_STAGE_SECTION = "test_programStageSection";
    private static final ProgramRuleActionType PROGRAM_RULE_ACTION_TYPE = ProgramRuleActionType.ASSIGN;
    private static final String PROGRAM_STAGE = "test_programStage";
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String PROGRAM_RULE = "test_programRule";

    // nested foreign key
    private static final String PROGRAM = "test_program";

    public static final String[] PROGRAM_RULE_ACTION_PROJECTION = {
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.DATA,
            Columns.CONTENT,
            Columns.LOCATION,
            Columns.TRACKED_ENTITY_ATTRIBUTE,
            Columns.PROGRAM_INDICATOR,
            Columns.PROGRAM_STAGE_SECTION,
            Columns.PROGRAM_RULE_ACTION_TYPE,
            Columns.PROGRAM_STAGE,
            Columns.DATA_ELEMENT,
            Columns.PROGRAM_RULE
    };

    private ProgramRuleActionStore programRuleActionStore;

    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        programRuleActionStore = new ProgramRuleActionStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() throws ParseException {
        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);

        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programRule = CreateProgramRuleUtils.createWithoutProgramStage(1L, PROGRAM_RULE, PROGRAM);
        database().insert(ProgramRuleModel.TABLE, null, programRule);

        long rowId = programRuleActionStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                CREATED,
                LAST_UPDATED,
                DATA,
                CONTENT,
                LOCATION,
                null,
                null,
                null,
                PROGRAM_RULE_ACTION_TYPE,
                null,
                null,
                PROGRAM_RULE
        );

        Cursor cursor = database().query(ProgramRuleActionModel.TABLE,
                PROGRAM_RULE_ACTION_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(
                        UID,
                        CODE,
                        NAME,
                        DISPLAY_NAME,
                        BaseIdentifiableObject.DATE_FORMAT.format(CREATED),
                        BaseIdentifiableObject.DATE_FORMAT.format(LAST_UPDATED),
                        DATA,
                        CONTENT,
                        LOCATION,
                        null,
                        null,
                        null,
                        PROGRAM_RULE_ACTION_TYPE,
                        null,
                        null,
                        PROGRAM_RULE)
                .isExhausted();
    }

    @Test
    public void insert_shouldPersistRowInDatabaseWithProgramStageAsNestedForeignKey() {
        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);

        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(1L, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);

        ContentValues programRule =
                CreateProgramRuleUtils.createWithProgramStage(
                        1L, PROGRAM_RULE, PROGRAM, PROGRAM_STAGE);

        database().insert(ProgramRuleModel.TABLE, null, programRule);

        long rowId = programRuleActionStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                CREATED,
                LAST_UPDATED,
                DATA,
                CONTENT,
                LOCATION,
                null,
                null,
                null,
                PROGRAM_RULE_ACTION_TYPE,
                PROGRAM_STAGE,
                null,
                PROGRAM_RULE
        );

        Cursor cursor = database().query(ProgramRuleActionModel.TABLE,
                PROGRAM_RULE_ACTION_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(
                        UID,
                        CODE,
                        NAME,
                        DISPLAY_NAME,
                        BaseIdentifiableObject.DATE_FORMAT.format(CREATED),
                        BaseIdentifiableObject.DATE_FORMAT.format(LAST_UPDATED),
                        DATA,
                        CONTENT,
                        LOCATION,
                        null,
                        null,
                        null,
                        PROGRAM_RULE_ACTION_TYPE,
                        PROGRAM_STAGE,
                        null,
                        PROGRAM_RULE)
                .isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_shouldFailWithoutMandatoryForeignKey() {
        programRuleActionStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                CREATED,
                LAST_UPDATED,
                DATA,
                CONTENT,
                LOCATION,
                TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_INDICATOR,
                PROGRAM_STAGE_SECTION,
                PROGRAM_RULE_ACTION_TYPE,
                PROGRAM_STAGE,
                DATA_ELEMENT,
                null
        );
    }

    @Test
    public void delete_shouldDeleteProgramRuleActionWhenDeletingProgramRule() throws Exception {
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);

        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);

        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programRule = CreateProgramRuleUtils.createWithoutProgramStage(ID, PROGRAM_RULE, PROGRAM);
        database().insert(ProgramRuleModel.TABLE, null, programRule);

        ContentValues programRuleAction = new ContentValues();
        programRuleAction.put(Columns.ID, ID);
        programRuleAction.put(Columns.UID, UID);
        programRuleAction.put(Columns.PROGRAM_RULE, PROGRAM_RULE);
        database().insert(ProgramRuleActionModel.TABLE, null, programRuleAction);

        String[] projection = {Columns.ID, Columns.UID, Columns.PROGRAM_RULE};

        Cursor cursor = database().query(ProgramRuleActionModel.TABLE, projection, null, null, null, null, null);
        // checking that program rule action was successfully inserted
        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM_RULE).isExhausted();
        // deleting program rule
        database().delete(ProgramRuleModel.TABLE, ProgramRuleModel.Columns.UID + " =?", new String[]{PROGRAM_RULE});

        cursor = database().query(ProgramRuleActionModel.TABLE, projection, null, null, null, null, null);
        // checking that program rule action is deleted
        assertThatCursor(cursor).isExhausted();

    }

    // ToDo: consider introducing conflict resolution strategy

    @Test
    public void close_shouldNotCloseDatabase() {
        programRuleActionStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
