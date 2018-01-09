package org.hisp.dhis.android.core.category;


import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;

@AutoValue
public abstract class CategoryOptionModel extends BaseIdentifiableObjectModel {
    public static final String TABLE = "CategoryOption";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String SHORT_NAME = "shortName";
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @NonNull
    public static CategoryOptionModel.Builder builder() {
        return new $$AutoValue_CategoryOptionModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.SHORT_NAME)
    public abstract String shortName();

    @NonNull
    public static CategoryOptionModel create(Cursor cursor) {
        return AutoValue_CategoryOptionModel.createFromCursor(cursor);

    }

    @AutoValue.Builder
    public static abstract class Builder extends
            BaseIdentifiableObjectModel.Builder<CategoryOptionModel.Builder> {
        public abstract Builder shortName(@Nullable String shortName);

        public abstract CategoryOptionModel build();
    }
}
