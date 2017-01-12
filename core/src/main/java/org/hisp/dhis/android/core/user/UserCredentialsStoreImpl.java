package org.hisp.dhis.android.core.user;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class UserCredentialsStoreImpl implements UserCredentialsStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.USER_CREDENTIALS + " (" +
            UserCredentialsModel.Columns.UID + ", " +
            UserCredentialsModel.Columns.CODE + ", " +
            UserCredentialsModel.Columns.NAME + ", " +
            UserCredentialsModel.Columns.DISPLAY_NAME + ", " +
            UserCredentialsModel.Columns.CREATED + ", " +
            UserCredentialsModel.Columns.LAST_UPDATED + ", " +
            UserCredentialsModel.Columns.USERNAME + ", " +
            UserCredentialsModel.Columns.USER + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private final SQLiteDatabase sqLiteDatabase;
    private final SQLiteStatement insertStatement;

    public UserCredentialsStoreImpl(SQLiteDatabase database) {
        this.sqLiteDatabase = database;
        this.insertStatement = database.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(
            @NonNull String uid, @Nullable String code, @Nullable String name,
            @Nullable String displayName, @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String username, @NonNull String user) {
        insertStatement.clearBindings();

        sqLiteBind(insertStatement, 1, uid);
        sqLiteBind(insertStatement, 2, code);
        sqLiteBind(insertStatement, 3, name);
        sqLiteBind(insertStatement, 4, displayName);
        sqLiteBind(insertStatement, 5, created);
        sqLiteBind(insertStatement, 6, lastUpdated);
        sqLiteBind(insertStatement, 7, username);
        sqLiteBind(insertStatement, 8, user);

        return insertStatement.executeInsert();
    }

    @Override
    public int delete() {
        return sqLiteDatabase.delete(Tables.USER_CREDENTIALS, null, null);
    }

    @Override
    public void close() {
        insertStatement.close();
    }
}
