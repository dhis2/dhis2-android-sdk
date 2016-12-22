package org.hisp.dhis.android.core.option;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.option.OptionSetContract.Columns;
import org.hisp.dhis.android.models.common.BaseIdentifiableObject;

import java.util.Date;

public class OptionStoreImpl implements OptionStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.OPTION + " (" +
            Columns.UID + ", " +
            Columns.CODE + ", " +
            Columns.NAME + ", " +
            Columns.DISPLAY_NAME + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ") " +
            "VALUES (?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public OptionStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid,
                       @NonNull String code,
                       @NonNull String name,
                       @NonNull String displayName,
                       @NonNull Date created,
                       @NonNull Date lastUpdated) {
        sqLiteStatement.clearBindings();

        sqLiteStatement.bindString(1, uid);
        sqLiteStatement.bindString(2, code);
        sqLiteStatement.bindString(3, name);
        sqLiteStatement.bindString(4, displayName);
        sqLiteStatement.bindString(5, BaseIdentifiableObject.DATE_FORMAT.format(created));
        sqLiteStatement.bindString(6, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
