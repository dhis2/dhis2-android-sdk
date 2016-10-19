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

package org.hisp.dhis.client.sdk.core.commons.database;

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

import org.hisp.dhis.client.sdk.core.event.EventTable;
import org.hisp.dhis.client.sdk.core.event.EventTable.EventColumns;
import org.hisp.dhis.client.sdk.core.option.OptionSetTable;
import org.hisp.dhis.client.sdk.core.option.OptionSetTable.OptionSetColumns;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitTable;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitTable.OrganisationUnitColumns;
import org.hisp.dhis.client.sdk.core.program.ProgramTable;
import org.hisp.dhis.client.sdk.core.program.ProgramTable.ProgramColumns;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueTable;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueTable.TrackedEntityDataValueColumns;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityTable;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityTable.TrackedEntityColumns;
import org.hisp.dhis.client.sdk.core.user.UserTable;
import org.hisp.dhis.client.sdk.core.user.UserTable.UserColumns;

import java.util.ArrayList;

import static android.content.ContentUris.parseId;
import static android.content.ContentUris.withAppendedId;
import static android.text.TextUtils.isEmpty;

public class DbContentProvider extends ContentProvider {
    private static final int EVENTS = 100;
    private static final int EVENT_ID = 101;

    private static final int PROGRAMS = 200;
    private static final int PROGRAM_ID = 201;

    private static final int OPTIONSETS = 300;
    private static final int OPTIONSET_ID = 301;

    private static final int ORGANISATION_UNITS = 400;
    private static final int ORGANISATION_UNIT_ID = 401;

    private static final int TRACKED_ENTITIES = 500;
    private static final int TRACKED_ENTITY_ID = 501;

    private static final int TRACKED_ENTITY_DATA_VALUES = 600;
    private static final int TRACKED_ENTITY_DATA_VALUE_ID = 601;

    private static final int USERS = 700;
    private static final int USER_ID = 701;

    private static final UriMatcher URI_MATCHER = buildMatcher();

    private DbHelper mDbHelper;

    private static UriMatcher buildMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(DbContract.AUTHORITY, EventTable.EVENTS, EVENTS);
        matcher.addURI(DbContract.AUTHORITY, EventTable.EVENT_ID, EVENT_ID);

        matcher.addURI(DbContract.AUTHORITY, ProgramTable.PROGRAMS, PROGRAMS);
        matcher.addURI(DbContract.AUTHORITY, ProgramTable.PROGRAM_ID, PROGRAM_ID);

        matcher.addURI(DbContract.AUTHORITY, OptionSetTable.OPTION_SETS, OPTIONSETS);
        matcher.addURI(DbContract.AUTHORITY, OptionSetTable.OPTION_SET_ID, OPTIONSET_ID);

        matcher.addURI(DbContract.AUTHORITY, OrganisationUnitTable.ORGANISATION_UNITS, ORGANISATION_UNITS);
        matcher.addURI(DbContract.AUTHORITY, OrganisationUnitTable.ORGANISATION_UNIT_ID, ORGANISATION_UNIT_ID);

        matcher.addURI(DbContract.AUTHORITY, TrackedEntityTable.TRACKED_ENTITIES, TRACKED_ENTITIES);
        matcher.addURI(DbContract.AUTHORITY, TrackedEntityTable.TRACKED_ENTITY_ID, TRACKED_ENTITY_ID);

        matcher.addURI(DbContract.AUTHORITY, TrackedEntityDataValueTable.TRACKED_ENTITY_DATA_VALUES, TRACKED_ENTITY_DATA_VALUES);
        matcher.addURI(DbContract.AUTHORITY, TrackedEntityDataValueTable.TRACKED_ENTITY_DATA_VALUE_ID, TRACKED_ENTITY_DATA_VALUE_ID);

