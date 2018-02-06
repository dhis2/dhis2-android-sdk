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

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.Store;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.NPathComplexity",
        "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.AvoidInstantiatingObjectsInLoops",
        "PMD.ExcessiveMethodLength"
})
public class ProgramRuleActionStoreImpl extends Store implements ProgramRuleActionStore {

    private static final String FIELDS =
            ProgramRuleActionModel.Columns.UID + ", " +
                    ProgramRuleActionModel.Columns.CODE + ", " +
                    ProgramRuleActionModel.Columns.NAME + ", " +
                    ProgramRuleActionModel.Columns.DISPLAY_NAME + ", " +
                    ProgramRuleActionModel.Columns.CREATED + ", " +
                    ProgramRuleActionModel.Columns.LAST_UPDATED + ", " +
                    ProgramRuleActionModel.Columns.DATA + ", " +
                    ProgramRuleActionModel.Columns.CONTENT + ", " +
                    ProgramRuleActionModel.Columns.LOCATION + ", " +
                    ProgramRuleActionModel.Columns.TRACKED_ENTITY_ATTRIBUTE + ", " +
                    ProgramRuleActionModel.Columns.PROGRAM_INDICATOR + ", " +
                    ProgramRuleActionModel.Columns.PROGRAM_STAGE_SECTION + ", " +
                    ProgramRuleActionModel.Columns.PROGRAM_RULE_ACTION_TYPE + ", " +
                    ProgramRuleActionModel.Columns.PROGRAM_STAGE + ", " +
                    ProgramRuleActionModel.Columns.DATA_ELEMENT + ", " +
                    ProgramRuleActionModel.Columns.PROGRAM_RULE;

