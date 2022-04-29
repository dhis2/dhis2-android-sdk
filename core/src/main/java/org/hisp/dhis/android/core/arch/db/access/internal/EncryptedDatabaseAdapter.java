/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.arch.db.access.internal;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.Transaction;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper;

class EncryptedDatabaseAdapter implements DatabaseAdapter {

    private final SQLiteDatabase database;
    private final String databaseName;

    EncryptedDatabaseAdapter(@NonNull SQLiteDatabase database, @NonNull String databaseName) {
        if (database == null) {
            throw new IllegalArgumentException("database == null");
        }
        this.database = database;
        this.databaseName = databaseName;
    }

    @Override
    public Transaction beginNewTransaction() {
        database.beginTransaction();
        return new TransactionImpl(this);
    }

    @Override
    public void setTransactionSuccessful() {
        database.setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        database.endTransaction();
    }

    @Override
    public void execSQL(String sql) {
        database.execSQL(sql);
    }

    @Override
    public StatementWrapper compileStatement(String sql) {
        return new EncryptedStatementWrapper(database.compileStatement(sql));
    }

    @Override
    public Cursor rawQuery(String sql, String... selectionArgs) {
        return database.rawQuery(sql, selectionArgs);
    }

    @Override
    public Cursor query(String sql, String[] columns) {
        return database.query(sql, columns, null, null, null, null, null);
    }

    @Override
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs) {
        return database.query(table, columns, selection, selectionArgs, null, null, null);
    }

    @Override
    public long executeInsert(StatementWrapper sqLiteStatement) {
        return sqLiteStatement.executeInsert();
    }

    @Override
    public int executeUpdateDelete(StatementWrapper sqLiteStatement) {
        return sqLiteStatement.executeUpdateDelete();
    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        return database.delete(table, whereClause, whereArgs);
    }

    @Override
    public int delete(String table) {
        return delete(table, "1", null);
    }

    @Override
    public long insert(String table, String nullColumnHack, ContentValues values) {
        return database.insert(table, nullColumnHack, values);
    }

    @Override
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return database.update(table, values, whereClause, whereArgs);
    }

    @Override
    public void setForeignKeyConstraintsEnabled(boolean enable) {
        database.setForeignKeyConstraintsEnabled(enable);
    }

    @Override
    public void enableWriteAheadLogging() {
        database.enableWriteAheadLogging();
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void close() {
        database.close();
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }
}
