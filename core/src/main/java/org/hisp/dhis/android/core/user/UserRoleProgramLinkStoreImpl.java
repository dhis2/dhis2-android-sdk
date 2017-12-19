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
package org.hisp.dhis.android.core.user;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.user.UserRoleProgramLinkModel.Columns;

public class UserRoleProgramLinkStoreImpl implements UserRoleProgramLinkStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + UserRoleProgramLinkModel.TABLE + " (" +
            Columns.USER_ROLE + ", " + Columns.PROGRAM + ") " +
            "VALUES (?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + UserRoleProgramLinkModel.TABLE +
            " SET " + Columns.USER_ROLE + "=?," + Columns.PROGRAM + "=?" +
            " WHERE " + Columns.USER_ROLE + "=?" + " AND " + Columns.PROGRAM + "=?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + UserRoleProgramLinkModel.TABLE +
            " WHERE " + Columns.USER_ROLE + " =?" + " AND " + Columns.PROGRAM + "=?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public UserRoleProgramLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String userRole, @NonNull String program) {
        isNull(userRole);
        isNull(program);
        sqLiteBind(insertStatement, 1, userRole);
        sqLiteBind(insertStatement, 2, program);

        Long insert = insertStatement.executeInsert();
        insertStatement.clearBindings();

        return insert;
    }

    @Override
    public int update(@NonNull String userRoleUid, @NonNull String programUid,
                      @NonNull String whereUserRoleUid, @NonNull String whereProgramUid) {
        isNull(userRoleUid);
        isNull(programUid);
        isNull(whereUserRoleUid);
        isNull(whereProgramUid);
        sqLiteBind(updateStatement, 1, userRoleUid);
        sqLiteBind(updateStatement, 2, programUid);
        sqLiteBind(updateStatement, 3, whereUserRoleUid);
        sqLiteBind(updateStatement, 4, whereProgramUid);

        int update = databaseAdapter.executeUpdateDelete(UserRoleProgramLinkModel.TABLE, updateStatement);
        updateStatement.clearBindings();
        return update;
    }

    @Override
    public int delete(@NonNull String userRoleUid, @NonNull String programUid) {
        isNull(userRoleUid);
        isNull(programUid);
        sqLiteBind(deleteStatement, 1, userRoleUid);
        sqLiteBind(deleteStatement, 2, programUid);

        int delete = databaseAdapter.executeUpdateDelete(UserRoleProgramLinkModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();
        return delete;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(UserRoleProgramLinkModel.TABLE);
    }
}
