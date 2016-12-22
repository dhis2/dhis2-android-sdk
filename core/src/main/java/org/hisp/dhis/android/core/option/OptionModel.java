package org.hisp.dhis.android.core.option;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

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

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {
        public abstract OptionModel build();
    }
}
