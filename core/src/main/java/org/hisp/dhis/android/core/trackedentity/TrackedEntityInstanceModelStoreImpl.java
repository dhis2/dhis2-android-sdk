package org.hisp.dhis.android.core.trackedentity;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class TrackedEntityInstanceModelStoreImpl implements TrackedEntityInstanceModelStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + DbOpenHelper.Tables.TRACKED_ENTITY_INSTANCE + " (" +
            TrackedEntityInstanceModel.Columns.UID + ", " +
            TrackedEntityInstanceModel.Columns.CREATED + ", " +
            TrackedEntityInstanceModel.Columns.LAST_UPDATED + ", " +
            TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT + ", " +
            TrackedEntityInstanceModel.Columns.STATE +
            ") " + "VALUES (?, ?, ?, ?, ?)";

    private final SQLiteDatabase sqLiteDatabase;
    private final SQLiteStatement insertRowStatement;

    public TrackedEntityInstanceModelStoreImpl(SQLiteDatabase database) {
        this.sqLiteDatabase = database;
        this.insertRowStatement = database.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable Date created, @Nullable Date lastUpdated,
                       @Nullable String organisationUnit, @Nullable State state) {
        insertRowStatement.clearBindings();

        sqLiteBind(insertRowStatement, 1, uid);
        sqLiteBind(insertRowStatement, 2, created);
        sqLiteBind(insertRowStatement, 3, lastUpdated);
        sqLiteBind(insertRowStatement, 4, organisationUnit);
        sqLiteBind(insertRowStatement, 5, state);

        return insertRowStatement.executeInsert();
    }

    @Override
    public int delete() {
        return sqLiteDatabase.delete(DbOpenHelper.Tables.TRACKED_ENTITY_INSTANCE, null, null);
    }

    @Override
    public void close() {
        insertRowStatement.close();
    }
}
