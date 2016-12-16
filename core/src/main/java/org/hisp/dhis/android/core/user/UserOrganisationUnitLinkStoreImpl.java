package org.hisp.dhis.android.core.user;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;

public class UserOrganisationUnitLinkStoreImpl implements UserOrganisationUnitLinkStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + DbOpenHelper.Tables.USER_ORGANISATION_UNIT + " (" +
            UserOrganisationUnitLinkContract.Columns.USER + ", " +
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT + ", " +
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT_SCOPE + ") " +
            "VALUES (?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public UserOrganisationUnitLinkStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String user, @NonNull String organisationUnit,
            @NonNull String organisationUnitScope) {
        sqLiteStatement.clearBindings();

        sqLiteStatement.bindString(1, user);
        sqLiteStatement.bindString(2, organisationUnit);
        sqLiteStatement.bindString(3, organisationUnitScope);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
