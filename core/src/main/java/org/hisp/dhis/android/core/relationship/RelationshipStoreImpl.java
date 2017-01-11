package org.hisp.dhis.android.core.relationship;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class RelationshipStoreImpl implements RelationshipStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " +
            DbOpenHelper.Tables.RELATIONSHIP_TABLE + " (" +
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_A + ", " +
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_B + ", " +
            RelationshipModel.Columns.RELATIONSHIP_TYPE + ") " +
            "VALUES(?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    RelationshipStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(
            @Nullable String trackedEntityInstanceA,
            @Nullable String trackedEntityInstanceB,
            @NonNull String relationshipType
    ) {

        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, trackedEntityInstanceA);
        sqLiteBind(sqLiteStatement, 2, trackedEntityInstanceB);
        sqLiteBind(sqLiteStatement, 3, relationshipType);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
