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

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.Store;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.option.OptionSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.NPathComplexity"
})
public class TrackedEntityAttributeStoreImpl extends Store implements TrackedEntityAttributeStore {
    private static final String FIELDS =
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
                    TrackedEntityAttributeModel.Columns.INHERIT;
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            TrackedEntityAttributeModel.TABLE + " (" + FIELDS +
            ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_STATEMENT =
            "UPDATE " + TrackedEntityAttributeModel.TABLE + " SET " +
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

    private static final String DELETE_STATEMENT =
            "DELETE FROM " + TrackedEntityAttributeModel.TABLE +
                    " WHERE " + TrackedEntityAttributeModel.Columns.UID + " =?;";

    private static final String QUERY_ALL_TRACKED_ENTITY_ATTRIBUTES =
            "SELECT " + FIELDS + " FROM " + TrackedEntityAttributeModel.TABLE;
    private static final String QUERY_BY_UID =
            "SELECT " + FIELDS + " FROM " + TrackedEntityAttributeModel.TABLE
                    + " WHERE " + TrackedEntityAttributeModel.Columns.UID + "=?;";

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
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated,
                shortName,
                displayShortName, description, displayDescription, pattern,
                sortOrderInListNoProgram, optionSet,
                valueType, expression, searchScope, programScope, displayInListNoProgram,
                generated, displayOnVisitSchedule, orgUnitScope, unique, inherit);

        Long insert = databaseAdapter.executeInsert(TrackedEntityAttributeModel.TABLE,
                insertStatement);
        insertStatement.clearBindings();
        return insert;
    }

    @Override
    public int update(@NonNull String uid, @Nullable String code, @NonNull String name,
            @Nullable String displayName,
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
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated,
                shortName,
                displayShortName, description, displayDescription, pattern,
                sortOrderInListNoProgram, optionSet,
                valueType, expression, searchScope, programScope, displayInListNoProgram,
                generated, displayOnVisitSchedule, orgUnitScope, unique, inherit);
        sqLiteBind(updateStatement, 24, whereUid);

        int update = databaseAdapter.executeUpdateDelete(TrackedEntityAttributeModel.TABLE,
                updateStatement);
        updateStatement.clearBindings();
        return update;
    }

    @Override
    public int delete(@NonNull String uid) {
        isNull(uid);
        sqLiteBind(deleteStatement, 1, uid);

        int delete = databaseAdapter.executeUpdateDelete(TrackedEntityAttributeModel.TABLE,
                deleteStatement);
        deleteStatement.clearBindings();
        return delete;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement, @NonNull String uid,
            @Nullable String code,
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

    @Override
    public int delete() {
        return databaseAdapter.delete(TrackedEntityAttributeModel.TABLE);
    }

    @Override
    public List<TrackedEntityAttribute> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_ALL_TRACKED_ENTITY_ATTRIBUTES);
        return mapTrackedEntityAttributesFromCursor(cursor);
    }

    @Override
    public TrackedEntityAttribute queryByUid(String uid) {
        TrackedEntityAttribute trackedEntityAttribute = null;

        Cursor cursor = databaseAdapter.query(QUERY_BY_UID, uid);

        if (cursor.getCount() > 0) {
            trackedEntityAttribute = mapTrackedEntityAttributesFromCursor(cursor).get(0);
        }

        return trackedEntityAttribute;
    }

    private List<TrackedEntityAttribute> mapTrackedEntityAttributesFromCursor(Cursor cursor) {
        List<TrackedEntityAttribute> trackedEntityAttributes = new ArrayList<>(
                cursor.getCount());
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    TrackedEntityAttribute trackedEntityAttribute =
                            mapTrackedEntityAttributeFromCursor(cursor);
                    trackedEntityAttributes.add(trackedEntityAttribute);
                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return trackedEntityAttributes;
    }

    private TrackedEntityAttribute mapTrackedEntityAttributeFromCursor(Cursor cursor) {
        TrackedEntityAttribute trackedEntityAttribute;

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
        String pattern = getStringFromCursor(cursor, 10);
        Integer sortOrderInListNoProgram = getIntegerFromCursor(cursor, 11);
        String optionSetUID = getStringFromCursor(cursor, 12);
        ValueType valueType = getValueTypeFromCursor(cursor, 13);
        String expression = getStringFromCursor(cursor, 14);
        TrackedEntityAttributeSearchScope searchScope =
                getTrackedEntityAttributeSearchScopeFromCursor(cursor, 15);
        Boolean programScope = getBooleanFromCursor(cursor, 16);
        Boolean displayInListNoProgram = getBooleanFromCursor(cursor, 17);
        Boolean generated = getBooleanFromCursor(cursor, 18);
        Boolean displayOnVisitSchedule = getBooleanFromCursor(cursor, 19);
        Boolean orgUnitScope = getBooleanFromCursor(cursor, 20);
        Boolean unique = getBooleanFromCursor(cursor, 21);
        Boolean inherit = getBooleanFromCursor(cursor, 22);

        OptionSet optionSet = optionSetUID == null ? null : OptionSet.builder()
                .uid(optionSetUID)
                .build();

        trackedEntityAttribute = TrackedEntityAttribute.builder()
                .uid(uid)
                .code(code)
                .displayName(displayName)
                .created(created)
                .lastUpdated(lastUpdated)
                .name(name)
                .shortName(shortName)
                .displayShortName(displayShortName)
                .description(description)
                .displayDescription(displayDescription)
                .pattern(pattern)
                .sortOrderInListNoProgram(sortOrderInListNoProgram)
                .optionSet(optionSet)
                .valueType(valueType)
                .expression(expression)
                .searchScope(searchScope)
                .programScope(programScope)
                .displayInListNoProgram(displayInListNoProgram)
                .generated(generated)
                .displayOnVisitSchedule(displayOnVisitSchedule)
                .orgUnitScope(orgUnitScope)
                .unique(unique)
                .inherit(inherit)
                .build();

        return trackedEntityAttribute;
    }

    @Nullable
    protected TrackedEntityAttributeSearchScope
    getTrackedEntityAttributeSearchScopeFromCursor(Cursor cursor, int index) {
        return cursor.getString(index) == null ? null :
                TrackedEntityAttributeSearchScope.valueOf(cursor.getString(index));
    }

}
