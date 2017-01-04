package org.hisp.dhis.android.core.dataelement;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.dataelement.DataElementContract.Columns;

import java.util.Date;

public class DataElementStoreImpl implements DataElementStore {

    public static final String INSERT_STATEMENT = "INSERT INTO " + Tables.DATA_ELEMENT + " (" +
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
            Columns.VALUE_TYPE + ", " +
            Columns.ZERO_IS_SIGNIFICANT + ", " +
            Columns.AGGREGATION_OPERATOR + ", " +
            Columns.FORM_NAME + ", " +
            Columns.NUMBER_TYPE + ", " +
            Columns.DOMAIN_TYPE + ", " +
            Columns.DIMENSION + ", " +
            Columns.DISPLAY_FORM_NAME + ", " +
            Columns.OPTION_SET + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public DataElementStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
                       @NonNull String displayName, @NonNull Date created,
                       @NonNull Date lastUpdated, @Nullable String shortName,
                       @Nullable String displayShortName, @Nullable String description,
                       @Nullable String displayDescription, @NonNull ValueType valueType,
                       @Nullable Boolean zeroIsSignificant, @Nullable String aggregationOperator,
                       @Nullable String formName, @Nullable String numberType,
                       @Nullable String domainType, @Nullable String dimension,
                       @Nullable String displayFormName, @Nullable String optionSet) {
        sqLiteStatement.clearBindings();

        sqLiteStatement.bindString(1, uid);
        if (code == null) {
            sqLiteStatement.bindNull(2);

        } else {
            sqLiteStatement.bindString(2, code);
        }

        sqLiteStatement.bindString(3, name);
        sqLiteStatement.bindString(4, displayName);
        sqLiteStatement.bindString(5, BaseIdentifiableObject.DATE_FORMAT.format(created));
        sqLiteStatement.bindString(6, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));

        if (shortName == null) {
            sqLiteStatement.bindNull(7);
        } else {
            sqLiteStatement.bindString(7, shortName);
        }

        if (displayShortName == null) {
            sqLiteStatement.bindNull(8);
        } else {
            sqLiteStatement.bindString(8, displayShortName);
        }

        if (description == null) {
            sqLiteStatement.bindNull(9);
        } else {
            sqLiteStatement.bindString(9, description);
        }

        if (displayDescription == null) {
            sqLiteStatement.bindNull(10);
        } else {
            sqLiteStatement.bindString(10, displayDescription);
        }

        sqLiteStatement.bindString(11, valueType.name());

        if (zeroIsSignificant == null) {
            sqLiteStatement.bindNull(12);
        } else {
            sqLiteStatement.bindLong(12, zeroIsSignificant ? 1 : 0);
        }

        if (aggregationOperator == null) {
            sqLiteStatement.bindNull(13);
        } else {
            sqLiteStatement.bindString(13, aggregationOperator);
        }

        if (formName == null) {
            sqLiteStatement.bindNull(14);
        } else {
            sqLiteStatement.bindString(14, formName);
        }

        if (numberType == null) {
            sqLiteStatement.bindNull(15);
        } else {
            sqLiteStatement.bindString(15, numberType);
        }

        if (domainType == null) {
            sqLiteStatement.bindNull(16);
        } else {
            sqLiteStatement.bindString(16, domainType);
        }

        if (dimension == null) {
            sqLiteStatement.bindNull(17);
        } else {
            sqLiteStatement.bindString(17, dimension);
        }

        if (displayFormName == null) {
            sqLiteStatement.bindNull(18);
        } else {
            sqLiteStatement.bindString(18, displayFormName);
        }

        if (optionSet == null) {
            sqLiteStatement.bindNull(19);
        } else {
            sqLiteStatement.bindString(19, optionSet);
        }


        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
