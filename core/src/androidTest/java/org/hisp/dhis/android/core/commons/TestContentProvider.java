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

    // faking database behaviour
    private Map<Long, String> storage;
    private ContentResolver contentResolver;

    void init(ContentResolver contentResolver) {
        this.storage = new HashMap<>();
        this.contentResolver = contentResolver;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        storage.put(values.getAsLong(TestModel.ID), values.getAsString(TestModel.VALUE));
        contentResolver.notifyChange(uri, null);
        return Uri.parse(TABLE + "/" + values.getAsString(TestModel.ID));
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

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