package org.hisp.dhis.android.core.commons;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.test.mock.MockContentProvider;

import java.util.HashMap;
import java.util.Map;

public final class FakeContentProvider extends MockContentProvider {
    static final Uri AUTHORITY = Uri.parse("content://test_authority");
    static final Uri TABLE = AUTHORITY.buildUpon().appendPath("test_table").build();
    static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static final int TABLE_PATH = 10;
    static final int TABLE_PATH_ITEM = 11;

    static {
        URI_MATCHER.addURI(AUTHORITY.getAuthority(), "test_table/", TABLE_PATH);
        URI_MATCHER.addURI(AUTHORITY.getAuthority(), "test_table/#/", TABLE_PATH_ITEM);
    }

    // faking database behaviour
    private Map<Long, String> storage;
    private ContentResolver contentResolver;

    void init(ContentResolver contentResolver) {
        this.storage = new HashMap<>();
        this.contentResolver = contentResolver;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case TABLE_PATH: {
                Long id = values.getAsLong(TestModel.ID);

                if (!storage.containsKey(id)) {
                    throw new RuntimeException("Row does not exist: " + id);
                }

                storage.put(id, values.getAsString(TestModel.VALUE));
                contentResolver.notifyChange(uri, null);
                return 1;
            }
            case TABLE_PATH_ITEM: {
                Long id = ContentUris.parseId(uri);

                if (!storage.containsKey(id)) {
                    throw new RuntimeException("Row does not exist: " + id);
                }

                storage.put(id, values.getAsString(TestModel.VALUE));
                contentResolver.notifyChange(uri, null);
                return 1;
            }
        }

        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        storage.put(values.getAsLong(TestModel.ID), values.getAsString(TestModel.VALUE));
        contentResolver.notifyChange(uri, null);
        return Uri.parse(TABLE + "/" + values.getAsString(TestModel.ID));
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String where, String[] args, String order) {
        MatrixCursor result = new MatrixCursor(new String[]{
                TestModel.ID, TestModel.VALUE
        });

        for (Map.Entry<Long, String> entry : storage.entrySet()) {
            result.addRow(new String[]{
                    String.valueOf(entry.getKey()), entry.getValue()
            });
        }

        return result;
    }
}