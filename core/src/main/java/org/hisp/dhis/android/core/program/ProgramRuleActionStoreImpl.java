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

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
public class ProgramRuleActionStoreImpl implements ProgramRuleActionStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + ProgramRuleActionModel.TABLE + " (" +
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
            ProgramRuleActionModel.Columns.PROGRAM_RULE +
            ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_STATEMENT = "UPDATE " + ProgramRuleActionModel.TABLE + " SET " +
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
    public int update(@NonNull String uid, @Nullable String code, @NonNull String name, @Nullable String displayName,
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
        int update = databaseAdapter.executeUpdateDelete(ProgramRuleActionModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return update;
    }

    @Override
    public int delete(String uid) {
        isNull(uid);
        // bind the where argument
        sqLiteBind(deleteStatement, 1, uid);

        // execute and clear bindings
        int delete = databaseAdapter.executeUpdateDelete(ProgramRuleActionModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();

        return delete;
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
}
