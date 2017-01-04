package org.hisp.dhis.android.core.option;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;

@AutoValue
public abstract class OptionModel extends BaseIdentifiableObjectModel {

    public static OptionModel create(Cursor cursor) {
        return AutoValue_OptionModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_OptionModel.Builder();
    }

    @Nullable
    @ColumnName(OptionContract.Columns.OPTION_SET)
    public abstract String optionSet();

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder optionSet(@Nullable String optionSet);

        public abstract OptionModel build();
    }
}
