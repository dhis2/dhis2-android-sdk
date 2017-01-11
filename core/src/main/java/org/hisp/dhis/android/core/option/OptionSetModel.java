package org.hisp.dhis.android.core.option;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DbValueTypeColumnAdapter;


@AutoValue
public abstract class OptionSetModel extends BaseIdentifiableObjectModel {

    public interface Columns extends BaseIdentifiableObjectModel.Columns {
        String VERSION = "version";
        String VALUE_TYPE = "valueType";
    }

    public static OptionSetModel create(Cursor cursor) {
        return AutoValue_OptionSetModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_OptionSetModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.VERSION)
    public abstract Integer version();

    @Nullable
    @ColumnName(Columns.VALUE_TYPE)
    @ColumnAdapter(DbValueTypeColumnAdapter.class)
    public abstract ValueType valueType();

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder version(@Nullable Integer version);

        public abstract Builder valueType(@Nullable ValueType valueType);

        public abstract OptionSetModel build();
    }
}
