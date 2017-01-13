package org.hisp.dhis.android.core.constant;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;

// TODO: Tests
@AutoValue
public abstract class ConstantModel extends BaseIdentifiableObjectModel {

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String VALUE = "value";
    }

    public static ConstantModel create(Cursor cursor) {
        return AutoValue_ConstantModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ConstantModel.Builder();
    }


    @Nullable
    @ColumnName(Columns.VALUE)
    public abstract String value();

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder value(@Nullable String value);

        public abstract ConstantModel build();
    }
}