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

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

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

    private final SQLiteStatement insertRowStatement;
    private final DatabaseAdapter databaseAdapter;

    public TrackedEntityAttributeStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertRowStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
                       @Nullable String displayName, @NonNull Date created,
                       @NonNull Date lastUpdated, @Nullable String shortName,
                       @Nullable String displayShortName, @Nullable String description,
                       @Nullable String displayDescription, @Nullable String pattern,
                       @Nullable String sortOrderInListNoProgram, @Nullable String optionSet,
                       @NonNull ValueType valueType, @Nullable String expression,
                       @Nullable TrackedEntityAttributeSearchScope searchScope,
                       @Nullable Boolean programScope, @Nullable Boolean displayInListNoProgram,
                       @Nullable Boolean generated, @Nullable Boolean displayOnVisitSchedule,
                       @Nullable Boolean orgUnitScope, @Nullable Boolean unique,
                       @Nullable Boolean inherit) {

        insertRowStatement.clearBindings();

        sqLiteBind(insertRowStatement, 1, uid);
        sqLiteBind(insertRowStatement, 2, code);
        sqLiteBind(insertRowStatement, 3, name);
        sqLiteBind(insertRowStatement, 4, displayName);
        sqLiteBind(insertRowStatement, 5, created);
        sqLiteBind(insertRowStatement, 6, lastUpdated);
        sqLiteBind(insertRowStatement, 7, shortName);
        sqLiteBind(insertRowStatement, 8, displayShortName);
        sqLiteBind(insertRowStatement, 9, description);
        sqLiteBind(insertRowStatement, 10, displayDescription);
        sqLiteBind(insertRowStatement, 11, pattern);
        sqLiteBind(insertRowStatement, 12, sortOrderInListNoProgram);
        sqLiteBind(insertRowStatement, 13, optionSet);
        sqLiteBind(insertRowStatement, 14, valueType);
        sqLiteBind(insertRowStatement, 15, expression);
        sqLiteBind(insertRowStatement, 16, searchScope);
        sqLiteBind(insertRowStatement, 17, programScope);
        sqLiteBind(insertRowStatement, 18, displayInListNoProgram);
        sqLiteBind(insertRowStatement, 19, generated);
        sqLiteBind(insertRowStatement, 20, displayOnVisitSchedule);
        sqLiteBind(insertRowStatement, 21, orgUnitScope);
        sqLiteBind(insertRowStatement, 22, unique);
        sqLiteBind(insertRowStatement, 23, inherit);

        return databaseAdapter.executeInsert(TrackedEntityAttributeModel.TABLE, insertRowStatement);
    }

    @Override
    public void close() {
        insertRowStatement.close();
    }
}
