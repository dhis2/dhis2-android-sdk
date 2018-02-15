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

package org.hisp.dhis.android.core.option;

import static org.hisp.dhis.android.core.utils.StoreUtils.parse;
import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.NPathComplexity",
        "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.AvoidInstantiatingObjectsInLoops"
})
public class OptionSetStoreImpl implements OptionSetStore {
    private static final String FIELDS =
            OptionSetModel.Columns.UID + ", " +
            OptionSetModel.Columns.CODE + ", " +
            OptionSetModel.Columns.NAME + ", " +
            OptionSetModel.Columns.DISPLAY_NAME + ", " +
            OptionSetModel.Columns.CREATED + ", " +
            OptionSetModel.Columns.LAST_UPDATED + ", " +
            OptionSetModel.Columns.VERSION + ", " +
                    OptionSetModel.Columns.VALUE_TYPE;

    private static final String EXIST_BY_UID_STATEMENT = "SELECT " +
            OptionSetModel.Columns.UID +
            " FROM " + OptionSetModel.TABLE +
            " WHERE " + OptionSetModel.Columns.UID + " =?;";

    private static final String QUERY_STATEMENT =
            "SELECT " + FIELDS + " FROM " + OptionSetModel.TABLE +
                    " WHERE " + OptionSetModel.Columns.UID + " =?";

    private static final String INSERT_STATEMENT = "INSERT INTO " + OptionSetModel.TABLE + " (" +
            FIELDS + ") " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + OptionSetModel.TABLE + " SET " +
            OptionSetModel.Columns.UID + " =?, " +
            OptionSetModel.Columns.CODE + "=?, " +
            OptionSetModel.Columns.NAME + "=?, " +
            OptionSetModel.Columns.DISPLAY_NAME + "=?, " +
            OptionSetModel.Columns.CREATED + "=?, " +
            OptionSetModel.Columns.LAST_UPDATED + "=?, " +
            OptionSetModel.Columns.VERSION + "=?, " +
            OptionSetModel.Columns.VALUE_TYPE + "=?" + " WHERE " +
            OptionSetModel.Columns.UID + " = ?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + OptionSetModel.TABLE +
            " WHERE " + OptionSetModel.Columns.UID + " =?;";

    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;
    private final SQLiteStatement insertStatement;
    private final DatabaseAdapter databaseAdapter;

    public OptionSetStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @NonNull String code, @NonNull String name, @NonNull String displayName,
                       @NonNull Date created, @NonNull Date lastUpdated, @NonNull Integer version,
                       @NonNull ValueType valueType) {
        isNull(uid);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated, version,
                valueType);

        // execute and clear bindings
        Long insert = databaseAdapter.executeInsert(OptionSetModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return insert;

    }

    @Override
    public int update(@NonNull String uid, @NonNull String code, @NonNull String name,
            @NonNull String displayName, @NonNull Date created,
            @NonNull Date lastUpdated, @NonNull Integer version, @NonNull ValueType valueType,
            @NonNull String whereUid) {
        isNull(uid);
        isNull(whereUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated, version,
                valueType);

        // bind the where clause
        sqLiteBind(updateStatement, 9, whereUid);

        int update = databaseAdapter.executeUpdateDelete(OptionSetModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return update;
    }

    @Override
    public int delete(@NonNull String uid) {
        isNull(uid);
        // bind the where clause
        sqLiteBind(deleteStatement, 1, uid);

        // execute and clear bindings
        int delete = databaseAdapter.executeUpdateDelete(OptionSetModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();

        return delete;
    }

    @Override
    public OptionSet queryByUid(String uid) {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT, uid);

        Map<String, OptionSet> optionSetMap = mapFromCursor(cursor);

        return optionSetMap.get(uid);
    }

    private void bindArguments(SQLiteStatement sqLiteStatement, @NonNull String uid, @NonNull String code,
                               @NonNull String name, @NonNull String displayName, @NonNull Date created,
                               @NonNull Date lastUpdated, @NonNull Integer version, @NonNull ValueType valueType) {
        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, version);
        sqLiteBind(sqLiteStatement, 8, valueType);
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(OptionSetModel.TABLE);
    }

    @Override
    public Boolean exists(String uId) {
        Cursor cursor = databaseAdapter.query(EXIST_BY_UID_STATEMENT, uId);
        return cursor.getCount() > 0;
    }

    private Map<String, OptionSet> mapFromCursor(Cursor cursor) {

        Map<String, OptionSet> optionSetMap = new HashMap<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String uid = cursor.getString(0) == null ? null : cursor.getString(
                            0);
                    String code = cursor.getString(1) == null ? null : cursor.getString(
                            1);
                    String name = cursor.getString(2) == null ? null : cursor.getString(
                            2);
                    String displayName = cursor.getString(3) == null ? null : cursor.getString(
                            3);
                    Date created = cursor.getString(4) == null ? null : parse(cursor.getString(4));
                    Date lastUpdated = cursor.getString(5) == null ? null : parse(
                            cursor.getString(5));

                    Integer version = cursor.getInt(6);

                    ValueType valueType = cursor.getString(7) == null ? null :
                            ValueType.valueOf(cursor.getString(7));

                    optionSetMap.put(uid, OptionSet.builder()
                            .uid(uid)
                            .code(code)
                            .name(name)
                            .displayName(displayName)
                            .created(created)
                            .lastUpdated(lastUpdated)
                            .version(version)
                            .valueType(valueType)
                            .build());

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return optionSetMap;
    }
}
