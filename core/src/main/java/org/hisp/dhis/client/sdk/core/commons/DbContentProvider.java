/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.core.commons;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.hisp.dhis.client.sdk.core.event.EventMapper;

import java.util.ArrayList;

import static android.content.ContentUris.parseId;
import static android.content.ContentUris.withAppendedId;
import static android.text.TextUtils.isEmpty;

public class DbContentProvider extends ContentProvider {
    private static final int EVENTS = 100;
    private static final int EVENT_ID = 101;

    private static final UriMatcher URI_MATCHER = buildMatcher();

    private DbHelper mDbHelper;

    private static UriMatcher buildMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(DbContract.AUTHORITY, EventMapper.EVENTS, EVENTS);
        matcher.addURI(DbContract.AUTHORITY, EventMapper.EVENTS_ID, EVENT_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case EVENTS:
                return EventMapper.CONTENT_TYPE;
            case EVENT_ID:
                return EventMapper.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("No corresponding Uri type was found");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case EVENTS: {
                return query(uri, EventMapper.EventColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case EVENT_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, EventMapper.EventColumns.TABLE_NAME,
                        EventMapper.EventColumns.COLUMN_ID, projection, selection, selectionArgs, sortOrder, id);
            }
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private Cursor query(Uri uri, String tableName, String[] projection,
                         String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        qBuilder.setTables(tableName);

        Cursor cursor = qBuilder.query(
                db, projection, selection, selectionArgs, null, null, sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor queryId(Uri uri, String tableName, String colId, String[] projection,
                           String selection, String[] selectionArgs, String sortOrder, String id) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        qBuilder.setTables(tableName);
        qBuilder.appendWhere(colId + " = " + id);

        Cursor cursor = qBuilder.query(
                db, projection, selection, selectionArgs, null, null, sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (URI_MATCHER.match(uri)) {
            case EVENTS: {
                return insert(EventMapper.EventColumns.TABLE_NAME, values, uri);
            }
            default: {
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
            }
        }
    }

    private Uri insert(String tableName, ContentValues values, Uri uri) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case EVENTS: {
                return delete(EventMapper.EventColumns.TABLE_NAME, selection, selectionArgs);
            }
            case EVENT_ID: {
                return deleteId(uri, EventMapper.EventColumns.TABLE_NAME,
                        EventMapper.EventColumns.COLUMN_ID, selection, selectionArgs);
            }
            default: {
                throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
        }
    }

    private int delete(String tableName, String selection,
                       String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.delete(tableName, selection, selectionArgs);
    }

    private int deleteId(Uri uri, String tableName, String colId,
                         String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = parseId(uri);
        String where = colId + " = " + id;
        if (!isEmpty(selection)) {
            where += " AND " + selection;
        }

        return db.delete(tableName, where, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case EVENTS: {
                return update(EventMapper.EventColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case EVENT_ID: {
                return updateId(uri, EventMapper.EventColumns.TABLE_NAME,
                        EventMapper.EventColumns.COLUMN_ID, selection, selectionArgs, values);
            }
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    private int update(String tableName, String selection,
                       String[] selectionArgs, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.update(tableName, values, selection, selectionArgs);
    }

    private int updateId(Uri uri, String tableName, String colId,
                         String selection, String[] selectionArgs,
                         ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = parseId(uri);
        String where = colId + " = " + id;
        if (!isEmpty(selection)) {
            where += " AND " + selection;
        }

        return db.update(tableName, values, where, selectionArgs);
    }

    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];

            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }
}