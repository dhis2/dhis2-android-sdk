package org.hisp.dhis.android.core.configuration;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;

public class ConfigurationStoreImpl implements ConfigurationStore {
    private static final long CONFIGURATION_ID = 1L;
    private static final String[] PROJECTION = {
            ConfigurationModel.Columns.ID,
            ConfigurationModel.Columns.SERVER_URL
    };

    private final SQLiteDatabase sqLiteDatabase;

    public ConfigurationStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @Override
    public long save(@NonNull String serverUrl) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationModel.Columns.SERVER_URL, serverUrl);

        int updatedRows = update(contentValues);
        if (updatedRows <= 0) {
            insert(contentValues);
        }

        return 1;
    }

    @Nullable
    @Override
    public ConfigurationModel query() {
        Cursor queryCursor = sqLiteDatabase.query(DbOpenHelper.Tables.CONFIGURATION,
                PROJECTION, ConfigurationModel.Columns.ID + " = ?", new String[]{
                        String.valueOf(CONFIGURATION_ID)
                }, null, null, null);

        ConfigurationModel configuration = null;

        try {
            if (queryCursor != null && queryCursor.getCount() > 0) {
                queryCursor.moveToFirst();

                configuration = ConfigurationModel.create(queryCursor);
            }
        } finally {
            if (queryCursor != null) {
                queryCursor.close();
            }
        }

        return configuration;
    }

    @Override
    public int delete() {
        return sqLiteDatabase.delete(DbOpenHelper.Tables.CONFIGURATION, null, null);
    }

    private int update(@NonNull ContentValues contentValues) {
        return sqLiteDatabase.update(DbOpenHelper.Tables.CONFIGURATION, contentValues,
                ConfigurationModel.Columns.ID + " = ?", new String[]{
                        String.valueOf(CONFIGURATION_ID)
                });
    }

    private long insert(@NonNull ContentValues contentValues) {
        return sqLiteDatabase.insertWithOnConflict(DbOpenHelper.Tables.CONFIGURATION, null,
                contentValues, SQLiteDatabase.CONFLICT_FAIL);
    }
}
