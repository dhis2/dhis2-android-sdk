package org.hisp.dhis.android.core.database;

import android.content.ContentResolver;

import java.util.Locale;

public final class DbUtils {
    private DbUtils() {
        // no instances
    }

    public static String directoryType(String mimeType) {
        return String.format(Locale.US, "%s/vnd.%s.%s", ContentResolver.CURSOR_DIR_BASE_TYPE,
                DbContract.AUTHORITY, mimeType);
    }

    public static String itemType(String mimeType) {
        return String.format(Locale.US, "%s/vnd.%s.%s", ContentResolver.CURSOR_ITEM_BASE_TYPE,
                DbContract.AUTHORITY, mimeType);
    }
}
