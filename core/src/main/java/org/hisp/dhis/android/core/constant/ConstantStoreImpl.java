package org.hisp.dhis.android.core.constant;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.constant.ConstantContract.Columns;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class ConstantStoreImpl implements ConstantStore {

    public static final String INSERT_STATEMENT = "INSERT INTO " + Tables.CONSTANT + " (" +
            Columns.UID + "," +
            Columns.CODE + "," +
            Columns.NAME + "," +
            Columns.DISPLAY_NAME + "," +
            Columns.CREATED + "," +
            Columns.LAST_UPDATED + "," +
            Columns.VALUE +
            ") VALUES (?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement insertStatement;

    public ConstantStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.insertStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    // TODO: Don't know if these should be @NonNull or @Nullable. They seem to differ in the other stores. For OptionSet they also differ between interface and implementation

    @Override
    public long insert(@Nullable String uid, @Nullable String code, @NonNull String name, @Nullable String displayName, @Nullable Date created, @Nullable Date lastUpdated, @NonNull Double value) {
        insertStatement.clearBindings();

        sqLiteBind(insertStatement, 1, uid);
        sqLiteBind(insertStatement, 2, code);
        sqLiteBind(insertStatement, 3, name);
        sqLiteBind(insertStatement, 4, displayName);
        sqLiteBind(insertStatement, 5, created);
        sqLiteBind(insertStatement, 6, lastUpdated);
        sqLiteBind(insertStatement, 7, value);

        return insertStatement.executeInsert();
    }

    @Override
    public void close() {
        insertStatement.close();
    }
}
