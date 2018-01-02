package org.hisp.dhis.android.core.category;


import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;

@AutoValue
public abstract class CategoryOptionModel extends BaseIdentifiableObjectModel {
    public static final String TABLE = "CategoryOption";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @NonNull
    public static CategoryOptionModel.Builder builder() {
        return new $$AutoValue_CategoryOptionModel.Builder();
    }

    @NonNull
    public static CategoryOptionModel create(Cursor cursor) {
        return AutoValue_CategoryOptionModel.createFromCursor(cursor);

    }

    @AutoValue.Builder
    public static abstract class Builder extends
            BaseIdentifiableObjectModel.Builder<CategoryOptionModel.Builder> {

        public abstract CategoryOptionModel build();
    }
}
