package org.hisp.dhis.android.core.user;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class UserOrganisationUnitLinkStoreImpl implements UserOrganisationUnitLinkStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + DbOpenHelper.Tables.USER_ORGANISATION_UNIT + " (" +
            UserOrganisationUnitLinkModel.Columns.USER + ", " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT + ", " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE + ") " +
            "VALUES (?, ?, ?);";

    private final SQLiteDatabase sqLiteDatabase;
    private final SQLiteStatement sqLiteStatement;

    public UserOrganisationUnitLinkStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String user, @NonNull String organisationUnit,
            @NonNull String organisationUnitScope) {

        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, user);
        sqLiteBind(sqLiteStatement, 2, organisationUnit);
        sqLiteBind(sqLiteStatement, 3, organisationUnitScope);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public int delete() {
        return sqLiteDatabase.delete(DbOpenHelper.Tables.USER_ORGANISATION_UNIT, null, null);
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
