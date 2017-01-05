package org.hisp.dhis.android.core.common;

import android.database.sqlite.SQLiteStatement;

/**
 * Provides helper functions to handle null checks and type conversions.
 */
public class StoreUtils {

    /**
     * Handle if String argument is null and bind it using .bindNull() if so.
     * A helper function to abstract/clean up boilerplate if/else bloat..
     *
     * @param sqLiteStatement
     * @param index
     * @param arg
     */
    public static void sqLiteBind(SQLiteStatement sqLiteStatement, int index, String arg) {
        if (arg == null) {
            sqLiteStatement.bindNull(index);
        } else {
            sqLiteStatement.bindString(index, arg);
        }
    }

    /**
     * Handle if Boolean argument is null and bind it using .bindNull() if so.
     * A helper function to abstract/clean up boilerplate if/else bloat...
     * Also convet the Boolean to Long...
     *
     * @param sqLiteStatement
     * @param index
     * @param arg
     */
    public static void sqLiteBind(SQLiteStatement sqLiteStatement, int index, Boolean arg) {
        if (arg == null) {
            sqLiteStatement.bindNull(index);
        } else {
            sqLiteStatement.bindLong(index, arg ? 1 : 0);
        }
    }

    /**
     * Handle if Integer argument is null and bind it using .bindNull() if so.
     * A helper function to abstract/clean up boilerplate if/else bloat..
     *
     * @param sqLiteStatement
     * @param index
     * @param arg
     */
    public static void sqLiteBind(SQLiteStatement sqLiteStatement, int index, Integer arg) {
        if (arg == null) {
            sqLiteStatement.bindNull(index);
        } else {
            sqLiteStatement.bindLong(index, arg);
        }
    }
}
