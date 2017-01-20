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
import android.support.annotation.Nullable;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class UserCredentialsStoreImpl implements UserCredentialsStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + UserCredentialsModel.TABLE + " (" +
            UserCredentialsModel.Columns.UID + ", " +
            UserCredentialsModel.Columns.CODE + ", " +
            UserCredentialsModel.Columns.NAME + ", " +
            UserCredentialsModel.Columns.DISPLAY_NAME + ", " +
            UserCredentialsModel.Columns.CREATED + ", " +
            UserCredentialsModel.Columns.LAST_UPDATED + ", " +
            UserCredentialsModel.Columns.USERNAME + ", " +
            UserCredentialsModel.Columns.USER + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private final SQLiteDatabase sqLiteDatabase;
    private final SQLiteStatement insertStatement;

    public UserCredentialsStoreImpl(SQLiteDatabase database) {
        this.sqLiteDatabase = database;
        this.insertStatement = database.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(
            @NonNull String uid, @Nullable String code, @Nullable String name,
            @Nullable String displayName, @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String username, @NonNull String user) {
        insertStatement.clearBindings();

        sqLiteBind(insertStatement, 1, uid);
        sqLiteBind(insertStatement, 2, code);
        sqLiteBind(insertStatement, 3, name);
        sqLiteBind(insertStatement, 4, displayName);
        sqLiteBind(insertStatement, 5, created);
        sqLiteBind(insertStatement, 6, lastUpdated);
        sqLiteBind(insertStatement, 7, username);
        sqLiteBind(insertStatement, 8, user);

        return insertStatement.executeInsert();
    }

    @Override
    public int delete() {
        return sqLiteDatabase.delete(UserCredentialsModel.TABLE, null, null);
    }

    @Override
    public void close() {
        insertStatement.close();
    }
}
