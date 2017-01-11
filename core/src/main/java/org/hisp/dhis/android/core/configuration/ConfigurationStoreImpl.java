package org.hisp.dhis.android.core.configuration;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationStoreImpl implements ConfigurationStore {
    private static final String PROJECTION[] = {
            ConfigurationModel.Columns.ID,
            ConfigurationModel.Columns.SERVER_URL
    };

    private final SQLiteDatabase sqLiteDatabase;

    public ConfigurationStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @Override
    public long save(@NonNull ConfigurationModel configurationModel) {
        return sqLiteDatabase.insertWithOnConflict(DbOpenHelper.Tables.CONFIGURATION, null,
                configurationModel.toContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
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
}
