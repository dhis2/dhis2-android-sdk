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
import org.hisp.dhis.android.core.program.ProgramRuleModel.Columns;

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
        "PMD.AvoidInstantiatingObjectsInLoops"
})
public class ProgramRuleStoreImpl extends Store implements ProgramRuleStore {
    private static final String FIELDS =
            Columns.UID + ", " +
                    Columns.CODE + ", " +
                    Columns.NAME + ", " +
                    Columns.DISPLAY_NAME + ", " +
                    Columns.CREATED + ", " +
                    Columns.LAST_UPDATED + ", " +
                    Columns.PRIORITY + ", " +
                    Columns.CONDITION + ", " +
                    Columns.PROGRAM + ", " +
                    Columns.PROGRAM_STAGE;

    private static final String EXIST_BY_UID_STATEMENT = "SELECT " +
            ProgramRuleModel.Columns.UID +
            " FROM " + ProgramRuleModel.TABLE +
            " WHERE " + ProgramRuleModel.Columns.UID + " =?;";

    private static final String QUERY_BY_UID_STATEMENT =
            "SELECT " + FIELDS + " FROM " + ProgramRuleModel.TABLE + " WHERE " +
                    ProgramRuleModel.Columns.UID + "=?";

    private static final String INSERT_STATEMENT = "INSERT INTO " + ProgramRuleModel.TABLE + " (" +
            FIELDS + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + ProgramRuleModel.TABLE + " SET " +
            Columns.UID + " =?, " +
            Columns.CODE + " =?, " +
            Columns.NAME + " =?, " +
            Columns.DISPLAY_NAME + " =?, " +
            Columns.CREATED + " =?, " +
            Columns.LAST_UPDATED + " =?, " +
            Columns.PRIORITY + " =?, " +
            Columns.CONDITION + " =?, " +
            Columns.PROGRAM + " =?, " +
            Columns.PROGRAM_STAGE + " =? " +
            " WHERE " +
            Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + ProgramRuleModel.TABLE +
            " WHERE " +
            Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public ProgramRuleStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
            @NonNull String displayName, @NonNull Date created,
            @NonNull Date lastUpdated, @Nullable Integer priority,
            @Nullable String condition, @NonNull String program,
            @Nullable String programStage) {
        isNull(uid);
        isNull(program);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated, priority,
                condition, program, programStage);

        Long insert = databaseAdapter.executeInsert(ProgramRuleModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return insert;
    }

    @Override
    public int update(@NonNull String uid, @Nullable String code, @NonNull String name,
            @NonNull String displayName,
            @NonNull Date created, @NonNull Date lastUpdated, @Nullable Integer priority,
            @Nullable String condition, @NonNull String program, @Nullable String programStage,
            @NonNull String whereProgramRuleUid) {
        isNull(uid);
        isNull(program);
        isNull(whereProgramRuleUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated, priority,
                condition, program, programStage);

        // bind the where argument
        sqLiteBind(updateStatement, 11, whereProgramRuleUid);

        int update = databaseAdapter.executeUpdateDelete(ProgramRuleModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return update;
    }

    @Override
    public int delete(String uid) {
        isNull(uid);
        // bind the where argument
        sqLiteBind(deleteStatement, 1, uid);

        // execute and clear bindings
        int delete = databaseAdapter.executeUpdateDelete(ProgramRuleModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();

        return delete;
    }

    @Override
    public ProgramRule queryByUid(String uid) {
        ProgramRule programRule = null;

        Cursor cursor = databaseAdapter.query(QUERY_BY_UID_STATEMENT, uid);

        if (cursor.getCount() > 0) {
            Map<String, List<ProgramRule>> programRuleMap = mapFromCursor(cursor);

            Map.Entry<String, List<ProgramRule>> entry =
                    programRuleMap.entrySet().iterator().next();
            programRule = entry.getValue().get(0);
        }

        return programRule;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement, @NonNull String uid,
            @Nullable String code,
            @NonNull String name, @NonNull String displayName, @NonNull Date created,
            @NonNull Date lastUpdated, @Nullable Integer priority,
            @Nullable String condition, @NonNull String program,
            @Nullable String programStage) {
        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, priority);
        sqLiteBind(sqLiteStatement, 8, condition);
        sqLiteBind(sqLiteStatement, 9, program);
        sqLiteBind(sqLiteStatement, 10, programStage);
    }

    @Override
    public Boolean exists(String uId) {
        Cursor cursor = databaseAdapter.query(EXIST_BY_UID_STATEMENT, uId);
        return cursor.getCount() > 0;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(ProgramRuleModel.TABLE);
    }

    private Map<String, List<ProgramRule>> mapFromCursor(Cursor cursor) {

        Map<String, List<ProgramRule>> programRulesMap = new HashMap<>();
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
                    Integer priority = getIntegerFromCursor(cursor, 6);
                    String condition = getStringFromCursor(cursor, 7);
                    String program = getStringFromCursor(cursor, 8);
                    String programStageUid = getStringFromCursor(cursor, 9);

                    if (!programRulesMap.containsKey(program)) {
                        programRulesMap.put(program, new ArrayList<ProgramRule>());
                    }

                    ProgramStage programStage = null;

                    if (programStageUid != null) {
                        programStage = ProgramStage.builder().uid(programStageUid).build();
                    }

                    programRulesMap.get(program).add(ProgramRule.builder()
                            .uid(uid)
                            .code(code)
                            .name(name)
                            .displayName(displayName)
                            .created(created)
                            .lastUpdated(lastUpdated)
                            .priority(priority)
                            .condition(condition)
                            .program(Program.builder().uid(program).build())
                            .programStage(programStage)
                            .build());

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return programRulesMap;
    }

}