    private static final String QUERY_BY_UID_STATEMENT =
            "SELECT " + FIELDS + " FROM " + ProgramRuleActionModel.TABLE +
                    " WHERE " + ProgramRuleActionModel.Columns.UID + " =?";

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + ProgramRuleActionModel.TABLE + " (" +
                    FIELDS +
                    ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_STATEMENT =
            "UPDATE " + ProgramRuleActionModel.TABLE + " SET " +
                    ProgramRuleActionModel.Columns.UID + " =?, " +
                    ProgramRuleActionModel.Columns.CODE + " =?, " +
                    ProgramRuleActionModel.Columns.NAME + " =?, " +
                    ProgramRuleActionModel.Columns.DISPLAY_NAME + " =?, " +
                    ProgramRuleActionModel.Columns.CREATED + " =?, " +
                    ProgramRuleActionModel.Columns.LAST_UPDATED + " =?, " +
                    ProgramRuleActionModel.Columns.DATA + " =?, " +
                    ProgramRuleActionModel.Columns.CONTENT + " =?, " +
                    ProgramRuleActionModel.Columns.LOCATION + " =?, " +
                    ProgramRuleActionModel.Columns.TRACKED_ENTITY_ATTRIBUTE + " =?, " +
                    ProgramRuleActionModel.Columns.PROGRAM_INDICATOR + " =?, " +
                    ProgramRuleActionModel.Columns.PROGRAM_STAGE_SECTION + " =?, " +
                    ProgramRuleActionModel.Columns.PROGRAM_RULE_ACTION_TYPE + " =?, " +
                    ProgramRuleActionModel.Columns.PROGRAM_STAGE + " =?, " +
                    ProgramRuleActionModel.Columns.DATA_ELEMENT + " =?, " +
                    ProgramRuleActionModel.Columns.PROGRAM_RULE + " =? " +
                    " WHERE " +
                    ProgramRuleActionModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + ProgramRuleActionModel.TABLE +
            " WHERE " +
            ProgramRuleActionModel.Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public ProgramRuleActionStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }


    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
            @Nullable String displayName, @NonNull Date created,
            @NonNull Date lastUpdated, @Nullable String data, @Nullable String content,
            @Nullable String location,
            @Nullable String trackedEntityAttribute,
            @Nullable String programIndicator,
            @Nullable String programStageSection,
            @NonNull ProgramRuleActionType programRuleActionType,
            @Nullable String programStage,
            @Nullable String dataElement,
            @Nullable String programRule) {
        isNull(uid);
        isNull(programRule);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated, data,
                content, location, trackedEntityAttribute, programIndicator, programStageSection,
                programRuleActionType, programStage, dataElement, programRule);

        // execute and clear bindings
        Long insert = databaseAdapter.executeInsert(ProgramRuleActionModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return insert;


    }

    @Override
    public int update(@NonNull String uid, @Nullable String code, @NonNull String name,
            @Nullable String displayName,
            @NonNull Date created, @NonNull Date lastUpdated, @Nullable String data,
            @Nullable String content, @Nullable String location,
            @Nullable String trackedEntityAttribute,
            @Nullable String programIndicator,
            @Nullable String programStageSection,
            @NonNull ProgramRuleActionType programRuleActionType,
            @Nullable String programStage,
            @Nullable String dataElement,
            @Nullable String programRule,
            @NonNull String whereProgramRuleActionUid) {
        isNull(uid);
        isNull(programRule);
        isNull(whereProgramRuleActionUid);
        bindArguments(updateStatement,
                uid, code, name, displayName, created, lastUpdated, data,
                content, location, trackedEntityAttribute, programIndicator, programStageSection,
                programRuleActionType, programStage, dataElement, programRule);

        // bind the where argument
        sqLiteBind(updateStatement, 17, whereProgramRuleActionUid);

        // execute and clear bindings
        int update = databaseAdapter.executeUpdateDelete(ProgramRuleActionModel.TABLE,
                updateStatement);
        updateStatement.clearBindings();

        return update;
    }

    @Override
    public int delete(String uid) {
        isNull(uid);
        // bind the where argument
        sqLiteBind(deleteStatement, 1, uid);

        // execute and clear bindings
        int delete = databaseAdapter.executeUpdateDelete(ProgramRuleActionModel.TABLE,
                deleteStatement);
        deleteStatement.clearBindings();

        return delete;
    }

    @Override
    public ProgramRuleAction queryByUid(String uid) {
        ProgramRuleAction programRuleAction = null;

        Cursor cursor = databaseAdapter.query(QUERY_BY_UID_STATEMENT, uid);

        if (cursor.getCount() > 0) {
            Map<String, List<ProgramRuleAction>> programRuleMap = mapFromCursor(cursor);

            Map.Entry<String, List<ProgramRuleAction>> entry =
                    programRuleMap.entrySet().iterator().next();

            programRuleAction = entry.getValue().get(0);
        }

        return programRuleAction;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement,
            @NonNull String uid, @Nullable String code, @NonNull String name,
            @Nullable String displayName, @NonNull Date created,
            @NonNull Date lastUpdated, @Nullable String data, @Nullable String content,
            @Nullable String location,
            @Nullable String trackedEntityAttribute,
            @Nullable String programIndicator,
            @Nullable String programStageSection,
            @NonNull ProgramRuleActionType programRuleActionType,
            @Nullable String programStage,
            @Nullable String dataElement,
            @Nullable String programRule) {

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, data);
        sqLiteBind(sqLiteStatement, 8, content);
        sqLiteBind(sqLiteStatement, 9, location);
        sqLiteBind(sqLiteStatement, 10, trackedEntityAttribute);
        sqLiteBind(sqLiteStatement, 11, programIndicator);
        sqLiteBind(sqLiteStatement, 12, programStageSection);
        sqLiteBind(sqLiteStatement, 13, programRuleActionType);
        sqLiteBind(sqLiteStatement, 14, programStage);
        sqLiteBind(sqLiteStatement, 15, dataElement);
        sqLiteBind(sqLiteStatement, 16, programRule);
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(ProgramRuleActionModel.TABLE);
    }

    private Map<String, List<ProgramRuleAction>> mapFromCursor(Cursor cursor) {

        Map<String, List<ProgramRuleAction>> programRulesMap = new HashMap<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {

                    String uid = getStringFromCursor(cursor, 0);
                    String code = getStringFromCursor(cursor, 1);
                    String name = getStringFromCursor(cursor, 2);
                    String displayName = getStringFromCursor(cursor, 3);
                    Date created = getDateFromCursor(cursor, 4);
                    Date lastUpdated = getDateFromCursor(cursor, 5);
                    String data = getStringFromCursor(cursor, 6);
                    String content = getStringFromCursor(cursor, 7);
                    String location = getStringFromCursor(cursor, 8);
                    String trackedEntityAttributeUid = getStringFromCursor(cursor, 9);
                    String programIndicatorUid = getStringFromCursor(cursor, 10);
                    String programStageSectionUid = getStringFromCursor(cursor, 11);
                    ProgramRuleActionType programRuleActionType =
                            getProgramRuleActionType(cursor, 12);
                    String programStageUid = getStringFromCursor(cursor, 13);
                    String dataElementUid = getStringFromCursor(cursor, 14);
                    String programRuleUid = getStringFromCursor(cursor, 15);

                    if (!programRulesMap.containsKey(programRuleUid)) {
                        programRulesMap.put(programRuleUid, new ArrayList<ProgramRuleAction>());
                    }

                    ProgramStage programStage = null;
                    TrackedEntityAttribute trackedEntityAttribute = null;
                    ProgramIndicator programIndicator = null;
                    ProgramStageSection programStageSection = null;
                    DataElement dataElement = null;
                    ProgramRule programRule = null;

                    if (programStageUid != null) {
                        programStage = ProgramStage.builder().uid(programStageUid).build();
                    }

                    if (trackedEntityAttributeUid != null) {
                        trackedEntityAttribute = createTrackedEntityAttribute(
                                trackedEntityAttributeUid);
                    }

                    if (programIndicatorUid != null) {
                        programIndicator = ProgramIndicator.builder().uid(
                                programIndicatorUid).build();
                    }

                    if (programStageSectionUid != null) {
                        programStageSection = createProgramStageSection(programStageSectionUid);
                    }

                    if (dataElementUid != null) {
                        dataElement = createDataElement(dataElementUid);
                    }

                    if (programRuleUid != null) {
                        programRule = ProgramRule.builder().uid(programRuleUid).build();
                    }

                    programRulesMap.get(programRuleUid).add(ProgramRuleAction.builder()
                            .uid(uid)
                            .code(code)
                            .name(name)
                            .displayName(displayName)
                            .created(created)
                            .lastUpdated(lastUpdated)
                            .data(data)
                            .content(content)
                            .location(location)
                            .trackedEntityAttribute(trackedEntityAttribute)
                            .programIndicator(programIndicator)
                            .programStageSection(programStageSection)
                            .programRuleActionType(programRuleActionType)
                            .programStage(programStage)
                            .dataElement(dataElement)
                            .programRule(programRule)
                            .build());

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return programRulesMap;
    }

    @NonNull
    private DataElement createDataElement(String dataElementUid) {
        return DataElement.builder()
                .uid(dataElementUid)
                .build();
    }

    @NonNull
    private ProgramStageSection createProgramStageSection(String programStageSectionUid) {
        //TODO: this will refactor when we create ProgramStageSection.Builder
        return ProgramStageSection.create(programStageSectionUid, null, null
                , null, null, null, null,
                null, null, null);
    }

    @NonNull
    private TrackedEntityAttribute createTrackedEntityAttribute(
            String trackedEntityAttributeUid) {

        return TrackedEntityAttribute.builder()
                .uid(trackedEntityAttributeUid)
                .sortOrderInListNoProgram(0)
                .programScope(false)
                .displayInListNoProgram(false)
                .generated(false)
                .displayOnVisitSchedule(false)
                .orgUnitScope(false)
                .unique(false)
                .inherit(false)
                .deleted(false).build();
    }

    @Nullable
    private ProgramRuleActionType getProgramRuleActionType(Cursor cursor, int index) {
        return cursor.getString(index) == null ? null : ProgramRuleActionType.valueOf(
                cursor.getString(index));
    }
}
