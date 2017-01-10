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

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

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

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, BaseIdentifiableObject.DATE_FORMAT.format(created));
        sqLiteBind(sqLiteStatement, 6, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));
        sqLiteBind(sqLiteStatement, 7, shortName);
        sqLiteBind(sqLiteStatement, 8, displayShortName);
        sqLiteBind(sqLiteStatement, 9, description);
        sqLiteBind(sqLiteStatement, 10, displayDescription);
        sqLiteBind(sqLiteStatement, 11, valueType.name());
        sqLiteBind(sqLiteStatement, 12, zeroIsSignificant);
        sqLiteBind(sqLiteStatement, 13, aggregationOperator);
        sqLiteBind(sqLiteStatement, 14, formName);
        sqLiteBind(sqLiteStatement, 15, numberType);
        sqLiteBind(sqLiteStatement, 16, domainType);
        sqLiteBind(sqLiteStatement, 17, dimension);
        sqLiteBind(sqLiteStatement, 18, displayFormName);
        sqLiteBind(sqLiteStatement, 19, optionSet);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
