package org.hisp.dhis.android.core.data.database;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import android.database.Cursor;

public final class DatabaseAssert {

    DatabaseAdapter databaseAdapter;

    public static DatabaseAssert assertThatDatabase(DatabaseAdapter databaseAdapter) {
        return new DatabaseAssert(databaseAdapter);
    }

    private DatabaseAssert(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    public DatabaseAssert ifValueExist(String tableName, String fieldName, String fieldValue) {
        boolean isExist = false;

        Cursor res = databaseAdapter.query(
                "SELECT " + fieldName + " from " + tableName + " where " + fieldName + " = '"
                        + fieldValue + "'", null);
        int value = res.getCount();
        if (value == 1) {
            isExist = true;
        }

        assertThat(isExist, is(true));

        return this;
    }

    public DatabaseAssert isFieldExist(String tableName, String fieldName) {
        boolean isExist = false;
        Cursor res = databaseAdapter.query("PRAGMA table_info(" + tableName + ")", null);
        int value = res.getColumnIndex("name");
        if (value != -1) {
            while (res.moveToNext()) {
                if (res.getString(value).equals(fieldName)) {
                    isExist = true;
                    break;
                }
            }
        }
        assertThat(isExist, is(true));

        return this;
    }

    public DatabaseAssert ifTableExist(String table) {
        boolean isExist = false;
        Cursor res = databaseAdapter.query("PRAGMA table_info(" + table + ")", null);
        int value = res.getColumnIndex("name");
        if (value != -1) {
            isExist = true;
        }
        assertThat(isExist, is(true));

        return this;
    }


    public DatabaseAssert isEmpty() {
        verifyEmptyDatabase(true);

        return this;
    }

    public DatabaseAssert isNotEmpty() {
        verifyEmptyDatabase(false);

        return this;
    }

    public DatabaseAssert isEmptyTable(String tableName) {
        assertThat(tableCount(tableName) == 0, is(true));

        return this;
    }

    public DatabaseAssert isNotEmptyTable(String tableName) {
        assertThat(tableCount(tableName) == 0, is(false));

        return this;
    }

    private void verifyEmptyDatabase(boolean expectedEmpty) {
        boolean isEmpty = true;

        Cursor res = databaseAdapter.query(" SELECT name FROM sqlite_master "
                + "WHERE type='table' and name!='android_metadata' and name!='sqlite_sequence'");
        int value = res.getColumnIndex("name");
        if (value != -1) {
            while (res.moveToNext()) {
                String tableName = res.getString(value);

                if (tableCount(tableName) > 0) {
                    isEmpty = false;
                }
            }
        }
        assertThat(isEmpty, is(expectedEmpty));
    }

    private int tableCount(String tableName) {
        Cursor cursor = null;
        int count = 0;

        try {
            cursor = databaseAdapter.query("SELECT * from " + tableName, null);
            count = cursor.getCount();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;
    }
}
