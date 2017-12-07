package org.hisp.dhis.android.core.data.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqliteChecker {


    public static Boolean ifValueExist(String tableName, String fieldName, String fieldValue,
            SQLiteDatabase db) {
        boolean isExist = false;
        Cursor res = db.rawQuery(
                "SELECT " + fieldName + " from " + tableName + " where " + fieldName + " = '"
                        + fieldValue + "'", null);
        int value = res.getCount();
        if (value == 1) {
            isExist = true;
        }
        return isExist;
    }


    public static boolean isFieldExist(String tableName, String fieldName, SQLiteDatabase db) {
        boolean isExist = false;
        Cursor res = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        int value = res.getColumnIndex("name");
        if (value != -1) {
            while (res.moveToNext()) {
                if (res.getString(value).equals(fieldName)) {
                    return true;
                }
            }
        }
        return isExist;
    }

    public static boolean ifTableExist(String table, SQLiteDatabase db) {
        boolean isExist = false;
        Cursor res = db.rawQuery("PRAGMA table_info(" + table + ")", null);
        int value = res.getColumnIndex("name");
        if (value != -1) {
            isExist = true;
        }
        return isExist;
    }
}