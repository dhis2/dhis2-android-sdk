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

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.Store;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.option.OptionSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
public class DataElementStoreImpl extends Store implements DataElementStore {

    private static final String FIELDS =
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
                    DataElementModel.Columns.OPTION_SET + ", " +
                    DataElementModel.Columns.CATEGORY_COMBO;

    private static final String INSERT_STATEMENT = "INSERT INTO "
            + DataElementModel.TABLE + " (" + FIELDS + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

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
            DataElementModel.Columns.OPTION_SET + " =?, " +
            DataElementModel.Columns.CATEGORY_COMBO + " =? " +
            " WHERE " + DataElementModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + DataElementModel.TABLE +
            " WHERE " + DataElementModel.Columns.UID + " =?;";

    private static final String QUERY_ALL = "SELECT " + FIELDS + " FROM "
            + DataElementModel.TABLE;

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
                       @Nullable String displayFormName, @Nullable String optionSet,
                       @Nullable String categoryCombo) {
        isNull(uid);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated, shortName, displayShortName,
                description, displayDescription, valueType, zeroIsSignificant, aggregationOperator, formName,
                numberType, domainType, dimension, displayFormName, optionSet, categoryCombo);

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
                      @Nullable String categoryCombo,
                      @NonNull String whereDataElementUid) {
        isNull(uid);
        isNull(whereDataElementUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated, shortName,
                displayShortName, description, displayDescription, valueType, zeroIsSignificant, aggregationOperator,
                formName, numberType, domainType, dimension, displayFormName, optionSet, categoryCombo);

        // bind the where argument
        sqLiteBind(updateStatement, 21, whereDataElementUid);

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
                               @Nullable String displayFormName, @Nullable String optionSet,
                               @Nullable String categoryCombo) {
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
        sqLiteBind(sqLiteStatement, 20, categoryCombo);

    }

    @Override
    public int delete() {
        return databaseAdapter.delete(DataElementModel.TABLE);
    }

    @Override
    public List<DataElement> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_ALL);

        return mapDataElementsFromCursor(cursor);
    }

    private List<DataElement> mapDataElementsFromCursor(Cursor cursor) {
        List<DataElement> dataElements = new ArrayList<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    DataElement dataElement = mapDataElementFromCursor(cursor);

                    dataElements.add(dataElement);
                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return dataElements;
    }

    @NonNull
    private DataElement mapDataElementFromCursor(Cursor cursor) {
        String uid = getStringFromCursor(cursor, 0);
        String code = getStringFromCursor(cursor, 1);
        String name = getStringFromCursor(cursor, 2);
        String displayName = getStringFromCursor(cursor, 3);
        Date created = getDateFromCursor(cursor, 4);
        Date lastUpdated = getDateFromCursor(cursor, 5);
        String shortName = getStringFromCursor(cursor, 6);
        String displayShortName = getStringFromCursor(cursor, 7);
        String description = getStringFromCursor(cursor, 8);
        String displayDescription = getStringFromCursor(cursor, 9);
        ValueType valueType = getValueTypeFromCursor(cursor, 10);
        Boolean zeroIsSignificant = getBooleanFromCursor(cursor, 11);
        String aggregationType = getStringFromCursor(cursor, 12);
        String formName = getStringFromCursor(cursor, 13);
        String numberType = getStringFromCursor(cursor, 14);
        String domainType = getStringFromCursor(cursor, 15);
        String dimension = getStringFromCursor(cursor, 16);
        String displayFormName = getStringFromCursor(cursor, 17);
        String optionSet = getStringFromCursor(cursor, 18);
        String categoryCombo = getStringFromCursor(cursor, 19);

        DataElement dataElement = DataElement.builder().uid(uid).code(code).name(name)
                .displayName(displayName).displayName(displayName)
                .created(created).lastUpdated(lastUpdated).shortName(shortName)
                .displayShortName(displayShortName).description(description)
                .displayDescription(displayDescription).valueType(valueType)
                .zeroIsSignificant(zeroIsSignificant).aggregationType(aggregationType)
                .formName(formName).numberType(numberType).domainType(domainType)
                .dimension(dimension).displayFormName(displayFormName)
                .optionSet(getSimpleOptionSet(optionSet))
                .categoryCombo(getSimpleCategoryCombo(categoryCombo))
                .build();
        return dataElement;
    }

    private CategoryCombo getSimpleCategoryCombo(String categoryComboUId) {
        CategoryCombo simpleCategoryCombo = null;
        if (categoryComboUId != null) {
            simpleCategoryCombo = CategoryCombo.builder().uid(categoryComboUId).build();
        }
        return simpleCategoryCombo;
    }

    private OptionSet getSimpleOptionSet(String optionSetUId) {
        OptionSet simpleOptionSet = null;
        if (optionSetUId != null) {
            simpleOptionSet = OptionSet.builder().uid(optionSetUId).build();
        }
        return simpleOptionSet;
    }
}
