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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public class AuthenticatedUserStoreImpl implements AuthenticatedUserStore {
    private static final String[] PROJECTION = new String[]{
            AuthenticatedUserModel.Columns.ID,
            AuthenticatedUserModel.Columns.USER,
            AuthenticatedUserModel.Columns.CREDENTIALS
    };

    private static final String INSERT_STATEMENT = "INSERT INTO " + AuthenticatedUserModel.TABLE +
            " (" + AuthenticatedUserModel.Columns.USER + ", " + AuthenticatedUserModel.Columns.CREDENTIALS + ")" +
            " VALUES (?, ?);";

    private final SQLiteDatabase sqLiteDatabase;
    private final SQLiteStatement insertRowStatement;

    public AuthenticatedUserStoreImpl(@NonNull SQLiteDatabase database) {
        this.sqLiteDatabase = database;
        this.insertRowStatement = database.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String userUid, @NonNull String credentials) {
        insertRowStatement.clearBindings();
        sqLiteBind(insertRowStatement, 1, userUid);
        sqLiteBind(insertRowStatement, 2, credentials);
        return insertRowStatement.executeInsert();
    }

    @NonNull
    @Override
    public List<AuthenticatedUserModel> query() {
        List<AuthenticatedUserModel> rows = new ArrayList<>();

        Cursor queryCursor = sqLiteDatabase.query(AuthenticatedUserModel.TABLE,
                PROJECTION, null, null, null, null, null);

        if (queryCursor == null) {
            return rows;
        }

        try {
            if (queryCursor.getCount() > 0) {
                queryCursor.moveToFirst();

                do {
                    rows.add(AuthenticatedUserModel.create(queryCursor));
                } while (queryCursor.moveToNext());
            }
        } finally {
            queryCursor.close();
        }

        return rows;
    }

    @Override
    public int delete() {
        return sqLiteDatabase.delete(AuthenticatedUserModel.TABLE, null, null);
    }

    @Override
    public void close() {
        insertRowStatement.close();
    }
}
