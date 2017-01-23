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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class UserOrganisationUnitLinkStoreImpl implements UserOrganisationUnitLinkStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            UserOrganisationUnitLinkModel.TABLE + " (" +
            UserOrganisationUnitLinkModel.Columns.USER + ", " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT + ", " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE + ") " +
            "VALUES (?, ?, ?);";

    private final SQLiteDatabase sqLiteDatabase;
    private final SQLiteStatement sqLiteStatement;

    public UserOrganisationUnitLinkStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String user, @NonNull String organisationUnit,
            @NonNull String organisationUnitScope) {

        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, user);
        sqLiteBind(sqLiteStatement, 2, organisationUnit);
        sqLiteBind(sqLiteStatement, 3, organisationUnitScope);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public int delete() {
        return sqLiteDatabase.delete(UserOrganisationUnitLinkModel.TABLE, null, null);
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
