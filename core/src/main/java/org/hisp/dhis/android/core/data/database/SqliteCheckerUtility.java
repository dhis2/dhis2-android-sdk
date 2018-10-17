package org.hisp.dhis.android.core.data.database;

import android.database.Cursor;

final public class SqliteCheckerUtility {

    private SqliteCheckerUtility() {
    }

    public static boolean isTableEmpty(DatabaseAdapter databaseAdapter, String table) {
        boolean isTableEmpty = true;
        Cursor cursor = databaseAdapter.query(" SELECT * FROM "+table);
        int value = cursor.getCount();
        if (value > 0) {
            isTableEmpty = false;
        }
        cursor.close();
        return isTableEmpty;
    }

    public static boolean isDatabaseEmpty(DatabaseAdapter databaseAdapter) {
        boolean isDatabaseEmpty = true;
        Cursor cursor = databaseAdapter.query(" SELECT name FROM sqlite_master WHERE "
                + "type='table' and name!='android_metadata' and name!='sqlite_sequence'");
        int value = cursor.getColumnIndex("name");
        if (value != -1) {
            while (cursor.moveToNext()){
                String tableName = cursor.getString(value);
                Cursor resTable = databaseAdapter.query(
                        "SELECT * from " + tableName , null);
                if (resTable.getCount() > 0) {
                    isDatabaseEmpty = false;
                    break;
                }
            }
        }
        cursor.close();
        return isDatabaseEmpty;
    }

    public static Boolean ifValueExist(String tableName, String fieldName, String fieldValue,
            DatabaseAdapter db) {
        boolean isExist = false;
        Cursor cursor = db.query(
                "SELECT " + fieldName + " from " + tableName + " where " + fieldName + " = '"
                        + fieldValue + "'", null);
        int value = cursor.getCount();
        if (value == 1) {
            isExist = true;
        }
        cursor.close();
        return isExist;
    }

    public static boolean isFieldExist(String tableName, String fieldName,  DatabaseAdapter db) {
        boolean isExist = false;
        Cursor cursor = db.query("PRAGMA table_info(" + tableName + ")", null);
        int value = cursor.getColumnIndex("name");
        if (value != -1) {
            while (cursor.moveToNext()) {
                if (cursor.getString(value).equals(fieldName)) {
                    isExist = true;
                    break;
                }
            }
        }
        cursor.close();
        return isExist;
    }

    public static boolean ifTableExist(String table, DatabaseAdapter db) {
        boolean isExist = false;
        Cursor cursor = db.query("PRAGMA table_info(" + table + ")");
        int itemsCount = cursor.getCount();
        if (itemsCount > 0) {
            isExist = true;
        }
        cursor.close();
        return isExist;
    }
}