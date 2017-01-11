package org.hisp.dhis.android.core.organisationunit;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract.Columns;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class OrganisationUnitStoreImpl implements OrganisationUnitStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.ORGANISATION_UNIT + " (" +
            Columns.UID + ", " +
            Columns.CODE + ", " +
            Columns.NAME + ", " +
            Columns.DISPLAY_NAME + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.SHORT_NAME + ", " +
            Columns.DISPLAY_SHORT_NAME + ", " +
            Columns.DESCRIPTION + ", " +
            Columns.DISPLAY_DESCRIPTION + ", " +
            Columns.PATH + ", " +
            Columns.OPENING_DATE + ", " +
            Columns.CLOSED_DATE + ", " +
            Columns.LEVEL + ", " +
            Columns.PARENT + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public OrganisationUnitStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(
            @NonNull String uid,
            @NonNull String code,
            @NonNull String name,
            @NonNull String displayName,
            @NonNull Date created,
            @NonNull Date lastUpdated,
            @Nullable String shortName,
            @Nullable String displayShortName,
            @Nullable String description,
            @Nullable String displayDescription,
            @Nullable String path,
            @Nullable Date openingDate,
            @Nullable Date closedDate,
            @Nullable String parent,
            @Nullable Integer level) {
        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5,created);
        sqLiteBind(sqLiteStatement, 6,lastUpdated);
        sqLiteBind(sqLiteStatement, 7, shortName);
        sqLiteBind(sqLiteStatement, 8, displayShortName);
        sqLiteBind(sqLiteStatement, 9, description);
        sqLiteBind(sqLiteStatement, 10, displayDescription);
        sqLiteBind(sqLiteStatement, 11, path);
        sqLiteBind(sqLiteStatement, 12,openingDate);
        sqLiteBind(sqLiteStatement, 13,closedDate);
        sqLiteBind(sqLiteStatement, 14, level);
        sqLiteBind(sqLiteStatement, 15, parent);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
