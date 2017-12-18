package org.hisp.dhis.android.core.category;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;

@AutoValue
public abstract class CategoryComboModel extends BaseIdentifiableObjectModel {
    public static final String TABLE = "CategoryCombo";

    @Nullable
    @ColumnName(Columns.IS_DEFAULT)
    public abstract Boolean isDefault();

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String IS_DEFAULT = "isDefault";

    }

    @NonNull
    public abstract ContentValues toContentValues();

    @NonNull
    public static Builder builder() {
        return new $$AutoValue_CategoryComboModel.Builder();
    }

    @NonNull
    public static CategoryComboModel create(Cursor cursor) {
        return AutoValue_CategoryComboModel.createFromCursor(cursor);
    }

    @AutoValue.Builder
    public static abstract class Builder extends
            BaseIdentifiableObjectModel.Builder<CategoryComboModel.Builder> {
        public abstract CategoryComboModel.Builder isDefault(@Nullable Boolean isDefault);

        public abstract CategoryComboModel build();
    }
}
