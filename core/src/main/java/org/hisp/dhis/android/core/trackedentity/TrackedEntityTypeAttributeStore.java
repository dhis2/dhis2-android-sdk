package org.hisp.dhis.android.core.trackedentity;


import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

final class TrackedEntityTypeAttributeStore {

    private TrackedEntityTypeAttributeStore() {}

    private static final StatementBinder<TrackedEntityTypeAttribute> BINDER
            = new StatementBinder<TrackedEntityTypeAttribute>() {
        @Override
        public void bindToStatement(@NonNull TrackedEntityTypeAttribute o,
                                    @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.trackedEntityType().uid());
            sqLiteBind(sqLiteStatement, 2, o.trackedEntityAttribute().uid());
            sqLiteBind(sqLiteStatement, 3, o.displayInList());
            sqLiteBind(sqLiteStatement, 4, o.mandatory());
            sqLiteBind(sqLiteStatement, 5, o.searchable());
            sqLiteBind(sqLiteStatement, 6, o.sortOrder());
        }
    };

    private static final CursorModelFactory<TrackedEntityTypeAttribute> FACTORY
            = new CursorModelFactory<TrackedEntityTypeAttribute>() {
        @Override
        public TrackedEntityTypeAttribute fromCursor(Cursor cursor) {
            return TrackedEntityTypeAttribute.create(cursor);
        }
    };

    public static LinkModelStore<TrackedEntityTypeAttribute> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.linkModelStore(
                databaseAdapter,
                TrackedEntityTypeAttributeTableInfo.TABLE_INFO,
                TrackedEntityTypeAttributeFields.TRACKED_ENTITY_TYPE,
                BINDER,
                FACTORY);
    }
}