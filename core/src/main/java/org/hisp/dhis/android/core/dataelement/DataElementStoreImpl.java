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

package org.hisp.dhis.android.core.dataelement;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
public class DataElementStoreImpl implements DataElementStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + DataElementModel.TABLE + " (" +
            DataElementModel.Columns.UID + ", " +
            DataElementModel.Columns.CODE + ", " +
            DataElementModel.Columns.NAME + ", " +
            DataElementModel.Columns.DISPLAY_NAME + ", " +
            DataElementModel.Columns.CREATED + ", " +
            DataElementModel.Columns.LAST_UPDATED + ", " +
            DataElementModel.Columns.SHORT_NAME + ", " +
            DataElementModel.Columns.DISPLAY_SHORT_NAME + ", " +
            DataElementModel.Columns.DESCRIPTION + ", " +
            DataElementModel.Columns.DISPLAY_DESCRIPTION + ", " +
            DataElementModel.Columns.VALUE_TYPE + ", " +
            DataElementModel.Columns.ZERO_IS_SIGNIFICANT + ", " +
            DataElementModel.Columns.AGGREGATION_TYPE + ", " +
            DataElementModel.Columns.FORM_NAME + ", " +
            DataElementModel.Columns.NUMBER_TYPE + ", " +
            DataElementModel.Columns.DOMAIN_TYPE + ", " +
            DataElementModel.Columns.DIMENSION + ", " +
            DataElementModel.Columns.DISPLAY_FORM_NAME + ", " +
            DataElementModel.Columns.OPTION_SET + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + DataElementModel.TABLE + " SET " +
            DataElementModel.Columns.UID + " =?, " +
            DataElementModel.Columns.CODE + " =?, " +
            DataElementModel.Columns.NAME + " =?, " +
            DataElementModel.Columns.DISPLAY_NAME + " =?, " +
            DataElementModel.Columns.CREATED + " =?, " +
            DataElementModel.Columns.LAST_UPDATED + " =?, " +
            DataElementModel.Columns.SHORT_NAME + " =?, " +
            DataElementModel.Columns.DISPLAY_SHORT_NAME + " =?, " +
            DataElementModel.Columns.DESCRIPTION + " =?, " +
            DataElementModel.Columns.DISPLAY_DESCRIPTION + " =?, " +
            DataElementModel.Columns.VALUE_TYPE + " =?, " +
            DataElementModel.Columns.ZERO_IS_SIGNIFICANT + " =?, " +
            DataElementModel.Columns.AGGREGATION_TYPE + " =?, " +
            DataElementModel.Columns.FORM_NAME + " =?, " +
            DataElementModel.Columns.NUMBER_TYPE + " =?, " +
            DataElementModel.Columns.DOMAIN_TYPE + " =?, " +
            DataElementModel.Columns.DIMENSION + " =?, " +
            DataElementModel.Columns.DISPLAY_FORM_NAME + " =?, " +
            DataElementModel.Columns.OPTION_SET + " =? " +
            " WHERE " + DataElementModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + DataElementModel.TABLE +
            " WHERE " + DataElementModel.Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public DataElementStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
                       @NonNull String displayName, @NonNull Date created,
                       @NonNull Date lastUpdated, @Nullable String shortName,
                       @Nullable String displayShortName, @Nullable String description,
                       @Nullable String displayDescription, @NonNull ValueType valueType,
                       @Nullable Boolean zeroIsSignificant, @Nullable String aggregationOperator,
                       @Nullable String formName, @Nullable String numberType,
                       @Nullable String domainType, @Nullable String dimension,
                       @Nullable String displayFormName, @Nullable String optionSet) {
        isNull(uid);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated, shortName, displayShortName,
                description, displayDescription, valueType, zeroIsSignificant, aggregationOperator, formName,
                numberType, domainType, dimension, displayFormName, optionSet);

        // execute and clear bindings
        Long insert = databaseAdapter.executeInsert(DataElementModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return insert;
    }

    @Override
    public int delete(String uid) {
        isNull(uid);
        // bind the where argument
        sqLiteBind(deleteStatement, 1, uid);

        // execute and clear bindings
        int delete = databaseAdapter.executeUpdateDelete(DataElementModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();
        return delete;
    }

    @Override
    public int update(@NonNull String uid, @Nullable String code, @NonNull String name, @NonNull String displayName,
                      @NonNull Date created, @NonNull Date lastUpdated, @Nullable String shortName,
                      @Nullable String displayShortName, @Nullable String description,
                      @Nullable String displayDescription, @NonNull ValueType valueType,
                      @Nullable Boolean zeroIsSignificant, @Nullable String aggregationOperator,
                      @Nullable String formName, @Nullable String numberType, @Nullable String domainType,
                      @Nullable String dimension, @Nullable String displayFormName, @Nullable String optionSet,
                      @NonNull String whereDataElementUid) {
        isNull(uid);
        isNull(whereDataElementUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated, shortName,
                displayShortName, description, displayDescription, valueType, zeroIsSignificant, aggregationOperator,
                formName, numberType, domainType, dimension, displayFormName, optionSet);

        // bind the where argument
        sqLiteBind(updateStatement, 20, whereDataElementUid);

        // execute and clear bindings
        int update = databaseAdapter.executeUpdateDelete(DataElementModel.TABLE, updateStatement);
        updateStatement.clearBindings();
        return update;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement, @NonNull String uid, @Nullable String code,
                               @NonNull String name, @NonNull String displayName, @NonNull Date created,
                               @NonNull Date lastUpdated, @Nullable String shortName,
                               @Nullable String displayShortName, @Nullable String description,
                               @Nullable String displayDescription, @NonNull ValueType valueType,
                               @Nullable Boolean zeroIsSignificant, @Nullable String aggregationOperator,
                               @Nullable String formName, @Nullable String numberType,
                               @Nullable String domainType, @Nullable String dimension,
                               @Nullable String displayFormName, @Nullable String optionSet) {
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
        sqLiteBind(sqLiteStatement, 11, valueType);
        sqLiteBind(sqLiteStatement, 12, zeroIsSignificant);
        sqLiteBind(sqLiteStatement, 13, aggregationOperator);
        sqLiteBind(sqLiteStatement, 14, formName);
        sqLiteBind(sqLiteStatement, 15, numberType);
        sqLiteBind(sqLiteStatement, 16, domainType);
        sqLiteBind(sqLiteStatement, 17, dimension);
        sqLiteBind(sqLiteStatement, 18, displayFormName);
        sqLiteBind(sqLiteStatement, 19, optionSet);

    }

    @Override
    public int delete() {
        return databaseAdapter.delete(DataElementModel.TABLE);
    }
}
