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

import org.hisp.dhis.android.core.common.Store;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.NPathComplexity",
        "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.AvoidInstantiatingObjectsInLoops"
})
public class ProgramIndicatorStoreImpl extends Store implements ProgramIndicatorStore {
    private static final String FIELDS =
            ProgramIndicatorModel.Columns.UID + ", " +
                    ProgramIndicatorModel.Columns.CODE + ", " +
                    ProgramIndicatorModel.Columns.NAME + ", " +
                    ProgramIndicatorModel.Columns.DISPLAY_NAME + ", " +
                    ProgramIndicatorModel.Columns.CREATED + ", " +
                    ProgramIndicatorModel.Columns.LAST_UPDATED + ", " +
                    ProgramIndicatorModel.Columns.SHORT_NAME + ", " +
                    ProgramIndicatorModel.Columns.DISPLAY_SHORT_NAME + ", " +
                    ProgramIndicatorModel.Columns.DESCRIPTION + ", " +
                    ProgramIndicatorModel.Columns.DISPLAY_DESCRIPTION + ", " +
                    ProgramIndicatorModel.Columns.DISPLAY_IN_FORM + ", " +
                    ProgramIndicatorModel.Columns.EXPRESSION + ", " +
                    ProgramIndicatorModel.Columns.DIMENSION_ITEM + ", " +
                    ProgramIndicatorModel.Columns.FILTER + ", " +
                    ProgramIndicatorModel.Columns.DECIMALS + ", " +
                    ProgramIndicatorModel.Columns.PROGRAM;

    private static final String EXIST_BY_UID_STATEMENT = "SELECT " +
            ProgramIndicatorModel.Columns.UID +
            " FROM " + ProgramIndicatorModel.TABLE +
            " WHERE " + ProgramIndicatorModel.Columns.UID + " =?;";

    private static final String QUERY_BY_UID_STATEMENT =
            "SELECT " + FIELDS + " FROM " + ProgramIndicatorModel.TABLE + " WHERE " +
                    ProgramIndicatorModel.Columns.UID + "=?";


    private static final String INSERT_STATEMENT =
            "INSERT INTO " + ProgramIndicatorModel.TABLE + " (" +
                    FIELDS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_STATEMENT =
            "UPDATE " + ProgramIndicatorModel.TABLE + " SET " +
                    ProgramIndicatorModel.Columns.UID + " =?, " +
                    ProgramIndicatorModel.Columns.CODE + " =?, " +
                    ProgramIndicatorModel.Columns.NAME + " =?, " +
                    ProgramIndicatorModel.Columns.DISPLAY_NAME + " =?, " +
                    ProgramIndicatorModel.Columns.CREATED + " =?, " +
                    ProgramIndicatorModel.Columns.LAST_UPDATED + " =?, " +
                    ProgramIndicatorModel.Columns.SHORT_NAME + " =?, " +
                    ProgramIndicatorModel.Columns.DISPLAY_SHORT_NAME + " =?, " +
                    ProgramIndicatorModel.Columns.DESCRIPTION + " =?, " +
                    ProgramIndicatorModel.Columns.DISPLAY_DESCRIPTION + " =?, " +
                    ProgramIndicatorModel.Columns.DISPLAY_IN_FORM + " =?, " +
                    ProgramIndicatorModel.Columns.EXPRESSION + " =?, " +
                    ProgramIndicatorModel.Columns.DIMENSION_ITEM + " =?, " +
                    ProgramIndicatorModel.Columns.FILTER + " =?, " +
                    ProgramIndicatorModel.Columns.DECIMALS + " =?, " +
                    ProgramIndicatorModel.Columns.PROGRAM + " =? " +
                    " WHERE " + ProgramIndicatorModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + ProgramIndicatorModel.TABLE +
            " WHERE " + ProgramIndicatorModel.Columns.UID + " =?;";

