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

package org.hisp.dhis.android.core.relationship;

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
public class RelationshipTypeStoreImpl implements RelationshipTypeStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            RelationshipTypeModel.TABLE + " (" +
            RelationshipTypeModel.Columns.UID + ", " +
            RelationshipTypeModel.Columns.CODE + ", " +
            RelationshipTypeModel.Columns.NAME + ", " +
            RelationshipTypeModel.Columns.DISPLAY_NAME + ", " +
            RelationshipTypeModel.Columns.CREATED + ", " +
            RelationshipTypeModel.Columns.LAST_UPDATED + ", " +
            RelationshipTypeModel.Columns.A_IS_TO_B + ", " +
            RelationshipTypeModel.Columns.B_IS_TO_A + ") " +
            "VALUES (" + "?, ?, ?, ?, ?, ?, ?, ?" + ");";

    private static final String UPDATE_STATEMENT = "UPDATE " + RelationshipTypeModel.TABLE + " SET " +
            RelationshipTypeModel.Columns.UID + " =?, " +
            RelationshipTypeModel.Columns.CODE + " =?, " +
            RelationshipTypeModel.Columns.NAME + " =?, " +
            RelationshipTypeModel.Columns.DISPLAY_NAME + " =?, " +
            RelationshipTypeModel.Columns.CREATED + " =?, " +
            RelationshipTypeModel.Columns.LAST_UPDATED + " =?, " +
            RelationshipTypeModel.Columns.A_IS_TO_B + " =?, " +
            RelationshipTypeModel.Columns.B_IS_TO_A + " =? " +
            " WHERE " +
            RelationshipTypeModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + RelationshipTypeModel.TABLE +
            " WHERE " +
            RelationshipTypeModel.Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public RelationshipTypeStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid,
                       @Nullable String code,
                       @NonNull String name,
                       @Nullable String displayName,
                       @Nullable Date created,
                       @Nullable Date lastUpdated,
                       @NonNull String aIsToB,
                       @NonNull String bIsToA) {
        isNull(uid);
        isNull(aIsToB);
        isNull(bIsToA);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated, aIsToB, bIsToA);

        long ret = databaseAdapter.executeInsert(RelationshipTypeModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return ret;
    }

    @Override
    public int update(@NonNull String uid,
                      @Nullable String code,
                      @Nullable String name,
                      @Nullable String displayName,
                      @Nullable Date created,
                      @Nullable Date lastUpdated,
                      @NonNull String aIsToB,
                      @NonNull String bIsToA,
                      @NonNull String whereUid) {
        isNull(uid);
        isNull(aIsToB);
        isNull(bIsToA);
        isNull(whereUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated, aIsToB, bIsToA);
        sqLiteBind(updateStatement, 9, whereUid);

        int ret = updateStatement.executeUpdateDelete();
        updateStatement.clearBindings();
        return ret;
    }

    @Override
    public int delete(@NonNull String uid) {
        isNull(uid);
        sqLiteBind(deleteStatement, 1, uid);

        int ret = deleteStatement.executeUpdateDelete();
        deleteStatement.clearBindings();
        return ret;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement,
                               @NonNull String uid,
                               @Nullable String code,
                               @Nullable String name,
                               @Nullable String displayName,
                               @Nullable Date created,
                               @Nullable Date lastUpdated,
                               @NonNull String aIsToB,
                               @NonNull String bIsToA) {
        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, aIsToB);
        sqLiteBind(sqLiteStatement, 8, bIsToA);
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(RelationshipTypeModel.TABLE);
    }
}
