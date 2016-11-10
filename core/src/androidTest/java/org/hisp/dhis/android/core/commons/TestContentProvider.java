package org.hisp.dhis.android.core.commons;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.test.mock.MockContentProvider;

import java.util.HashMap;
import java.util.Map;

public final class TestContentProvider extends MockContentProvider {
    static final Uri AUTHORITY = Uri.parse("content://test_authority");
    static final Uri TABLE = AUTHORITY.buildUpon().appendPath("test_table").build();

    // projection
    static final String KEY = "test_key";
    static final String VALUE = "test_value";

    // faking database behaviour
    private Map<String, String> storage;
    private ContentResolver contentResolver;

    void init(ContentResolver contentResolver) {
        this.storage = new HashMap<>();
        this.contentResolver = contentResolver;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        storage.put(values.getAsString(KEY), values.getAsString(VALUE));
        contentResolver.notifyChange(uri, null);
        return Uri.parse(TABLE + "/" + values.getAsString(KEY));
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        MatrixCursor result = new MatrixCursor(new String[]{
                KEY, VALUE
        });

        for (Map.Entry<String, String> entry : storage.entrySet()) {
            result.addRow(new String[]{
                    entry.getKey(), entry.getValue()
            });
        }

        return result;
    }

    public static ContentValues values(String valueOne, String valueTwo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TestContentProvider.KEY, valueOne);
        contentValues.put(TestContentProvider.VALUE, valueTwo);
        return contentValues;
    }
}