        matcher.addURI(DbContract.AUTHORITY, UserTable.USERS, USERS);
        matcher.addURI(DbContract.AUTHORITY, UserTable.USER_ID, USER_ID);

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
                return EventTable.CONTENT_TYPE;
            case EVENT_ID:
                return EventTable.CONTENT_ITEM_TYPE;
            case PROGRAMS:
                return ProgramTable.CONTENT_TYPE;
            case PROGRAM_ID:
                return ProgramTable.CONTENT_ITEM_TYPE;
            case OPTIONSETS:
                return OptionSetTable.CONTENT_TYPE;
            case OPTIONSET_ID:
                return OptionSetTable.CONTENT_ITEM_TYPE;
            case ORGANISATION_UNITS:
                return OrganisationUnitTable.CONTENT_TYPE;
            case ORGANISATION_UNIT_ID:
                return OrganisationUnitTable.CONTENT_ITEM_TYPE;
            case TRACKED_ENTITIES:
                return TrackedEntityTable.CONTENT_TYPE;
            case TRACKED_ENTITY_ID:
                return TrackedEntityTable.CONTENT_ITEM_TYPE;
            case TRACKED_ENTITY_DATA_VALUES:
                return TrackedEntityDataValueTable.CONTENT_TYPE;
            case TRACKED_ENTITY_DATA_VALUE_ID:
                return TrackedEntityDataValueTable.CONTENT_ITEM_TYPE;
            case USERS:
                return UserTable.CONTENT_TYPE;
            case USER_ID:
                return UserTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("No corresponding Uri type was found");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case EVENTS: {
                return query(uri, EventColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case EVENT_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, EventColumns.TABLE_NAME,
                        EventColumns.COLUMN_ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case PROGRAMS: {
                return query(uri, ProgramColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case PROGRAM_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, ProgramColumns.TABLE_NAME,
                        ProgramColumns.COLUMN_ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case OPTIONSETS: {
                return query(uri, OptionSetColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case OPTIONSET_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, OptionSetColumns.TABLE_NAME,
                        OptionSetColumns.COLUMN_ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case ORGANISATION_UNITS: {
                return query(uri, OrganisationUnitColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case ORGANISATION_UNIT_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, OrganisationUnitColumns.TABLE_NAME,
                        OrganisationUnitColumns.COLUMN_ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case TRACKED_ENTITIES: {
                return query(uri, TrackedEntityColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case TRACKED_ENTITY_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, TrackedEntityColumns.TABLE_NAME,
                        TrackedEntityColumns.COLUMN_ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case TRACKED_ENTITY_DATA_VALUES: {
                return query(uri, TrackedEntityDataValueColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case TRACKED_ENTITY_DATA_VALUE_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, TrackedEntityDataValueColumns.TABLE_NAME,
                        TrackedEntityDataValueColumns.COLUMN_ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case USERS: {
                return query(uri, UserColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case USER_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, UserColumns.TABLE_NAME,
                        UserColumns.COLUMN_ID, projection, selection, selectionArgs, sortOrder, id);
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
                return insert(EventColumns.TABLE_NAME, values, uri);
            }
            case PROGRAMS: {
                return insert(ProgramColumns.TABLE_NAME, values, uri);
            }
            case OPTIONSETS: {
                return insert(OptionSetColumns.TABLE_NAME, values, uri);
            }
            case ORGANISATION_UNITS: {
                return insert(OrganisationUnitColumns.TABLE_NAME, values, uri);
            }
            case TRACKED_ENTITIES: {
                return insert(TrackedEntityColumns.TABLE_NAME, values, uri);
            }
            case TRACKED_ENTITY_DATA_VALUES: {
                return insert(TrackedEntityDataValueColumns.TABLE_NAME, values, uri);
            }
            case USERS: {
                return insert(UserColumns.TABLE_NAME, values, uri);
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
                return delete(EventColumns.TABLE_NAME, selection, selectionArgs);
            }
            case EVENT_ID: {
                return deleteId(uri, EventColumns.TABLE_NAME,
                        EventColumns.COLUMN_ID, selection, selectionArgs);
            }
            case PROGRAMS: {
                return delete(ProgramColumns.TABLE_NAME, selection, selectionArgs);
            }
            case PROGRAM_ID: {
                return deleteId(uri, ProgramColumns.TABLE_NAME,
                        ProgramColumns.COLUMN_ID, selection, selectionArgs);
            }
            case OPTIONSETS: {
                return delete(OptionSetColumns.TABLE_NAME, selection, selectionArgs);
            }
            case OPTIONSET_ID: {
                return deleteId(uri, OptionSetColumns.TABLE_NAME,
                        OptionSetColumns.COLUMN_ID, selection, selectionArgs);
            }
            case ORGANISATION_UNITS: {
                return delete(OrganisationUnitColumns.TABLE_NAME, selection, selectionArgs);
            }
            case ORGANISATION_UNIT_ID: {
                return deleteId(uri, OrganisationUnitColumns.TABLE_NAME,
                        OrganisationUnitColumns.COLUMN_ID, selection, selectionArgs);
            }
            case TRACKED_ENTITIES: {
                return delete(TrackedEntityColumns.TABLE_NAME, selection, selectionArgs);

            }
            case TRACKED_ENTITY_ID: {
                return deleteId(uri, TrackedEntityColumns.TABLE_NAME,
                        TrackedEntityColumns.COLUMN_ID, selection, selectionArgs);
            }
            case TRACKED_ENTITY_DATA_VALUES: {
                return delete(TrackedEntityDataValueColumns.TABLE_NAME, selection, selectionArgs);
            }
            case TRACKED_ENTITY_DATA_VALUE_ID: {
                return deleteId(uri, TrackedEntityDataValueColumns.TABLE_NAME,
                        TrackedEntityDataValueColumns.COLUMN_ID, selection, selectionArgs);
            }
            case USERS: {
                return delete(UserColumns.TABLE_NAME, selection, selectionArgs);
            }
            case USER_ID: {
                return deleteId(uri, UserColumns.TABLE_NAME,
                        UserColumns.COLUMN_ID, selection, selectionArgs);
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
                return update(EventColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case EVENT_ID: {
                return updateId(uri, EventColumns.TABLE_NAME,
                        EventColumns.COLUMN_ID, selection, selectionArgs, values);
            }
            case PROGRAMS: {
                return update(ProgramColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case PROGRAM_ID: {
                return updateId(uri, ProgramColumns.TABLE_NAME,
                        ProgramColumns.COLUMN_ID, selection, selectionArgs, values);
            }
            case OPTIONSETS: {
                return update(OptionSetColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case OPTIONSET_ID: {
                return updateId(uri, OptionSetColumns.TABLE_NAME,
                        OptionSetColumns.COLUMN_ID, selection, selectionArgs, values);
            }
            case ORGANISATION_UNITS: {
                return update(OrganisationUnitColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case ORGANISATION_UNIT_ID: {
                return updateId(uri, OrganisationUnitColumns.TABLE_NAME,
                        OrganisationUnitColumns.COLUMN_ID, selection, selectionArgs, values);
            }
            case TRACKED_ENTITIES: {
                return update(TrackedEntityColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case TRACKED_ENTITY_ID: {
                return updateId(uri, TrackedEntityColumns.TABLE_NAME,
                        TrackedEntityColumns.COLUMN_ID, selection, selectionArgs, values);
            }
            case TRACKED_ENTITY_DATA_VALUES: {
                return update(TrackedEntityDataValueColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case TRACKED_ENTITY_DATA_VALUE_ID: {
                return updateId(uri, TrackedEntityDataValueColumns.TABLE_NAME,
                        TrackedEntityDataValueColumns.COLUMN_ID, selection, selectionArgs, values);
            }
            case USERS: {
                return update(UserColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case USER_ID: {
                return updateId(uri, UserColumns.TABLE_NAME,
                        UserColumns.COLUMN_ID, selection, selectionArgs, values);
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