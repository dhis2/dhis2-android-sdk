package org.hisp.dhis.android.core.common;

import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;

import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;

import java.util.Date;

public abstract class BaseIdentifiableDataModel extends BaseDataModel {

    public static class Columns extends BaseDataModel.Columns {
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
    }

    @Nullable
    @ColumnName(Columns.CREATED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Nullable
    @ColumnName(Columns.LAST_UPDATED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    protected static abstract class Builder<T extends Builder> extends BaseDataModel.Builder<T> {
        public abstract T created(@Nullable Date created);

        public abstract T lastUpdated(@Nullable Date created);
    }
}
