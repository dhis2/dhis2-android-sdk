package org.hisp.dhis.android.core.user;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.user.UserCredentialsContract.Columns;
import org.hisp.dhis.android.models.common.BaseIdentifiableObject;

import java.util.Date;

public class UserCredentialsStoreImpl implements UserCredentialsStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.USER_CREDENTIALS + " (" +
            Columns.UID + ", " +
            Columns.CODE + ", " +
            Columns.NAME + ", " +
            Columns.DISPLAY_NAME + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.USERNAME + ", " +
            Columns.USER + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private final SQLiteStatement insertStatement;

    public UserCredentialsStoreImpl(SQLiteDatabase database) {
        this.insertStatement = database.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(
            @NonNull String uid, @NonNull String code, @NonNull String name,
            @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated,
            @NonNull String username, @NonNull String user) {
        insertStatement.clearBindings();

        insertStatement.bindString(1, uid);
        insertStatement.bindString(2, code);
        insertStatement.bindString(3, name);
        insertStatement.bindString(4, displayName);
        insertStatement.bindString(5, BaseIdentifiableObject.DATE_FORMAT.format(created));
        insertStatement.bindString(6, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));
        insertStatement.bindString(7, username);
        insertStatement.bindString(8, user);

        return insertStatement.executeInsert();
    }

    @Override
    public void close() {
        insertStatement.close();
    }
}