    private final SQLiteStatement insertRowStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public ProgramIndicatorStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertRowStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
            @Nullable String displayName, @NonNull Date created,
            @NonNull Date lastUpdated, @Nullable String shortName,
            @Nullable String displayShortName, @Nullable String description,
            @Nullable String displayDescription, @Nullable Boolean displayInForm,
            @Nullable String expression, @Nullable String dimensionItem,
            @Nullable String filter, @Nullable Integer decimals,
            @Nullable String program) {
        isNull(uid);
        isNull(program);
        bindArguments(insertRowStatement, uid, code, name, displayName, created, lastUpdated,
                shortName,
                displayShortName, description, displayDescription, displayInForm, expression,
                dimensionItem,
                filter, decimals, program);

        // execute and clear bindings
        Long insert = databaseAdapter.executeInsert(ProgramIndicatorModel.TABLE,
                insertRowStatement);
        insertRowStatement.clearBindings();
        return insert;
    }

    @Override
    public int update(@NonNull String uid, @Nullable String code, @NonNull String name,
            @Nullable String displayName,
            @NonNull Date created, @NonNull Date lastUpdated, @Nullable String shortName,
            @Nullable String displayShortName, @Nullable String description,
            @Nullable String displayDescription, @Nullable Boolean displayInForm,
            @Nullable String expression, @Nullable String dimensionItem, @Nullable String filter,
            @Nullable Integer decimals, @Nullable String program,
            @NonNull String whereProgramIndicatorUid) {
        isNull(uid);
        isNull(program);
        isNull(whereProgramIndicatorUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated,
                shortName, displayShortName,
                description, displayDescription, displayInForm, expression, dimensionItem, filter,
                decimals, program);

        // bind the where argument
        sqLiteBind(updateStatement, 17, whereProgramIndicatorUid);

        // execute and clear bindings
        int update = databaseAdapter.executeUpdateDelete(ProgramIndicatorModel.TABLE,
                updateStatement);
        updateStatement.clearBindings();
        return update;
    }

    @Override
    public int delete(String uid) {
        isNull(uid);
        // bind the where argument
        sqLiteBind(deleteStatement, 1, uid);

        // execute and clear bindings
        int delete = databaseAdapter.executeUpdateDelete(ProgramIndicatorModel.TABLE,
                deleteStatement);
        deleteStatement.clearBindings();
        return delete;
    }

    @Override
    public ProgramIndicator queryByUid(String uid) {
        ProgramIndicator programIndicator = null;

        Cursor cursor = databaseAdapter.query(QUERY_BY_UID_STATEMENT, uid);

        if (cursor.getCount() > 0) {
            Map<String, List<ProgramIndicator>> programIndicatorMap = mapFromCursor(cursor);

            Map.Entry<String, List<ProgramIndicator>> entry =
                    programIndicatorMap.entrySet().iterator().next();
            programIndicator = entry.getValue().get(0);
        }

        return programIndicator;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement, @NonNull String uid,
            @Nullable String code,
            @NonNull String name, @Nullable String displayName, @NonNull Date created,
            @NonNull Date lastUpdated, @Nullable String shortName,
            @Nullable String displayShortName, @Nullable String description,
            @Nullable String displayDescription, @Nullable Boolean displayInForm,
            @Nullable String expression, @Nullable String dimensionItem,
            @Nullable String filter, @Nullable Integer decimals,
            @Nullable String program) {
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
        sqLiteBind(sqLiteStatement, 11, displayInForm);
        sqLiteBind(sqLiteStatement, 12, expression);
        sqLiteBind(sqLiteStatement, 13, dimensionItem);
        sqLiteBind(sqLiteStatement, 14, filter);
        sqLiteBind(sqLiteStatement, 15, decimals);
        sqLiteBind(sqLiteStatement, 16, program);
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(ProgramIndicatorModel.TABLE);
    }

    @Override
    public Boolean exists(String uId) {
        Cursor cursor = databaseAdapter.query(EXIST_BY_UID_STATEMENT, uId);
        return cursor.getCount() > 0;
    }

    private Map<String, List<ProgramIndicator>> mapFromCursor(Cursor cursor) {

        Map<String, List<ProgramIndicator>> programStagesMap = new HashMap<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
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
                    Boolean displayInForm = getBooleanFromCursor(cursor, 10);
                    String expression = getStringFromCursor(cursor, 11);
                    String dimensionItem = getStringFromCursor(cursor, 12);
                    String filter = getStringFromCursor(cursor, 13);
                    Integer decimals = getIntegerFromCursor(cursor, 14);
                    String program = getStringFromCursor(cursor, 15);

                    if (!programStagesMap.containsKey(program)) {
                        programStagesMap.put(program, new ArrayList<ProgramIndicator>());
                    }

                    programStagesMap.get(program).add(ProgramIndicator.builder()
                            .uid(uid)
                            .code(code)
                            .name(name)
                            .displayName(displayName)
                            .created(created)
                            .lastUpdated(lastUpdated)
                            .shortName(shortName)
                            .displayShortName(displayShortName)
                            .description(description)
                            .displayDescription(displayDescription)
                            .displayInForm(displayInForm)
                            .expression(expression)
                            .dimensionItem(dimensionItem)
                            .filter(filter)
                            .decimals(decimals)
                            .program(Program.builder().uid(program).build())
                            .build());

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return programStagesMap;
    }
}
