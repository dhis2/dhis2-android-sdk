package org.hisp.dhis.android.core.data.database;

import android.database.Cursor;

final public class SqliteCheckerUtility {

    private SqliteCheckerUtility() {
    }

    public static boolean isTableEmpty(DatabaseAdapter databaseAdapter, String table) {
        boolean isTableEmpty = true;
        Cursor res = databaseAdapter.query(" SELECT * FROM "+table);
        int value = res.getCount();
        if (value > 0) {
            isTableEmpty = false;
        }
        return isTableEmpty;
    }

    public static boolean isDatabaseEmpty(DatabaseAdapter databaseAdapter) {
        boolean isDatabaseEmpty = true;
        Cursor res = databaseAdapter.query(" SELECT name FROM sqlite_master WHERE "
                + "type='table' and name!='android_metadata' and name!='sqlite_sequence'");
        int value = res.getColumnIndex("name");
        if (value != -1) {
            while (res.moveToNext()){
                String tableName = res.getString(value);
                Cursor resTable = databaseAdapter.query(
                        "SELECT * from " + tableName , null);
                if (resTable.getCount() > 0) {
                    isDatabaseEmpty = false;
                    break;
                }
            }
        }
        return isDatabaseEmpty;
    }
}
