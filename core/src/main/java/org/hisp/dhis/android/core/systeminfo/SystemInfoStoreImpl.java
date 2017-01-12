package org.hisp.dhis.android.core.systeminfo;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModel.Columns;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class SystemInfoStoreImpl implements SystemInfoStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.SYSTEM_INFO + " (" +
            Columns.SERVER_DATE + ", " +
            Columns.DATE_FORMAT + ") " +
            "VALUES (?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public SystemInfoStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull Date serverDate, @NonNull String dateFormat) {
        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, serverDate);
        sqLiteBind(sqLiteStatement, 2, dateFormat);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
