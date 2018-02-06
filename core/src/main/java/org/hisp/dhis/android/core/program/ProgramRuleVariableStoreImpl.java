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
public class ProgramRuleVariableStoreImpl extends Store implements ProgramRuleVariableStore {

    private static final String FIELDS =
            ProgramRuleVariableModel.Columns.UID + ", " +
                    ProgramRuleVariableModel.Columns.CODE + ", " +
                    ProgramRuleVariableModel.Columns.NAME + ", " +
                    ProgramRuleVariableModel.Columns.DISPLAY_NAME + ", " +
                    ProgramRuleVariableModel.Columns.CREATED + ", " +
                    ProgramRuleVariableModel.Columns.LAST_UPDATED + ", " +
                    ProgramRuleVariableModel.Columns.USE_CODE_FOR_OPTION_SET + ", " +
                    ProgramRuleVariableModel.Columns.PROGRAM + ", " +
                    ProgramRuleVariableModel.Columns.PROGRAM_STAGE + ", " +
                    ProgramRuleVariableModel.Columns.DATA_ELEMENT + ", " +
                    ProgramRuleVariableModel.Columns.TRACKED_ENTITY_ATTRIBUTE + ", " +
                    ProgramRuleVariableModel.Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE;

    private static final String QUERY_BY_UID_STATEMENT = "SELECT " + FIELDS +
            " FROM " + ProgramRuleVariableModel.TABLE +
            " WHERE " + ProgramRuleVariableModel.Columns.UID + "= ?";

