package org.hisp.dhis.android.core.trackedentity;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class TrackedEntityAttributeValueStoreImpl implements TrackedEntityAttributeValueStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " +
            DbOpenHelper.Tables.TRACKED_ENTITY_ATTRIBUTE_VALUE + " (" +
            TrackedEntityAttributeValueModel.Columns.STATE + ", " +
            TrackedEntityAttributeValueModel.Columns.ATTRIBUTE + ", " +
            TrackedEntityAttributeValueModel.Columns.VALUE + ") " +
            "VALUES (?, ?, ?)";

    private final SQLiteStatement insertRowStatement;

    public TrackedEntityAttributeValueStoreImpl(SQLiteDatabase database) {
        this.insertRowStatement = database.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull State state, @NonNull String attribute, @Nullable String value) {

        insertRowStatement.clearBindings();

        sqLiteBind(insertRowStatement, 1, state);
        sqLiteBind(insertRowStatement, 2, attribute);
        sqLiteBind(insertRowStatement, 3, value);

        return insertRowStatement.executeInsert();
    }

    @Override
    public void close() {
        insertRowStatement.close();
    }
}
