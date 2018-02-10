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

package org.hisp.dhis.android.core.user;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.Store;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
public class UserOrganisationUnitLinkStoreImpl extends Store implements
        UserOrganisationUnitLinkStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            UserOrganisationUnitLinkModel.TABLE + " (" +
            UserOrganisationUnitLinkModel.Columns.USER + ", " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT + ", " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE + ") " +
            "VALUES (?, ?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + UserOrganisationUnitLinkModel.TABLE + " SET " +
            UserOrganisationUnitLinkModel.Columns.USER + " =?, " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT + "=?, " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE + "=? " +
            " WHERE " +
            UserOrganisationUnitLinkModel.Columns.USER + " = ? AND " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT + " = ? AND " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + UserOrganisationUnitLinkModel.TABLE +
            " WHERE " + UserOrganisationUnitLinkModel.Columns.USER + " =? AND " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT + " =? AND " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE + " =?;";

    private static final String QUERY_USER_UID_BY_ORGANISATION_UNIT_UID = "SELECT " +
            UserOrganisationUnitLinkModel.Columns.USER
            + "  FROM " + UserOrganisationUnitLinkModel.TABLE + " WHERE " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT + "=?;";

    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    public UserOrganisationUnitLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String user, @NonNull String organisationUnit, @NonNull String organisationUnitScope) {
        isNull(user);
        isNull(organisationUnit);
        isNull(organisationUnitScope);

        bindArguments(insertStatement, user, organisationUnit, organisationUnitScope);

        Long insert = databaseAdapter.executeInsert(UserOrganisationUnitLinkModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return insert;
    }

    @Override
    public int update(@NonNull String user, @NonNull String organisationUnit, @NonNull String organisationUnitScope,
                      @NonNull String whereUserUid, @NonNull String whereOrganisationUnitUid,
                      @NonNull String whereOrganisationUnitScope) {
        isNull(user);
        isNull(organisationUnit);
        isNull(organisationUnitScope);
        isNull(whereUserUid);
        isNull(whereOrganisationUnitUid);
        isNull(whereOrganisationUnitScope);

        bindArguments(updateStatement, user, organisationUnit, organisationUnitScope);
        sqLiteBind(updateStatement, 4, whereUserUid);
        sqLiteBind(updateStatement, 5, whereOrganisationUnitUid);
        sqLiteBind(updateStatement, 6, whereOrganisationUnitScope);

        int update = databaseAdapter.executeUpdateDelete(UserOrganisationUnitLinkModel.TABLE, updateStatement);
        updateStatement.clearBindings();
        return update;
    }

    @Override
    public int delete(@NonNull String userUid, @NonNull String organisationUnitUid,
                      @NonNull String organisationUnitScope) {
        isNull(userUid);
        isNull(organisationUnitUid);
        isNull(organisationUnitScope);

        bindArguments(deleteStatement, userUid, organisationUnitUid, organisationUnitScope);

        int delete = databaseAdapter.executeUpdateDelete(UserOrganisationUnitLinkModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();
        return delete;
    }

    private void bindArguments(SQLiteStatement sqLiteStatement, @NonNull String user, @NonNull String organisationUnit,
                               @NonNull String organisationUnitScope) {
        sqLiteBind(sqLiteStatement, 1, user);
        sqLiteBind(sqLiteStatement, 2, organisationUnit);
        sqLiteBind(sqLiteStatement, 3, organisationUnitScope);
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(UserOrganisationUnitLinkModel.TABLE);
    }

    @Override
    public String queryUserUIdByOrganisationUnitUId(String organisationUnitUId) {
        Cursor cursor = databaseAdapter.query(QUERY_USER_UID_BY_ORGANISATION_UNIT_UID,
                organisationUnitUId);
        String userUId = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            userUId = getStringFromCursor(cursor, 0);
        }
        return userUId;
    }

}
