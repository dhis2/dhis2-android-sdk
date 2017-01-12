package org.hisp.dhis.android.core.relationship;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class RelationshipTypeStoreImpl implements RelationshipTypeStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " +
            DbOpenHelper.Tables.RELATIONSHIP_TYPE + " (" +
            RelationshipTypeModel.Columns.UID + ", " +
            RelationshipTypeModel.Columns.CODE + ", " +
            RelationshipTypeModel.Columns.NAME + ", " +
            RelationshipTypeModel.Columns.DISPLAY_NAME + ", " +
            RelationshipTypeModel.Columns.CREATED + ", " +
            RelationshipTypeModel.Columns.LAST_UPDATED + ", " +
            RelationshipTypeModel.Columns.A_IS_TO_B + ", " +
            RelationshipTypeModel.Columns.B_IS_TO_A + ") " +
            "VALUES (" + "?, ?, ?, ?, ?, ?, ?, ?" + ");";

    private final SQLiteStatement sqLiteStatement;

    RelationshipTypeStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(
            @NonNull String uid,
            @Nullable String code,
            @NonNull String name,
            @Nullable String displayName,
            @Nullable Date created,
            @Nullable Date lastUpdated,
            @NonNull String aIsToB,
            @NonNull String bIsToA
    ) {
        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, aIsToB);
        sqLiteBind(sqLiteStatement, 8, bIsToA);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
