package org.hisp.dhis.android.core.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class AuthenticatedUserStoreImpl implements AuthenticatedUserStore {
    private static final String[] PROJECTION = new String[]{
            AuthenticatedUserModel.Columns.ID,
            AuthenticatedUserModel.Columns.USER,
            AuthenticatedUserModel.Columns.CREDENTIALS
    };

    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.AUTHENTICATED_USER +
            " (" + AuthenticatedUserModel.Columns.USER + ", " + AuthenticatedUserModel.Columns.CREDENTIALS + ")" +
            " VALUES (?, ?);";

    private final SQLiteDatabase sqLiteDatabase;
    private final SQLiteStatement insertRowStatement;

    public AuthenticatedUserStoreImpl(@NonNull SQLiteDatabase database) {
        this.sqLiteDatabase = database;
        this.insertRowStatement = database.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String userUid, @NonNull String credentials) {
        insertRowStatement.clearBindings();
        sqLiteBind(insertRowStatement, 1, userUid);
        sqLiteBind(insertRowStatement, 2, credentials);
        return insertRowStatement.executeInsert();
    }

    @NonNull
    @Override
    public List<AuthenticatedUserModel> query() {
        List<AuthenticatedUserModel> rows = new ArrayList<>();

        Cursor queryCursor = sqLiteDatabase.query(Tables.AUTHENTICATED_USER,
                PROJECTION, null, null, null, null, null);

        if (queryCursor == null) {
            return rows;
        }

        try {
            if (queryCursor.getCount() > 0) {
                queryCursor.moveToFirst();

                do {
                    rows.add(AuthenticatedUserModel.create(queryCursor));
                } while (queryCursor.moveToNext());
            }
        } finally {
            queryCursor.close();
        }

        return rows;
    }

    @Override
    public int delete() {
        return sqLiteDatabase.delete(Tables.AUTHENTICATED_USER, null, null);
    }

    @Override
    public void close() {
        insertRowStatement.close();
    }
}
