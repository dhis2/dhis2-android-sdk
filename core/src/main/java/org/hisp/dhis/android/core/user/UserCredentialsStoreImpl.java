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

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class UserCredentialsStoreImpl implements UserCredentialsStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + UserCredentialsModel.TABLE + " (" +
            UserCredentialsModel.Columns.UID + ", " +
            UserCredentialsModel.Columns.CODE + ", " +
            UserCredentialsModel.Columns.NAME + ", " +
            UserCredentialsModel.Columns.DISPLAY_NAME + ", " +
            UserCredentialsModel.Columns.CREATED + ", " +
            UserCredentialsModel.Columns.LAST_UPDATED + ", " +
            UserCredentialsModel.Columns.USERNAME + ", " +
            UserCredentialsModel.Columns.USER + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_STATEMENT = "UPDATE " + UserCredentialsModel.TABLE + " SET " +
            UserCredentialsModel.Columns.UID + " =?, " +
            UserCredentialsModel.Columns.CODE + "=?, " +
            UserCredentialsModel.Columns.NAME + "=?, " +
            UserCredentialsModel.Columns.DISPLAY_NAME + "=?, " +
            UserCredentialsModel.Columns.CREATED + "=?, " +
            UserCredentialsModel.Columns.LAST_UPDATED + "=?, " +
            UserCredentialsModel.Columns.USERNAME + "=?, " +
            UserCredentialsModel.Columns.USER + "=?" + " WHERE " +
            UserCredentialsModel.Columns.UID + " = ?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + UserCredentialsModel.TABLE +
            " WHERE " + UserCredentialsModel.Columns.UID + " =?;";

    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    public UserCredentialsStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @Nullable String name,
                       @Nullable String displayName, @Nullable Date created, @Nullable Date lastUpdated,
                       @Nullable String username, @NonNull String user) {

        isNull(uid);
        isNull(user);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated, username, user);

        long returnValue = databaseAdapter.executeInsert(UserCredentialsModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return returnValue;
    }

    @Override
    public int update(@NonNull String uid, @Nullable String code, @Nullable String name,
                      @Nullable String displayName, @Nullable Date created, @Nullable Date lastUpdated,
                      @Nullable String username, @NonNull String user, @NonNull String whereUid) {

        isNull(uid);
        isNull(user);
        isNull(whereUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated, username, user);
        sqLiteBind(updateStatement, 9, whereUid);

        int returnValue = databaseAdapter.executeUpdateDelete(UserCredentialsModel.TABLE, updateStatement);
        updateStatement.clearBindings();
        return returnValue;
    }

    @Override
    public int delete(@NonNull String uid) {
        isNull(uid);
        sqLiteBind(deleteStatement, 1, uid);

        int returnValue = databaseAdapter.executeUpdateDelete(UserCredentialsModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();
        return returnValue;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(UserCredentialsModel.TABLE);
    }

    private void bindArguments(SQLiteStatement sqLiteStatement, @NonNull String uid, @Nullable String code,
                               @Nullable String name, @Nullable String displayName,
                               @Nullable Date created, @Nullable Date lastUpdated,
                               @Nullable String username, @NonNull String user) {
        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, username);
        sqLiteBind(sqLiteStatement, 8, user);
    }
}
