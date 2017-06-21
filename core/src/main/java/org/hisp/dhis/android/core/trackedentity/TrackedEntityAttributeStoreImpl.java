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

package org.hisp.dhis.android.core.trackedentity;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
public class TrackedEntityAttributeStoreImpl implements TrackedEntityAttributeStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            TrackedEntityAttributeModel.TABLE + " (" +
            TrackedEntityAttributeModel.Columns.UID + ", " +
            TrackedEntityAttributeModel.Columns.CODE + ", " +
            TrackedEntityAttributeModel.Columns.NAME + ", " +
            TrackedEntityAttributeModel.Columns.DISPLAY_NAME + ", " +
            TrackedEntityAttributeModel.Columns.CREATED + ", " +
            TrackedEntityAttributeModel.Columns.LAST_UPDATED + ", " +
            TrackedEntityAttributeModel.Columns.SHORT_NAME + ", " +
            TrackedEntityAttributeModel.Columns.DISPLAY_SHORT_NAME + ", " +
            TrackedEntityAttributeModel.Columns.DESCRIPTION + ", " +
            TrackedEntityAttributeModel.Columns.DISPLAY_DESCRIPTION + ", " +
            TrackedEntityAttributeModel.Columns.PATTERN + ", " +
            TrackedEntityAttributeModel.Columns.SORT_ORDER_IN_LIST_NO_PROGRAM + ", " +
            TrackedEntityAttributeModel.Columns.OPTION_SET + ", " +
            TrackedEntityAttributeModel.Columns.VALUE_TYPE + ", " +
            TrackedEntityAttributeModel.Columns.EXPRESSION + ", " +
            TrackedEntityAttributeModel.Columns.SEARCH_SCOPE + ", " +
            TrackedEntityAttributeModel.Columns.PROGRAM_SCOPE + ", " +
            TrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST_NO_PROGRAM + ", " +
            TrackedEntityAttributeModel.Columns.GENERATED + ", " +
            TrackedEntityAttributeModel.Columns.DISPLAY_ON_VISIT_SCHEDULE + ", " +
            TrackedEntityAttributeModel.Columns.ORG_UNIT_SCOPE + ", " +
            TrackedEntityAttributeModel.Columns.UNIQUE + ", " +
            TrackedEntityAttributeModel.Columns.INHERIT +
            ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_STATEMENT = "UPDATE " + TrackedEntityAttributeModel.TABLE + " SET " +
            TrackedEntityAttributeModel.Columns.UID + " =?, " +
            TrackedEntityAttributeModel.Columns.CODE + " =?, " +
            TrackedEntityAttributeModel.Columns.NAME + " =?, " +
            TrackedEntityAttributeModel.Columns.DISPLAY_NAME + " =?, " +
            TrackedEntityAttributeModel.Columns.CREATED + " =?, " +
            TrackedEntityAttributeModel.Columns.LAST_UPDATED + " =?, " +
            TrackedEntityAttributeModel.Columns.SHORT_NAME + " =?, " +
            TrackedEntityAttributeModel.Columns.DISPLAY_SHORT_NAME + " =?, " +
            TrackedEntityAttributeModel.Columns.DESCRIPTION + " =?, " +
            TrackedEntityAttributeModel.Columns.DISPLAY_DESCRIPTION + " =?, " +
            TrackedEntityAttributeModel.Columns.PATTERN + " =?, " +
            TrackedEntityAttributeModel.Columns.SORT_ORDER_IN_LIST_NO_PROGRAM + " =?, " +
            TrackedEntityAttributeModel.Columns.OPTION_SET + " =?, " +
            TrackedEntityAttributeModel.Columns.VALUE_TYPE + " =?, " +
            TrackedEntityAttributeModel.Columns.EXPRESSION + " =?, " +
            TrackedEntityAttributeModel.Columns.SEARCH_SCOPE + " =?, " +
            TrackedEntityAttributeModel.Columns.PROGRAM_SCOPE + " =?, " +
            TrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST_NO_PROGRAM + " =?, " +
            TrackedEntityAttributeModel.Columns.GENERATED + " =?, " +
            TrackedEntityAttributeModel.Columns.DISPLAY_ON_VISIT_SCHEDULE + " =?, " +
            TrackedEntityAttributeModel.Columns.ORG_UNIT_SCOPE + " =?, " +
            TrackedEntityAttributeModel.Columns.UNIQUE + " =?, " +
            TrackedEntityAttributeModel.Columns.INHERIT + " =? " +
            " WHERE " + TrackedEntityAttributeModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + TrackedEntityAttributeModel.TABLE +
            " WHERE " + TrackedEntityAttributeModel.Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public TrackedEntityAttributeStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @Nullable String name,
                       @Nullable String displayName, @Nullable Date created,
                       @Nullable Date lastUpdated, @Nullable String shortName,
                       @Nullable String displayShortName, @Nullable String description,
                       @Nullable String displayDescription, @Nullable String pattern,
                       @Nullable Integer sortOrderInListNoProgram, @Nullable String optionSet,
                       @Nullable ValueType valueType, @Nullable String expression,
                       @Nullable TrackedEntityAttributeSearchScope searchScope,
                       @Nullable Boolean programScope, @Nullable Boolean displayInListNoProgram,
                       @Nullable Boolean generated, @Nullable Boolean displayOnVisitSchedule,
                       @Nullable Boolean orgUnitScope, @Nullable Boolean unique,
                       @Nullable Boolean inherit) {
        isNull(uid);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated, shortName,
                displayShortName, description, displayDescription, pattern, sortOrderInListNoProgram, optionSet,
                valueType, expression, searchScope, programScope, displayInListNoProgram,
                generated, displayOnVisitSchedule, orgUnitScope, unique, inherit);

        Long insert = databaseAdapter.executeInsert(TrackedEntityAttributeModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return insert;
    }

    @Override
    public int update(@NonNull String uid, @Nullable String code, @NonNull String name, @Nullable String displayName,
                      @NonNull Date created, @NonNull Date lastUpdated, @Nullable String shortName,
                      @Nullable String displayShortName, @Nullable String description,
                      @Nullable String displayDescription, @Nullable String pattern,
                      @Nullable Integer sortOrderInListNoProgram, @Nullable String optionSet,
                      @NonNull ValueType valueType, @Nullable String expression,
                      @Nullable TrackedEntityAttributeSearchScope searchScope, @Nullable Boolean programScope,
                      @Nullable Boolean displayInListNoProgram, @Nullable Boolean generated,
                      @Nullable Boolean displayOnVisitSchedule, @Nullable Boolean orgUnitScope,
                      @Nullable Boolean unique, @Nullable Boolean inherit,
                      @NonNull String whereUid) {
        isNull(uid);
        isNull(whereUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated, shortName,
                displayShortName, description, displayDescription, pattern, sortOrderInListNoProgram, optionSet,
                valueType, expression, searchScope, programScope, displayInListNoProgram,
                generated, displayOnVisitSchedule, orgUnitScope, unique, inherit);
        sqLiteBind(updateStatement, 24, whereUid);

        int update = databaseAdapter.executeUpdateDelete(TrackedEntityAttributeModel.TABLE, updateStatement);
        updateStatement.clearBindings();
        return update;
    }

    @Override
    public int delete(@NonNull String uid) {
        isNull(uid);
        sqLiteBind(deleteStatement, 1, uid);

        int delete = databaseAdapter.executeUpdateDelete(TrackedEntityAttributeModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();
        return delete;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement, @NonNull String uid, @Nullable String code,
                               @NonNull String name, @Nullable String displayName, @NonNull Date created,
                               @NonNull Date lastUpdated, @Nullable String shortName,
                               @Nullable String displayShortName, @Nullable String description,
                               @Nullable String displayDescription, @Nullable String pattern,
                               @Nullable Integer sortOrderInListNoProgram, @Nullable String optionSet,
                               @NonNull ValueType valueType, @Nullable String expression,
                               @Nullable TrackedEntityAttributeSearchScope searchScope,
                               @Nullable Boolean programScope, @Nullable Boolean displayInListNoProgram,
                               @Nullable Boolean generated, @Nullable Boolean displayOnVisitSchedule,
                               @Nullable Boolean orgUnitScope, @Nullable Boolean unique,
                               @Nullable Boolean inherit) {
        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, shortName);
        sqLiteBind(sqLiteStatement, 8, displayShortName);
        sqLiteBind(sqLiteStatement, 9, description);
        sqLiteBind(sqLiteStatement, 10, displayDescription);
        sqLiteBind(sqLiteStatement, 11, pattern);
        sqLiteBind(sqLiteStatement, 12, sortOrderInListNoProgram);
        sqLiteBind(sqLiteStatement, 13, optionSet);
        sqLiteBind(sqLiteStatement, 14, valueType);
        sqLiteBind(sqLiteStatement, 15, expression);
        sqLiteBind(sqLiteStatement, 16, searchScope);
        sqLiteBind(sqLiteStatement, 17, programScope);
        sqLiteBind(sqLiteStatement, 18, displayInListNoProgram);
        sqLiteBind(sqLiteStatement, 19, generated);
        sqLiteBind(sqLiteStatement, 20, displayOnVisitSchedule);
        sqLiteBind(sqLiteStatement, 21, orgUnitScope);
        sqLiteBind(sqLiteStatement, 22, unique);
        sqLiteBind(sqLiteStatement, 23, inherit);
    }
}
