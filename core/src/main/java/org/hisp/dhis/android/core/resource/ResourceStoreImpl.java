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
package org.hisp.dhis.android.core.resource;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceModel.Columns;

import java.util.Date;

public class ResourceStoreImpl implements ResourceStore {
    public static final String INSERT_STATEMENT = "INSERT INTO " + ResourceModel.TABLE + " (" +
            Columns.RESOURCE_TYPE + ", " +
            Columns.LAST_SYNCED + ") " +
            "VALUES(?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + ResourceModel.TABLE + " SET " +
            Columns.RESOURCE_TYPE + " =?, " +
            Columns.LAST_SYNCED + "=? " + " WHERE " +
            Columns.RESOURCE_TYPE + " = ?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + ResourceModel.TABLE +
            " WHERE " + Columns.RESOURCE_TYPE + " =?;";
    
    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    public ResourceStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String resourceType, @Nullable Date lastSynced) {
        isNull(resourceType);
        sqLiteBind(insertStatement, 1, resourceType);
        sqLiteBind(insertStatement, 2, lastSynced);

        long returnValue = databaseAdapter.executeInsert(ResourceModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return returnValue;
    }

    @Override
    public int update(@NonNull String resourceType, @Nullable Date lastSynced,
                      @NonNull String whereResourceType) {
        isNull(resourceType);
        isNull(whereResourceType);
        sqLiteBind(updateStatement, 1, resourceType);
        sqLiteBind(updateStatement, 2, lastSynced);
        sqLiteBind(updateStatement, 3, whereResourceType);

        int returnValue = databaseAdapter.executeUpdateDelete(ResourceModel.TABLE, updateStatement);
        updateStatement.clearBindings();
        return returnValue;
    }

    @Override
    public int delete(@NonNull String resourceType) {
        isNull(resourceType);
        sqLiteBind(deleteStatement, 1, resourceType);

        int returnValue = databaseAdapter.executeUpdateDelete(ResourceModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();
        return returnValue;
    }

    @Override
    public String getLastUpdated(ResourceModel.Type type) {
        String lastUpdated = null;
        Cursor cursor = databaseAdapter.query("SELECT " + ResourceModel.Columns.LAST_SYNCED +
                " FROM " + ResourceModel.TABLE +
                " WHERE " + ResourceModel.Columns.RESOURCE_TYPE +
                " = '" + type.name() + "'"
        );
        if(cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                lastUpdated = cursor.getString(cursor.getColumnIndex(ResourceModel.Columns.LAST_SYNCED));
            }
            cursor.close();
        }
        return lastUpdated;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(ResourceModel.TABLE);
    }
}
