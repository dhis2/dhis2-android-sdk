package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public interface DefaultStubProvider {

    /* Always return true, indicating that the
      * provider loaded correctly.*/
    boolean onCreate();

    /*Return no type for MIME type */
    String getType(Uri uri);

    /* query() always returns no results*/
    Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder);

    /* insert() always returns null (no URI) */
    Uri insert(Uri uri, ContentValues values);

    /* delete() always returns "no rows affected" (0)*/
    int delete(Uri uri, String selection, String[] selectionArgs);

    /* update() always returns "no rows affected" (0) */
    int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs);
}
