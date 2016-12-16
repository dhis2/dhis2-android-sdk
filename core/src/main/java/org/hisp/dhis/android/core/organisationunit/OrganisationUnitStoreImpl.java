package org.hisp.dhis.android.core.organisationunit;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract.Columns;
import org.hisp.dhis.android.models.common.BaseIdentifiableObject;

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
            @NonNull String shortName,
            @NonNull String displayShortName,
            @NonNull String description,
            @NonNull String displayDescription,
            @NonNull String path,
            @NonNull Date openingDate,
            @NonNull Date closedDate,
            @Nullable String parent,
            int level) {
        sqLiteStatement.clearBindings();

        sqLiteStatement.bindString(1, uid);
        sqLiteStatement.bindString(2, code);
        sqLiteStatement.bindString(3, name);
        sqLiteStatement.bindString(4, displayName);
        sqLiteStatement.bindString(5, BaseIdentifiableObject.DATE_FORMAT.format(created));
        sqLiteStatement.bindString(6, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));
        sqLiteStatement.bindString(7, shortName);
        sqLiteStatement.bindString(8, displayShortName);
        sqLiteStatement.bindString(9, description);
        sqLiteStatement.bindString(10, displayDescription);
        sqLiteStatement.bindString(11, path);
        sqLiteStatement.bindString(12, BaseIdentifiableObject.DATE_FORMAT.format(openingDate));
        sqLiteStatement.bindString(13, BaseIdentifiableObject.DATE_FORMAT.format(closedDate));
        sqLiteStatement.bindLong(14, level);

        if (parent == null) {
            sqLiteStatement.bindNull(15);
        } else {
            sqLiteStatement.bindString(15, parent);
        }

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
