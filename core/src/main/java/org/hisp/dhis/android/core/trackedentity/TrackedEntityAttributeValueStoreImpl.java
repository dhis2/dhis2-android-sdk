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
            TrackedEntityAttributeValueModel.Columns.VALUE  + ", " +
            TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE + ", " +
            TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE + ") " +
            "VALUES (?, ?, ?, ?)";

    private final SQLiteStatement insertRowStatement;

    public TrackedEntityAttributeValueStoreImpl(SQLiteDatabase database) {
        this.insertRowStatement = database.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull State state,
                       @Nullable String value,
                       @NonNull String trackedEntityAttribute,
                       @NonNull String trackedEntityInstance) {

        insertRowStatement.clearBindings();

        sqLiteBind(insertRowStatement, 1, state);
        sqLiteBind(insertRowStatement, 2, value);
        sqLiteBind(insertRowStatement, 3, trackedEntityAttribute);
        sqLiteBind(insertRowStatement, 4, trackedEntityInstance);

        return insertRowStatement.executeInsert();
    }

    @Override
    public void close() {
        insertRowStatement.close();
    }
}
