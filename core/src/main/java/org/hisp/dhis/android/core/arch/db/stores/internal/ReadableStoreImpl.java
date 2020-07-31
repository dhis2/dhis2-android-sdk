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

package org.hisp.dhis.android.core.arch.db.stores.internal;

import android.database.Cursor;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.cursors.internal.ObjectFactory;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.ReadOnlySQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType;
import org.hisp.dhis.android.core.common.CoreObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadableStoreImpl<M extends CoreObject> implements ReadableStore<M> {
    protected final DatabaseAdapter databaseAdapter;
    protected final ReadOnlySQLStatementBuilder builder;
    final ObjectFactory<M> objectFactory;

    public ReadableStoreImpl(DatabaseAdapter databaseAdapter,
                             ReadOnlySQLStatementBuilder builder,
                             ObjectFactory<M> objectFactory) {
        this.databaseAdapter = databaseAdapter;
        this.builder = builder;
        this.objectFactory = objectFactory;
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
    public M selectOneOrderedBy(String orderingColumName, SQLOrderType orderingType) {
        Cursor cursor = databaseAdapter.rawQuery(builder.selectOneOrderedBy(orderingColumName, orderingType));
        return getFirstFromCursor(cursor);
    }



    @Override
    public List<M> selectRawQuery(String sqlRawQuery) {
        Cursor cursor = databaseAdapter.rawQuery(sqlRawQuery);
        List<M> list = new ArrayList<>();
        addObjectsToCollection(cursor, list);
        return list;
    }

    @Override
    public M selectOneWhere(@NonNull String whereClause) {
        Cursor cursor = databaseAdapter.rawQuery(builder.selectWhere(whereClause, 1));
        return getFirstFromCursor(cursor);
    }

    @Override
    public M selectFirst() {
        Cursor cursor = databaseAdapter.rawQuery(builder.selectAll());
        return getFirstFromCursor(cursor);
    }

    M getFirstFromCursor(@NonNull Cursor cursor) {
        try {
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                return objectFactory.fromCursor(cursor);
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    @Override
    public int count() {
        return processCount(databaseAdapter.rawQuery(builder.count()));
    }

    @Override
    public int countWhere(@NonNull String whereClause) {
        return processCount(databaseAdapter.rawQuery(builder.countWhere(whereClause)));
    }

    @Override
    public Map<String, Integer> groupAndGetCountBy(@NonNull String column) {
        Map<String, Integer> result = new HashMap<>();
        try(Cursor cursor = databaseAdapter.rawQuery(builder.countAndGroupBy(column))) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String columnValue = cursor.getString(0);
                    Integer countValue = cursor.getInt(1);
                    result.put(columnValue, countValue);
                }
                while (cursor.moveToNext());
            }
        }
        return result;
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
                    collection.add(objectFactory.fromCursor(cursor));
                }
                while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
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