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

package org.hisp.dhis.android.core.systeminfo;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModel.Columns;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

public class SystemInfoStoreImpl implements SystemInfoStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + SystemInfoModel.TABLE + " (" +
            Columns.SERVER_DATE + ", " +
            Columns.DATE_FORMAT + ", " +
            Columns.VERSION + ", " +
            Columns.CONTEXT_PATH + ") " +
            "VALUES (?, ?, ?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + SystemInfoModel.TABLE + " SET " +
            Columns.SERVER_DATE + " =?, " +
            Columns.DATE_FORMAT + " =?, " +
            Columns.VERSION + " =?, " +
            Columns.CONTEXT_PATH + " =? " +
            " WHERE " + Columns.CONTEXT_PATH + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + SystemInfoModel.TABLE +
            " WHERE " + Columns.CONTEXT_PATH + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;
    private final DatabaseAdapter databaseAdapter;

    public SystemInfoStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull Date serverDate,
                       @NonNull String dateFormat,
                       @NonNull String version,
                       @NonNull String contextPath) {
        isNull(serverDate);
        isNull(dateFormat);
        isNull(version);
        isNull(contextPath);
        bindArguments(insertStatement, serverDate, dateFormat, version, contextPath);
        long ret = databaseAdapter.executeInsert(SystemInfoModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return ret;
    }

    @Override
    public int update(@NonNull Date serverDate,
                      @NonNull String dateFormat,
                      @NonNull String version,
                      @NonNull String contextPath,
                      @NonNull String whereContextPath) {
        isNull(serverDate);
        isNull(dateFormat);
        isNull(version);
        isNull(contextPath);
        isNull(whereContextPath);
        bindArguments(updateStatement, serverDate, dateFormat, version, contextPath);
        sqLiteBind(updateStatement, 5, whereContextPath);

        int ret = databaseAdapter.executeUpdateDelete(SystemInfoModel.TABLE, updateStatement);
        updateStatement.clearBindings();
        return ret;
    }

    @Override
    public int delete(@NonNull String contextPath) {
        isNull(contextPath);
        sqLiteBind(deleteStatement, 1, contextPath);

        int ret = databaseAdapter.executeUpdateDelete(SystemInfoModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();
        return ret;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement,
                               @NonNull Date serverDate,
                               @NonNull String dateFormat,
                               @NonNull String version,
                               @NonNull String contextPath) {
        sqLiteBind(sqLiteStatement, 1, serverDate);
        sqLiteBind(sqLiteStatement, 2, dateFormat);
        sqLiteBind(sqLiteStatement, 3, version);
        sqLiteBind(sqLiteStatement, 4, contextPath);
    }
}
