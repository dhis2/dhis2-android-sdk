package org.hisp.dhis.android.core.common;

import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;

import org.hisp.dhis.android.core.data.database.DbStateColumnAdapter;

public abstract class BaseIdentifiableDataModel extends BaseDataModel {

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String STATE = "state";
        public static final String UID = "uid";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";

    }

    @Override
    @Nullable
    @ColumnName(BaseDataModel.Columns.STATE)
    @ColumnAdapter(DbStateColumnAdapter.class)
    public abstract State state();

    protected static abstract class Builder<T extends Builder> extends BaseIdentifiableObjectModel.Builder<T> {
        public abstract T state(State state);
    }
}
