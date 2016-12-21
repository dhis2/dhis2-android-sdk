package org.hisp.dhis.android.core.organisationunit;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract.Columns;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;

import java.util.Date;

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

        sqLiteStatement.bindString(1, uid);
        sqLiteStatement.bindString(2, code);
        sqLiteStatement.bindString(3, name);
        sqLiteStatement.bindString(4, displayName);
        sqLiteStatement.bindString(5, BaseIdentifiableObject.DATE_FORMAT.format(created));
        sqLiteStatement.bindString(6, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));

        if (shortName != null) {
            sqLiteStatement.bindString(7, shortName);
        } else {
            sqLiteStatement.bindNull(7);
        }

        if (displayShortName != null) {
            sqLiteStatement.bindString(8, displayShortName);
        } else {
            sqLiteStatement.bindNull(8);
        }

        if (description != null) {
            sqLiteStatement.bindString(9, description);
        } else {
            sqLiteStatement.bindNull(9);
        }

        if (displayDescription != null) {
            sqLiteStatement.bindString(10, displayDescription);
        } else {
            sqLiteStatement.bindNull(10);
        }

        if (path != null) {
            sqLiteStatement.bindString(11, path);
        } else {
            sqLiteStatement.bindNull(11);
        }

        if (openingDate != null) {
            sqLiteStatement.bindString(12, BaseIdentifiableObject.DATE_FORMAT.format(openingDate));
        } else {
            sqLiteStatement.bindNull(12);
        }

        if (closedDate != null) {
            sqLiteStatement.bindString(13, BaseIdentifiableObject.DATE_FORMAT.format(closedDate));
        } else {
            sqLiteStatement.bindNull(13);
        }

        if (level != null) {
            sqLiteStatement.bindLong(14, level);
        } else {
            sqLiteStatement.bindNull(14);
        }

        if (parent != null) {
            sqLiteStatement.bindString(15, parent);
        } else {
            sqLiteStatement.bindNull(15);
        }

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
