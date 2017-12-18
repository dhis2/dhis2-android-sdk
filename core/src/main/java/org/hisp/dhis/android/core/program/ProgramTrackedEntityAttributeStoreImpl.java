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

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
public class ProgramTrackedEntityAttributeStoreImpl implements ProgramTrackedEntityAttributeStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            ProgramTrackedEntityAttributeModel.TABLE + " (" +
            ProgramTrackedEntityAttributeModel.Columns.UID + ", " +
            ProgramTrackedEntityAttributeModel.Columns.CODE + ", " +
            ProgramTrackedEntityAttributeModel.Columns.NAME + ", " +
            ProgramTrackedEntityAttributeModel.Columns.DISPLAY_NAME + ", " +
            ProgramTrackedEntityAttributeModel.Columns.CREATED + ", " +
            ProgramTrackedEntityAttributeModel.Columns.LAST_UPDATED + ", " +
            ProgramTrackedEntityAttributeModel.Columns.SHORT_NAME + ", " +
            ProgramTrackedEntityAttributeModel.Columns.DISPLAY_SHORT_NAME + ", " +
            ProgramTrackedEntityAttributeModel.Columns.DESCRIPTION + ", " +
            ProgramTrackedEntityAttributeModel.Columns.DISPLAY_DESCRIPTION + ", " +
            ProgramTrackedEntityAttributeModel.Columns.MANDATORY + ", " +
            ProgramTrackedEntityAttributeModel.Columns.TRACKED_ENTITY_ATTRIBUTE + ", " +
            ProgramTrackedEntityAttributeModel.Columns.ALLOW_FUTURE_DATES + ", " +
            ProgramTrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST + ", " +
            ProgramTrackedEntityAttributeModel.Columns.PROGRAM + ", " +
            ProgramTrackedEntityAttributeModel.Columns.SORT_ORDER +
            ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_STATEMENT = "UPDATE " + ProgramTrackedEntityAttributeModel.TABLE +
            " SET " +
            ProgramTrackedEntityAttributeModel.Columns.UID + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.CODE + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.NAME + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.DISPLAY_NAME + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.CREATED + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.LAST_UPDATED + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.SHORT_NAME + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.DISPLAY_SHORT_NAME + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.DESCRIPTION + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.DISPLAY_DESCRIPTION + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.MANDATORY + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.TRACKED_ENTITY_ATTRIBUTE + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.ALLOW_FUTURE_DATES + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.PROGRAM + " =?, " +
            ProgramTrackedEntityAttributeModel.Columns.SORT_ORDER + " =? " +
            " WHERE " +
            ProgramTrackedEntityAttributeModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + ProgramTrackedEntityAttributeModel.TABLE +
            " WHERE " +
            ProgramTrackedEntityAttributeModel.Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public ProgramTrackedEntityAttributeStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
                       @Nullable String displayName, @NonNull Date created,
                       @NonNull Date lastUpdated, @Nullable String shortName,
                       @Nullable String displayShortName, @Nullable String description,
                       @Nullable String displayDescription, @Nullable Boolean mandatory,
                       @NonNull String trackedEntityAttribute, @Nullable Boolean allowFutureDates,
                       @Nullable Boolean displayInList, @NonNull String program, @Nullable Integer sortOrder) {
        isNull(uid);
        isNull(trackedEntityAttribute);
        isNull(program);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated, shortName,
                displayShortName, description, displayDescription, mandatory, trackedEntityAttribute,
                allowFutureDates, displayInList, program, sortOrder);

        Long insert = databaseAdapter.executeInsert(ProgramTrackedEntityAttributeModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return insert;
    }

    @Override
    public int update(@NonNull String uid, @Nullable String code, @NonNull String name, @Nullable String displayName,
                      @NonNull Date created, @NonNull Date lastUpdated, @Nullable String shortName,
                      @Nullable String displayShortName, @Nullable String description,
                      @Nullable String displayDescription, @Nullable Boolean mandatory,
                      @NonNull String trackedEntityAttribute, @Nullable Boolean allowFutureDates,
                      @Nullable Boolean displayInList, @NonNull String program, @Nullable Integer sortOrder,
                      @NonNull String whereProgramTrackedEntityAttributeUid) {
        isNull(uid);
        isNull(trackedEntityAttribute);
        isNull(program);
        isNull(whereProgramTrackedEntityAttributeUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated, shortName,
                displayShortName, description, displayDescription, mandatory, trackedEntityAttribute,
                allowFutureDates, displayInList, program, sortOrder);

        // bind the where argument
        sqLiteBind(updateStatement, 17, whereProgramTrackedEntityAttributeUid);

        // execute and clear bindings
        int update = databaseAdapter.executeUpdateDelete(ProgramTrackedEntityAttributeModel.TABLE, updateStatement);
        updateStatement.clearBindings();
        return update;
    }

    @Override
    public int delete(@NonNull String uid) {
        isNull(uid);
        // bind the where argument
        sqLiteBind(deleteStatement, 1, uid);

        // execute and clear bindings
        int delete = databaseAdapter.executeUpdateDelete(ProgramTrackedEntityAttributeModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();
        return delete;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement, @NonNull String uid, @Nullable String code,
                               @NonNull String name, @Nullable String displayName, @NonNull Date created,
                               @NonNull Date lastUpdated, @Nullable String shortName,
                               @Nullable String displayShortName, @Nullable String description,
                               @Nullable String displayDescription, @Nullable Boolean mandatory,
                               @NonNull String trackedEntityAttribute, @Nullable Boolean allowFutureDates,
                               @Nullable Boolean displayInList, @NonNull String program, @Nullable Integer sortOrder) {
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
        sqLiteBind(sqLiteStatement, 11, mandatory);
        sqLiteBind(sqLiteStatement, 12, trackedEntityAttribute);
        sqLiteBind(sqLiteStatement, 13, allowFutureDates);
        sqLiteBind(sqLiteStatement, 14, displayInList);
        sqLiteBind(sqLiteStatement, 15, program);
        sqLiteBind(sqLiteStatement, 16, sortOrder);
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(ProgramTrackedEntityAttributeModel.TABLE);
    }
}
