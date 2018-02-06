package org.hisp.dhis.android.core.common;

import static org.hisp.dhis.android.core.utils.StoreUtils.parse;

import android.database.Cursor;
import android.support.annotation.Nullable;

import java.util.Date;

public class Store {
    @Nullable
    protected String getStringFromCursor(Cursor cursor, int index) {
        return cursor.getString(index) == null ? null : cursor.getString(index);
    }

    @Nullable
    protected Date getDateFromCursor(Cursor cursor, int index) {
        return cursor.getString(index) == null ? null : parse(cursor.getString(index));
    }

    @Nullable
    protected Boolean getBooleanFromCursor(Cursor cursor, int index) {
        return cursor.getString(index) == null ? null : cursor.getInt(index) > 0;
    }

    @Nullable
    protected Integer getIntegerFromCursor(Cursor cursor, int index) {
        return cursor.getString(index) == null ? null : cursor.getInt(index);
    }

    @Nullable
    protected ValueType getValueTypeFromCursor(Cursor cursor, int index) {
        return cursor.getString(index) == null ? null :
                ValueType.valueOf(cursor.getString(index));
    }
}
