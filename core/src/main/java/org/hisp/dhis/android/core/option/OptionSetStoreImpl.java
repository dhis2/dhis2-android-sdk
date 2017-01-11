package org.hisp.dhis.android.core.option;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class OptionSetStoreImpl implements OptionSetStore {

    public static final String INSERT_STATEMENT = "INSERT INTO " + Tables.OPTION_SET + " (" +
            OptionSetModel.Columns.UID + ", " +
            OptionSetModel.Columns.CODE + ", " +
            OptionSetModel.Columns.NAME + ", " +
            OptionSetModel.Columns.DISPLAY_NAME + ", " +
            OptionSetModel.Columns.CREATED + ", " +
            OptionSetModel.Columns.LAST_UPDATED + ", " +
            OptionSetModel.Columns.VERSION + ", " +
            OptionSetModel.Columns.VALUE_TYPE + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public OptionSetStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid,
                       @Nullable String code,
                       @NonNull String name,
                       @NonNull String displayName,
                       @NonNull Date created,
                       @NonNull Date lastUpdated,
                       @NonNull Integer version,
                       @NonNull ValueType valueType) {
        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, version);
        sqLiteBind(sqLiteStatement, 8, valueType.name());

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
