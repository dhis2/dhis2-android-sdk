package org.hisp.dhis.android.sdk.persistence.migrations;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by thomaslindsjorn on 28/07/16.
 */
public class MigrationUtil {

    private static SQLiteDatabase database;

    public static boolean columnExists(@NotNull Class tableClass, @NotNull String columnName) {
        Cursor dbCursor = database.query( // empty query just to get the column names for the table
                FlowManager.getTableName(tableClass), null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(columnName)) {
                return true;
            }
        }
        return false;
    }

    public static void setDatabase(SQLiteDatabase database) {
        MigrationUtil.database = database;
    }
}
