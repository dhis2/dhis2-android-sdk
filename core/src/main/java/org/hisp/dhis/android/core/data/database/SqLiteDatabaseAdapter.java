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

package org.hisp.dhis.android.core.data.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

public class SqLiteDatabaseAdapter implements DatabaseAdapter {

    private final DbOpenHelper dbOpenHelper;

    public SqLiteDatabaseAdapter(@NonNull DbOpenHelper dbOpenHelper) {
        if (dbOpenHelper == null) {
            throw new IllegalArgumentException("dbOpenHelper == null");
        }
        this.dbOpenHelper = dbOpenHelper;
    }

    @Override
    public Transaction beginNewTransaction() {
        Transaction transaction = new SqLiteTransaction(dbOpenHelper);
        transaction.begin();
        return transaction;
    }

    @Override
    public SQLiteStatement compileStatement(String sql) {
        return database().compileStatement(sql);
    }

    @Override
    public Cursor query(String sql, String... selectionArgs) {
        return readableDatabase().rawQuery(sql, selectionArgs);
    }

    @Override
    public long executeInsert(String table, SQLiteStatement sqLiteStatement) {
        return sqLiteStatement.executeInsert();
    }

    @Override
    public int executeUpdateDelete(String table, SQLiteStatement sqLiteStatement) {
        return sqLiteStatement.executeUpdateDelete();
    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        return database().delete(table, whereClause, whereArgs);
    }

    @Override
    public int delete(String table) {
        return delete(table, "1", null);
    }

    private SQLiteDatabase database() {
        return dbOpenHelper.getWritableDatabase();
    }

    private SQLiteDatabase readableDatabase() {
        return dbOpenHelper.getReadableDatabase();
    }
}
