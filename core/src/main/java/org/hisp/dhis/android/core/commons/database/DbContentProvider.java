package org.hisp.dhis.android.core.commons.database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract;
import org.hisp.dhis.android.core.user.UserContract;
import org.hisp.dhis.android.core.user.UserOrganisationUnitContract;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentUris.parseId;
import static android.content.ContentUris.withAppendedId;
import static android.text.TextUtils.isEmpty;

public final class DbContentProvider extends ContentProvider {
    private static final int USERS = 10;
    private static final int USERS_ID = 11;
    private static final int USERS_ID_ORGANISATION_UNITS = 12;

    private static final int ORGANISATION_UNITS = 20;
    private static final int ORGANISATION_UNITS_ID = 21;

    private UriMatcher uriMatcher;

    // android persistence apis
    private SQLiteOpenHelper databaseOpenHelper;
    private ContentResolver contentResolver;

    @Override
    public boolean onCreate() {
        if (getContext() == null) {
            throw new IllegalStateException();
        }

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        databaseOpenHelper = new DbHelper(getContext());
        contentResolver = getContext().getContentResolver();

        // build uriMatcher

        // users resource
        uriMatcher.addURI(DbContract.AUTHORITY, UserContract.USERS, USERS);
        uriMatcher.addURI(DbContract.AUTHORITY, UserContract.USERS_ID, USERS_ID);
        uriMatcher.addURI(DbContract.AUTHORITY, UserContract.USERS_ID_ORGANISATION_UNITS,
                USERS_ID_ORGANISATION_UNITS);

        // organisation units resource
        uriMatcher.addURI(DbContract.AUTHORITY, OrganisationUnitContract.ORGANISATION_UNITS, ORGANISATION_UNITS);
        uriMatcher.addURI(DbContract.AUTHORITY, OrganisationUnitContract.ORGANISATION_UNITS_ID, ORGANISATION_UNITS_ID);
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case USERS: {
                return UserContract.CONTENT_TYPE_DIR;
            }
            case USERS_ID: {
                return UserContract.CONTENT_TYPE_ITEM;
            }
            case USERS_ID_ORGANISATION_UNITS: {
                return OrganisationUnitContract.CONTENT_TYPE_DIR;
            }
            case ORGANISATION_UNITS: {
                return OrganisationUnitContract.CONTENT_TYPE_DIR;
            }
            case ORGANISATION_UNITS_ID: {
                return OrganisationUnitContract.CONTENT_TYPE_ITEM;
            }
            default: {
                throw new IllegalArgumentException("unknown URI: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case USERS: {
                cursor = query(UserContract.USER, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }
            case USERS_ID: {
                cursor = queryById(parseId(uri), UserContract.USER, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }
            case USERS_ID_ORGANISATION_UNITS: {
                cursor = queryByUid(parseUid(uri), UserOrganisationUnitContract.ORGANISATION_UNIT_JOIN,
                        projection, selection, selectionArgs, sortOrder);
                break;
            }
            case ORGANISATION_UNITS: {
                cursor = query(OrganisationUnitContract.ORGANISATION_UNIT, projection, selection,
                        selectionArgs, sortOrder);
                break;
            }
            case ORGANISATION_UNITS_ID: {
                cursor = queryById(parseId(uri), OrganisationUnitContract.ORGANISATION_UNIT,
                        projection, selection, selectionArgs, sortOrder);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown URI: " + uri);
            }
        }

        cursor.setNotificationUri(contentResolver, uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long itemId;
        switch (uriMatcher.match(uri)) {
            case USERS: {
                itemId = insert(UserContract.USER, values);
                break;
            }
            case ORGANISATION_UNITS: {
                itemId = insert(OrganisationUnitContract.ORGANISATION_UNIT, values);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported uri: " + uri);
            }
        }

        // notify listeners of URI about new row
        contentResolver.notifyChange(uri, null);

        // append id of newly inserted row
        return withAppendedId(uri, itemId);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] args) {
        int updated;
        switch (uriMatcher.match(uri)) {
            case USERS: {
                updated = update(UserContract.USER, values, selection, args);
                break;
            }
            case USERS_ID: {
                updated = update(parseId(uri), UserContract.USER, values, selection, args);
                break;
            }
            case ORGANISATION_UNITS: {
                updated = update(OrganisationUnitContract.ORGANISATION_UNIT,
                        values, selection, args);
                break;
            }
            case ORGANISATION_UNITS_ID: {
                updated = update(parseId(uri), OrganisationUnitContract.ORGANISATION_UNIT,
                        values, selection, args);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported uri: " + uri);
            }
        }

        // notify listeners of URI about updates in the table
        contentResolver.notifyChange(uri, null);

        return updated;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int deleted;
        switch (uriMatcher.match(uri)) {
            case USERS: {
                deleted = delete(UserContract.USER, selection, selectionArgs);
                break;
            }
            case USERS_ID: {
                deleted = delete(parseId(uri), UserContract.USER, selection, selectionArgs);
                break;
            }
            case ORGANISATION_UNITS: {
                deleted = delete(OrganisationUnitContract.ORGANISATION_UNIT,
                        selection, selectionArgs);
                break;
            }
            case ORGANISATION_UNITS_ID: {
                deleted = delete(parseId(uri), OrganisationUnitContract.ORGANISATION_UNIT,
                        selection, selectionArgs);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported uri: " + uri);
            }
        }

        // notify listeners of URI about deletes in the table
        contentResolver.notifyChange(uri, null);

        return deleted;
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(
            @NonNull ArrayList<ContentProviderOperation> ops) throws OperationApplicationException {
        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();

        try {
            database.beginTransaction();

            ContentProviderResult[] results = new ContentProviderResult[ops.size()];
            for (int index = 0; index < ops.size(); index++) {
                results[index] = ops.get(index).apply(this, results, index);
            }

            database.setTransactionSuccessful();
            return results;
        } finally {
            database.endTransaction();
        }
    }

    @NonNull
    private Cursor query(@NonNull String table, String[] projection, String selection,
            String selectionArgs[], String sortOrder) {
        SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getReadableDatabase();
        return sqLiteDatabase.query(table, projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    @NonNull
    private Cursor queryById(long id, @NonNull String table, String[] projection, String selection,
            String selectionArgs[], String sortOrder) {
        SQLiteQueryBuilder query = new SQLiteQueryBuilder();
        query.setTables(table);
        query.appendWhere(String.format(Locale.US, "%s = %d", BaseColumns._ID, id));

        SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getReadableDatabase();
        return query.query(sqLiteDatabase, projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    @NonNull
    private Cursor queryByUid(String uid, @NonNull String table, String[] projection, String selection,
            String selectionArgs[], String sortOrder) {
        SQLiteQueryBuilder query = new SQLiteQueryBuilder();
        query.setTables(table);
        query.appendWhere(String.format(Locale.US, "%s = %s",
                BaseIdentifiableObjectContract.Columns.UID, uid));

        SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getReadableDatabase();
        return query.query(sqLiteDatabase, projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    private long insert(@NonNull String table, ContentValues values) {
        SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getWritableDatabase();
        return sqLiteDatabase.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private int update(@NonNull String table, ContentValues values,
            String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getWritableDatabase();
        return sqLiteDatabase.update(table, values, selection, selectionArgs);
    }

    private int update(long id, @NonNull String table, ContentValues values,
            String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getWritableDatabase();
        String where = String.format(Locale.US, "%s = %d", BaseColumns._ID, id);
        if (!isEmpty(selection)) {
            where = where + " AND " + selection;
        }
        return sqLiteDatabase.update(table, values, where, selectionArgs);
    }

    private int delete(@NonNull String table, String selection, String[] selectionArgs) {
        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();
        return database.delete(table, selection, selectionArgs);
    }

    private int delete(long id, @NonNull String table, String selection, String[] selectionArgs) {
        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();
        String where = String.format(Locale.US, "%s = %d", BaseColumns._ID, id);
        if (!isEmpty(selection)) {
            where = where + " AND " + selection;
        }
        return database.delete(table, where, selectionArgs);
    }

    @NonNull
    private static String parseUid(Uri uri) {
        if (uri.getPathSegments().size() > 1) {
            return uri.getPathSegments().get(1);
        }

        throw new IllegalArgumentException("Invalid uri format: " + uri);
    }
}
