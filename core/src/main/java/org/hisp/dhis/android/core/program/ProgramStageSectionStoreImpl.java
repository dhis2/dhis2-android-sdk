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

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
public class ProgramStageSectionStoreImpl implements ProgramStageSectionStore {

    private static final String EXIST_BY_UID_STATEMENT = "SELECT " +
            ProgramStageSectionModel.Columns.UID +
            " FROM " + ProgramStageSectionModel.TABLE +
            " WHERE " + ProgramStageSectionModel.Columns.UID + " =?;";

    private static final String INSERT_STATEMENT = "INSERT INTO " +
            ProgramStageSectionModel.TABLE + " (" +
            ProgramStageSectionModel.Columns.UID + ", " +
            ProgramStageSectionModel.Columns.CODE + ", " +
            ProgramStageSectionModel.Columns.NAME + ", " +
            ProgramStageSectionModel.Columns.DISPLAY_NAME + ", " +
            ProgramStageSectionModel.Columns.CREATED + ", " +
            ProgramStageSectionModel.Columns.LAST_UPDATED + ", " +
            ProgramStageSectionModel.Columns.SORT_ORDER + ", " +
            ProgramStageSectionModel.Columns.PROGRAM_STAGE + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + ProgramStageSectionModel.TABLE + " SET " +
            ProgramStageSectionModel.Columns.UID + " =?, " +
            ProgramStageSectionModel.Columns.CODE + " =?, " +
            ProgramStageSectionModel.Columns.NAME + " =?, " +
            ProgramStageSectionModel.Columns.DISPLAY_NAME + " =?, " +
            ProgramStageSectionModel.Columns.CREATED + " =?, " +
            ProgramStageSectionModel.Columns.LAST_UPDATED + " =?, " +
            ProgramStageSectionModel.Columns.SORT_ORDER + " =?, " +
            ProgramStageSectionModel.Columns.PROGRAM_STAGE + " =? " +
            " WHERE " + ProgramStageSectionModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + ProgramStageSectionModel.TABLE +
            " WHERE " + ProgramStageSectionModel.Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public ProgramStageSectionStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code,
                       @NonNull String name, @NonNull String displayName,
                       @NonNull Date created, @NonNull Date lastUpdated,
                       @Nullable Integer sortOrder, @Nullable String programStage) {
        isNull(uid);
        isNull(programStage);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated, sortOrder, programStage);

        Long insert = databaseAdapter.executeInsert(ProgramStageSectionModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return insert;
    }

    @Override
    public int update(@NonNull String uid, @Nullable String code, @NonNull String name, @NonNull String displayName,
                      @NonNull Date created, @NonNull Date lastUpdated, @Nullable Integer sortOrder,
                      @Nullable String programStage, @NonNull String whereProgramStageSectionUid) {
        isNull(uid);
        isNull(programStage);
        isNull(whereProgramStageSectionUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated, sortOrder, programStage);

        // bind the where argument
        sqLiteBind(updateStatement, 9, whereProgramStageSectionUid);

        // execute and clear bindings
        int update = databaseAdapter.executeUpdateDelete(ProgramStageSectionModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return update;
    }

    @Override
    public int delete(String uid) {
        isNull(uid);
        sqLiteBind(deleteStatement, 1, uid);
        int delete = databaseAdapter.executeUpdateDelete(ProgramStageSectionModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();

        return delete;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement, @NonNull String uid, @Nullable String code,
                               @NonNull String name, @NonNull String displayName,
                               @NonNull Date created, @NonNull Date lastUpdated, @Nullable Integer sortOrder,
                               @Nullable String programStage) {
        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, sortOrder);
        sqLiteBind(sqLiteStatement, 8, programStage);
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(ProgramStageSectionModel.TABLE);
    }

    @Override
    public Boolean exists(String uId) {
        Cursor cursor = databaseAdapter.query(EXIST_BY_UID_STATEMENT, uId);
        return cursor.getCount() > 0;
    }

}
