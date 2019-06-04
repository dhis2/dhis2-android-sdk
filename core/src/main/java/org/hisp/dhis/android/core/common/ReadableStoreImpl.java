/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.common;

import android.database.Cursor;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReadableStoreImpl<M extends Model> implements ReadableStore<M> {
    protected final DatabaseAdapter databaseAdapter;
    protected final SQLStatementBuilder builder;
    final CursorModelFactory<M> modelFactory;

    public ReadableStoreImpl(DatabaseAdapter databaseAdapter,
                             SQLStatementBuilder builder,
                             CursorModelFactory<M> modelFactory) {
        this.databaseAdapter = databaseAdapter;
        this.builder = builder;
        this.modelFactory = modelFactory;
    }

    @Override
    public List<M> selectAll() {
        String query = builder.selectAll();
        return selectRawQuery(query);
    }

    @Override
    public List<M> selectWhere(String whereClause) {
        String query = builder.selectWhere(whereClause);
        return selectRawQuery(query);
    }

    @Override
    public List<M> selectWhere(String filterWhereClause, String orderByClause) {
        String query = builder.selectWhere(filterWhereClause, orderByClause);
        return selectRawQuery(query);
    }

    @Override
    public List<M> selectWhere(String filterWhereClause, String orderByClause, int limit) {
        String query = builder.selectWhere(filterWhereClause, orderByClause, limit);
        return selectRawQuery(query);
    }

    @Override
    public List<M> selectRawQuery(String sqlRawQuery) {
        Cursor cursor = databaseAdapter.query(sqlRawQuery);
        List<M> list = new ArrayList<>();
        addObjectsToCollection(cursor, list);
        return list;
    }

    @Override
    public M selectOneWhere(@NonNull String whereClause) {
        Cursor cursor = databaseAdapter.query(builder.selectWhere(whereClause, 1));
        return getFirstFromCursor(cursor);
    }

    @Override
    public M selectOneOrderedBy(String orderingColumName, SQLOrderType orderingType) {
        Cursor cursor = databaseAdapter.query(builder.selectOneOrderedBy(orderingColumName, orderingType));
        return getFirstFromCursor(cursor);
    }

    @Override
    public M selectFirst() {
        Cursor cursor = databaseAdapter.query(builder.selectAll());
        return getFirstFromCursor(cursor);
    }

    private M getFirstFromCursor(@NonNull Cursor cursor) {
        try {
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                return modelFactory.fromCursor(cursor);
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    @Override
    public int count() {
        return processCount(databaseAdapter.query(builder.count()));
    }

    @Override
    public int countWhere(@NonNull String whereClause) {
        return processCount(databaseAdapter.query(builder.countWhere(whereClause)));
    }

    protected int processCount(Cursor cursor) {
        try {
            cursor.moveToFirst();
            return cursor.getInt(0);
        } finally {
            cursor.close();
        }
    }

    protected void addObjectsToCollection(Cursor cursor, Collection<M> collection) {
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    collection.add(modelFactory.fromCursor(cursor));
                }
                while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
    }

    @Override
    public List<String> selectStringColumnsWhereClause(String column, String clause) {
        Cursor cursor = databaseAdapter.query(builder.selectColumnWhere(column, clause));
        return mapStringColumnSetFromCursor(cursor);
    }

    List<String> mapStringColumnSetFromCursor(Cursor cursor) {
        List<String> columns = new ArrayList<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    columns.add(cursor.getString(0));
                }
                while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return columns;
    }
}