package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class UserOrganisationUnitLinkStoreImpl implements UserOrganisationUnitLinkStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + DbOpenHelper.Tables.USER_ORGANISATION_UNIT + " (" +
            UserOrganisationUnitLinkContract.Columns.USER + ", " +
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT + ", " +
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT_SCOPE + ") " +
            "VALUES (?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public static ContentValues create(long id, String user, String organisationUnit, String orgUnitScope) {
        ContentValues userOrganisationUnitLink = new ContentValues();
        userOrganisationUnitLink.put(UserOrganisationUnitLinkContract.Columns.ID, id);
        userOrganisationUnitLink.put(UserOrganisationUnitLinkContract.Columns.USER, user);
        userOrganisationUnitLink.put(UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT, organisationUnit);
        userOrganisationUnitLink.put(UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT_SCOPE, orgUnitScope);
        return userOrganisationUnitLink;
    }

    public UserOrganisationUnitLinkStoreImpl(SQLiteDatabase sqLiteDatabase) {
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
    public void close() {
        sqLiteStatement.close();
    }
}
