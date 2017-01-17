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

 package org.hisp.dhis.android.core.organisationunit;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class OrganisationUnitStoreImpl implements OrganisationUnitStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + OrganisationUnitModel.ORGANISATION_UNIT + " (" +
            OrganisationUnitModel.Columns.UID + ", " +
            OrganisationUnitModel.Columns.CODE + ", " +
            OrganisationUnitModel.Columns.NAME + ", " +
            OrganisationUnitModel.Columns.DISPLAY_NAME + ", " +
            OrganisationUnitModel.Columns.CREATED + ", " +
            OrganisationUnitModel.Columns.LAST_UPDATED + ", " +
            OrganisationUnitModel.Columns.SHORT_NAME + ", " +
            OrganisationUnitModel.Columns.DISPLAY_SHORT_NAME + ", " +
            OrganisationUnitModel.Columns.DESCRIPTION + ", " +
            OrganisationUnitModel.Columns.DISPLAY_DESCRIPTION + ", " +
            OrganisationUnitModel.Columns.PATH + ", " +
            OrganisationUnitModel.Columns.OPENING_DATE + ", " +
            OrganisationUnitModel.Columns.CLOSED_DATE + ", " +
            OrganisationUnitModel.Columns.LEVEL + ", " +
            OrganisationUnitModel.Columns.PARENT + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteDatabase sqLiteDatabase;
    private final SQLiteStatement sqLiteStatement;

    public OrganisationUnitStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(
            @NonNull String uid,
            @Nullable String code,
            @Nullable String name,
            @Nullable String displayName,
            @Nullable Date created,
            @Nullable Date lastUpdated,
            @Nullable String shortName,
            @Nullable String displayShortName,
            @Nullable String description,
            @Nullable String displayDescription,
            @Nullable String path,
            @Nullable Date openingDate,
            @Nullable Date closedDate,
            @Nullable String parent,
            @Nullable Integer level) {
        sqLiteStatement.clearBindings();

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
        sqLiteBind(sqLiteStatement, 11, path);
        sqLiteBind(sqLiteStatement, 12, openingDate);
        sqLiteBind(sqLiteStatement, 13, closedDate);
        sqLiteBind(sqLiteStatement, 14, level);
        sqLiteBind(sqLiteStatement, 15, parent);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public int delete() {
        return sqLiteDatabase.delete(OrganisationUnitModel.ORGANISATION_UNIT, null, null);
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
