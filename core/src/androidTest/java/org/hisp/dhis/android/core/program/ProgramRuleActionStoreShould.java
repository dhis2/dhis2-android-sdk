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
import org.hisp.dhis.android.core.program.ProgramRuleActionModel.Columns;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramRuleActionStoreShould extends AbsStoreTestCase {
    public static final String[] PROJECTION = {
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
    private static final Long ID = 2L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
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
    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";
    // nested foreign key
    private static final String PROGRAM = "test_program";

    private ProgramRuleActionStore store;

    private final Date date;
    private final String dateString;

    public ProgramRuleActionStoreShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        store = new ProgramRuleActionStoreImpl(databaseAdapter());
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

        //Nullable foreign keys:
        ContentValues trackedEntityAttribute = CreateTrackedEntityUtils.create(1L, TRACKED_ENTITY_ATTRIBUTE);
        database().insert(TrackedEntityAttributeModel.TABLE, null, trackedEntityAttribute);

        ContentValues programStage = CreateProgramStageUtils.create(2L, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);

        ContentValues programStageSection = CreateProgramStageSectionUtils.create(1L,
                PROGRAM_STAGE_SECTION, PROGRAM_STAGE);
        database().insert(ProgramStageSectionModel.TABLE, null, programStageSection);

        ContentValues dataElement = CreateDataElementUtils.create(1L, DATA_ELEMENT, null);
        database().insert(DataElementModel.TABLE, null, dataElement);

        ContentValues programIndicator = CreateProgramIndicatorUtils.create(1L, PROGRAM_INDICATOR, PROGRAM);
        database().insert(ProgramIndicatorModel.TABLE, null, programIndicator);
    }

    @Test
    public void persist_row_in_data_base_when_insert() {
        long rowId = store.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date,
                date,
                DATA,
                CONTENT,
                LOCATION,
                TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_INDICATOR,
                PROGRAM_STAGE_SECTION,
                PROGRAM_RULE_ACTION_TYPE,
                PROGRAM_STAGE,
                DATA_ELEMENT,
                PROGRAM_RULE
        );
        Cursor cursor = database().query(ProgramRuleActionModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                dateString,
                dateString,
                DATA,
                CONTENT,
                LOCATION,
                TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_INDICATOR,
                PROGRAM_STAGE_SECTION,
                PROGRAM_RULE_ACTION_TYPE,
                PROGRAM_STAGE,
                DATA_ELEMENT,
                PROGRAM_RULE
        ).isExhausted();
    }

    @Test
    public void persist_deferrable_row_in_data_base_when_insert() {
        final String deferredProgramRule = "deferredProgramRule";
        final String deferredTrackedEntityAttribute = "deferredTrackedEntityAttribute";
        final String deferredProgramStageSection = "deferredProgramStageSection";
        final String deferredProgramStage = "deferredProgramStage";
        final String deferredDataElement = "deferredDataElement";

        database().beginTransaction();
        long rowId = store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, DATA, CONTENT, LOCATION,
                deferredTrackedEntityAttribute,
                PROGRAM_INDICATOR,
                deferredProgramStageSection,
                PROGRAM_RULE_ACTION_TYPE,
                deferredProgramStage,
                deferredDataElement,
                deferredProgramRule
        );
        ContentValues programRule = CreateProgramRuleUtils.createWithoutProgramStage(3L, deferredProgramRule, PROGRAM);
        database().insert(ProgramRuleModel.TABLE, null, programRule);
        ContentValues trackedEntityAttribute = CreateTrackedEntityUtils.create(3L, deferredTrackedEntityAttribute);
        database().insert(TrackedEntityAttributeModel.TABLE, null, trackedEntityAttribute);
        ContentValues programStage = CreateProgramStageUtils.create(3L, deferredProgramStage, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);
        ContentValues programStageSection = CreateProgramStageSectionUtils.create(3L,
                deferredProgramStageSection, deferredProgramStage);
        database().insert(ProgramStageSectionModel.TABLE, null, programStageSection);
        ContentValues dataElement = CreateDataElementUtils.create(3L, deferredDataElement, null);
        database().insert(DataElementModel.TABLE, null, dataElement);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(ProgramRuleActionModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, CODE, NAME, DISPLAY_NAME, dateString, dateString, DATA, CONTENT, LOCATION,
                deferredTrackedEntityAttribute,
                PROGRAM_INDICATOR,
                deferredProgramStageSection,
                PROGRAM_RULE_ACTION_TYPE,
                deferredProgramStage,
                deferredDataElement,
                deferredProgramRule
        ).isExhausted();
    }

    @Test
    public void persist_nullable_row_in_data_base_when_insert() {
        long rowId = store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, DATA, CONTENT, LOCATION,
                null,
                null,
                null,
                PROGRAM_RULE_ACTION_TYPE,
                null,
                null,
                PROGRAM_RULE
        );
        Cursor cursor = database().query(ProgramRuleActionModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, CODE, NAME, DISPLAY_NAME, dateString, dateString, DATA, CONTENT, LOCATION,
                null,
                null,
                null,
                PROGRAM_RULE_ACTION_TYPE,
                null,
                null,
                PROGRAM_RULE
        ).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_wrong_mandatory_foreign_key() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, DATA, CONTENT, LOCATION,
                TRACKED_ENTITY_ATTRIBUTE, PROGRAM_INDICATOR, PROGRAM_STAGE_SECTION, PROGRAM_RULE_ACTION_TYPE,
                PROGRAM_STAGE, DATA_ELEMENT, "wrong"
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_wrong_tracked_entity_attribute_foreign_key() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, DATA, CONTENT, LOCATION,
                "wrong", PROGRAM_INDICATOR, PROGRAM_STAGE_SECTION, PROGRAM_RULE_ACTION_TYPE,
                PROGRAM_STAGE, DATA_ELEMENT, PROGRAM
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_wrong_program_stage_section_foreign_key() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, DATA, CONTENT, LOCATION,
                TRACKED_ENTITY_ATTRIBUTE, PROGRAM_INDICATOR, "wrong", PROGRAM_RULE_ACTION_TYPE,
                PROGRAM_STAGE, DATA_ELEMENT, PROGRAM
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_wrong_program_stage_foreign_key() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, DATA, CONTENT, LOCATION,
                TRACKED_ENTITY_ATTRIBUTE, PROGRAM_INDICATOR, PROGRAM_STAGE_SECTION, PROGRAM_RULE_ACTION_TYPE,
                "wrong", DATA_ELEMENT, PROGRAM
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_wrong_data_element_foreign_key() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, DATA, CONTENT, LOCATION,
                TRACKED_ENTITY_ATTRIBUTE, PROGRAM_INDICATOR, PROGRAM_STAGE_SECTION, PROGRAM_RULE_ACTION_TYPE,
                PROGRAM_STAGE, "wrong", PROGRAM
        );
    }

    @Test
    public void delete_program_rule_action_when_delete_program_rule() throws Exception {
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

    @Test
    public void update_program_rule_action_in_data_base_when_update() throws Exception {
        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programRule = CreateProgramRuleUtils.createWithoutProgramStage(ID, PROGRAM_RULE, PROGRAM);
        database().insert(ProgramRuleModel.TABLE, null, programRule);

        ContentValues programRuleAction = new ContentValues();
        programRuleAction.put(Columns.UID, UID);
        programRuleAction.put(Columns.LOCATION, LOCATION);
        programRuleAction.put(Columns.PROGRAM_RULE, PROGRAM_RULE);
        database().insert(ProgramRuleActionModel.TABLE, null, programRuleAction);

        String[] projection = {Columns.UID, Columns.LOCATION};

        Cursor cursor = database().query(ProgramRuleActionModel.TABLE, projection, null, null, null, null, null);
        // check that program rule action was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID, LOCATION);
        String updatedLocation = "updated_location_program_rule_action";
        int update = store.update(
                UID, CODE, NAME, DISPLAY_NAME, date, date,
                DATA, CONTENT, updatedLocation, null, null, null,
                PROGRAM_RULE_ACTION_TYPE, null, null, PROGRAM_RULE, UID);

        // check that store returns 1 on successful update
        assertThat(update).isEqualTo(1);

        cursor = database().query(ProgramRuleActionModel.TABLE, projection, null, null, null, null, null);

        // check that program rule action is updated in database
        assertThatCursor(cursor).hasRow(UID, updatedLocation).isExhausted();
    }

    @Test
    public void delete_program_rule_action_in_data_base_when_delete() throws Exception {
        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programRule = CreateProgramRuleUtils.createWithoutProgramStage(ID, PROGRAM_RULE, PROGRAM);
        database().insert(ProgramRuleModel.TABLE, null, programRule);

        ContentValues programRuleAction = new ContentValues();
        programRuleAction.put(Columns.UID, UID);
        programRuleAction.put(Columns.PROGRAM_RULE, PROGRAM_RULE);
        database().insert(ProgramRuleActionModel.TABLE, null, programRuleAction);

        String[] projection = {Columns.UID};

        Cursor cursor = database().query(ProgramRuleActionModel.TABLE, projection, null, null, null, null, null);
        // check that program rule action was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID);

        // Delete program rule action
        int delete = store.delete(UID);

        // check that store returns 1 on successful delete
        assertThat(delete).isEqualTo(1);

        cursor = database().query(ProgramRuleActionModel.TABLE, projection, null, null, null, null, null);

        // check that program rule action is deleted in database
        assertThatCursor(cursor).isExhausted();
    }

    // ToDo: consider introducing conflict resolution strategy

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        store.insert(null, CODE, NAME, DISPLAY_NAME, date, date, DATA, CONTENT, LOCATION, TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_INDICATOR, PROGRAM_STAGE_SECTION, PROGRAM_RULE_ACTION_TYPE, PROGRAM_STAGE, DATA_ELEMENT,
                PROGRAM_RULE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_programRule() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, DATA, CONTENT, LOCATION, TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_INDICATOR, PROGRAM_STAGE_SECTION, PROGRAM_RULE_ACTION_TYPE, PROGRAM_STAGE, DATA_ELEMENT,
                null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_uid() {
        store.update(null, CODE, NAME, DISPLAY_NAME, date, date, DATA, CONTENT, LOCATION, TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_INDICATOR, PROGRAM_STAGE_SECTION, PROGRAM_RULE_ACTION_TYPE, PROGRAM_STAGE, DATA_ELEMENT,
                PROGRAM_RULE, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_program_rule() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, DATA, CONTENT, LOCATION, TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_INDICATOR, PROGRAM_STAGE_SECTION, PROGRAM_RULE_ACTION_TYPE, PROGRAM_STAGE, DATA_ELEMENT,
                null, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_where_uid() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, DATA, CONTENT, LOCATION, TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_INDICATOR, PROGRAM_STAGE_SECTION, PROGRAM_RULE_ACTION_TYPE, PROGRAM_STAGE, DATA_ELEMENT,
                PROGRAM_RULE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_delete_null_uid() {
        store.delete(null);
    }
}
