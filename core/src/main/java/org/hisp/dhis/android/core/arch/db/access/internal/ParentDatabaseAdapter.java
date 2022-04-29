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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.Transaction;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper;

class ParentDatabaseAdapter implements DatabaseAdapter {

    private DatabaseAdapter adapter;

    private DatabaseAdapter getAdapter() {
        if (adapter == null) {
            throw new RuntimeException("Please login to access the database.");
        } else {
            return adapter;
        }
    }

    void setAdapter(DatabaseAdapter adapter) {
        this.adapter = adapter;
    }

    void removeAdapter() {
        this.adapter = null;
    }

    @Override
    public Transaction beginNewTransaction() {
        return getAdapter().beginNewTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        getAdapter().setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        getAdapter().endTransaction();
    }

    @Override
    public void execSQL(String sql) {
        getAdapter().execSQL(sql);
    }

    @Override
    public StatementWrapper compileStatement(String sql) {
        return getAdapter().compileStatement(sql);
    }

    @Override
    public Cursor rawQuery(String sql, String... selectionArgs) {
        return getAdapter().rawQuery(sql, selectionArgs);
    }

    @Override
    public Cursor query(String sql, String[] columns) {
        return getAdapter().query(sql, columns);
    }

    @Override
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs) {
        return getAdapter().query(table, columns, selection, selectionArgs);
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
        return getAdapter().delete(table, whereClause, whereArgs);
    }

    @Override
    public int delete(String table) {
        return delete(table, "1", null);
    }

    @Override
    public long insert(String table, String nullColumnHack, ContentValues values) {
        return getAdapter().insert(table, nullColumnHack, values);
    }

    @Override
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return getAdapter().update(table, values, whereClause, whereArgs);
    }

    @Override
    public void setForeignKeyConstraintsEnabled(boolean enable) {
        getAdapter().setForeignKeyConstraintsEnabled(enable);
    }

    @Override
    public void enableWriteAheadLogging() {
        getAdapter().enableWriteAheadLogging();
    }

    @Override
    public void close() {
        if (adapter != null) {
            adapter.close();
        }
    }

    @Override
    public String getDatabaseName() {
        return getAdapter().getDatabaseName();
    }

    @Override
    public boolean isReady() {
        return adapter != null;
    }

    @Override
    public int hashCode() {
        return getAdapter().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return getAdapter().equals(o);
    }
}