    private static final String INSERT_STATEMENT = "INSERT INTO " +
            ProgramRuleVariableModel.TABLE + " (" +
            FIELDS
            + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + ProgramRuleVariableModel.TABLE +
            " SET " +
            ProgramRuleVariableModel.Columns.UID + " =?, " +
            ProgramRuleVariableModel.Columns.CODE + " =?, " +
            ProgramRuleVariableModel.Columns.NAME + " =?, " +
            ProgramRuleVariableModel.Columns.DISPLAY_NAME + " =?, " +
            ProgramRuleVariableModel.Columns.CREATED + " =?, " +
            ProgramRuleVariableModel.Columns.LAST_UPDATED + " =?, " +
            ProgramRuleVariableModel.Columns.USE_CODE_FOR_OPTION_SET + " =?, " +
            ProgramRuleVariableModel.Columns.PROGRAM + " =?, " +
            ProgramRuleVariableModel.Columns.PROGRAM_STAGE + " =?, " +
            ProgramRuleVariableModel.Columns.DATA_ELEMENT + " =?, " +
            ProgramRuleVariableModel.Columns.TRACKED_ENTITY_ATTRIBUTE + " =?, " +
            ProgramRuleVariableModel.Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE + " =? " +
            " WHERE " + ProgramRuleVariableModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + ProgramRuleVariableModel.TABLE +
            " WHERE " + ProgramRuleVariableModel.Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public ProgramRuleVariableStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
            @NonNull String displayName, @NonNull Date created,
            @NonNull Date lastUpdated, @Nullable Boolean useCodeForOptionSet,
            @NonNull String program, @Nullable String programStage,
            @Nullable String dataElement, @Nullable String trackedEntityAttribute,
            @Nullable ProgramRuleVariableSourceType programRuleVariableSourceType) {
        isNull(uid);
        isNull(program);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated,
                useCodeForOptionSet,
                program, programStage, dataElement, trackedEntityAttribute,
                programRuleVariableSourceType);

        // execute and clear bindings
        Long insert = databaseAdapter.executeInsert(ProgramRuleVariableModel.TABLE,
                insertStatement);
        insertStatement.clearBindings();
        return insert;
    }

    @Override
    public int update(@NonNull String uid, @Nullable String code, @NonNull String name,
            @NonNull String displayName,
            @NonNull Date created, @NonNull Date lastUpdated, @Nullable Boolean useCodeForOptionSet,
            @NonNull String program, @Nullable String programStage, @Nullable String dataElement,
            @Nullable String trackedEntityAttribute,
            @Nullable ProgramRuleVariableSourceType programRuleVariableSourceType,
            @NonNull String whereProgramRuleVariableUid) {
        isNull(uid);
        isNull(program);
        isNull(whereProgramRuleVariableUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated,
                useCodeForOptionSet,
                program, programStage, dataElement, trackedEntityAttribute,
                programRuleVariableSourceType);

        // bind the where argument
        sqLiteBind(updateStatement, 13, whereProgramRuleVariableUid);

        // execute and clear bindings
        int update = databaseAdapter.executeUpdateDelete(ProgramRuleVariableModel.TABLE,
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
        int delete = databaseAdapter.executeUpdateDelete(ProgramRuleVariableModel.TABLE,
                deleteStatement);
        deleteStatement.clearBindings();
        return delete;
    }

    @Override
    public ProgramRuleVariable queryByUid(String uid) {
        ProgramRuleVariable programRuleVariable = null;

        Cursor cursor = databaseAdapter.query(QUERY_BY_UID_STATEMENT, uid);

        if (cursor.getCount() > 0) {
            Map<String, List<ProgramRuleVariable>> programRuleMap = mapFromCursor(cursor);

            Map.Entry<String, List<ProgramRuleVariable>> entry =
                    programRuleMap.entrySet().iterator().next();

            programRuleVariable = entry.getValue().get(0);
        }

        return programRuleVariable;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement, @NonNull String uid,
            @Nullable String code,
            @NonNull String name, @NonNull String displayName, @NonNull Date created,
            @NonNull Date lastUpdated, @Nullable Boolean useCodeForOptionSet,
            @NonNull String program, @Nullable String programStage,
            @Nullable String dataElement, @Nullable String trackedEntityAttribute,
            @Nullable ProgramRuleVariableSourceType programRuleVariableSourceType) {
        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, useCodeForOptionSet);
        sqLiteBind(sqLiteStatement, 8, program);
        sqLiteBind(sqLiteStatement, 9, programStage);
        sqLiteBind(sqLiteStatement, 10, dataElement);
        sqLiteBind(sqLiteStatement, 11, trackedEntityAttribute);
        sqLiteBind(sqLiteStatement, 12, programRuleVariableSourceType.name());


    }

    @Override
    public int delete() {
        return databaseAdapter.delete(ProgramRuleVariableModel.TABLE);
    }

    private Map<String, List<ProgramRuleVariable>> mapFromCursor(Cursor cursor) {

        Map<String, List<ProgramRuleVariable>> programRuleVariablesMap = new HashMap<>();
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
                    Boolean useCodeForOptionSet = getBooleanFromCursor(cursor, 6);
                    String programUid = getStringFromCursor(cursor, 7);
                    String programStageUid = getStringFromCursor(cursor, 8);
                    String dataElementUid = getStringFromCursor(cursor, 9);
                    String trackedEntityAttributeUid = getStringFromCursor(cursor, 10);
                    ProgramRuleVariableSourceType programRuleVariableSourceType =
                            getProgramRuleVariableSourceTypeFromCursor(cursor, 11);


                    if (!programRuleVariablesMap.containsKey(programUid)) {
                        programRuleVariablesMap.put(programUid,
                                new ArrayList<ProgramRuleVariable>());
                    }

                    Program program = null;
                    ProgramStage programStage = null;
                    DataElement dataElement = null;
                    TrackedEntityAttribute trackedEntityAttribute = null;

                    if (programUid != null) {
                        program = Program.builder().uid(programUid).build();
                    }

                    if (programStageUid != null) {
                        programStage = ProgramStage.builder().uid(programStageUid).build();
                    }

                    if (dataElementUid != null) {
                        dataElement = createDataElement(dataElementUid);
                    }


                    if (trackedEntityAttributeUid != null) {
                        trackedEntityAttribute = createTrackedEntityAttribute(
                                trackedEntityAttributeUid);
                    }

                    programRuleVariablesMap.get(programUid).add(ProgramRuleVariable.builder()
                            .uid(uid)
                            .code(code)
                            .name(name)
                            .displayName(displayName)
                            .created(created)
                            .lastUpdated(lastUpdated)
                            .useCodeForOptionSet(useCodeForOptionSet)
                            .programRuleVariableSourceType(programRuleVariableSourceType)
                            .program(program)
                            .programStage(programStage)
                            .dataElement(dataElement)
                            .trackedEntityAttribute(trackedEntityAttribute)
                            .build());

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return programRuleVariablesMap;
    }

    private ProgramRuleVariableSourceType getProgramRuleVariableSourceTypeFromCursor(
            Cursor cursor, int index) {
        return cursor.getString(index) == null ? null : ProgramRuleVariableSourceType.valueOf(
                cursor.getString(index));
    }

    @NonNull
    private DataElement createDataElement(String dataElementUid) {
        return DataElement.builder().uid(dataElementUid).build();
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
}
