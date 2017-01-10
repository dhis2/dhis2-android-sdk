package org.hisp.dhis.android.core.configuration;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationStoreImpl implements ConfigurationStore {
    private static final String PROJECTION[] = {
            ConfigurationContract.Columns.ID,
            ConfigurationContract.Columns.SERVER_URL
    };

    private static final String INSERT_STATEMENT = "INSERT INTO " + DbOpenHelper.Tables.CONFIGURATION + " (" +
            ConfigurationContract.Columns.SERVER_URL +
            ") VALUES (?);";

    private final SQLiteDatabase sqLiteDatabase;
    private final SQLiteStatement insertStatement;

    public ConfigurationStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.insertStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String serverUrl) {
        insertStatement.clearBindings();
        insertStatement.bindString(1, serverUrl);

        return insertStatement.executeInsert();
    }

    @NonNull
    @Override
    public List<ConfigurationModel> query() {
        List<ConfigurationModel> rows = new ArrayList<>();

        Cursor queryCursor = sqLiteDatabase.query(DbOpenHelper.Tables.CONFIGURATION,
                PROJECTION, null, null, null, null, null);

        if (queryCursor == null) {
            return rows;
        }

        try {
            if (queryCursor.getCount() > 0) {
                queryCursor.moveToFirst();

                do {
                    rows.add(ConfigurationModel.create(queryCursor));
                } while (queryCursor.moveToNext());
            }
        } finally {
            queryCursor.close();
        }

        return rows;
    }

    @Override
    public void close() {
        insertStatement.close();
    }
}
