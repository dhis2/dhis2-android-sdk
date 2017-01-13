package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseDataModel;

@AutoValue
public abstract class TrackedEntityAttributeValueModel extends BaseDataModel {

    public interface Columns extends BaseDataModel.Columns {
        String VALUE = "value";
        String ATTRIBUTE = "trackedEntityAttribute";
        String INSTANCE = "trackedEntityAttributeInstance";
    }

    public static TrackedEntityAttributeValueModel create(Cursor cursor) {
        return AutoValue_TrackedEntityAttributeValueModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_TrackedEntityAttributeValueModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @Nullable
    @ColumnName(Columns.ATTRIBUTE)
    public abstract String trackedEntityAttribute();

    @Nullable
    @ColumnName(Columns.VALUE)
    public abstract String value();

    @AutoValue.Builder
    public static abstract class Builder extends BaseDataModel.Builder<Builder> {

        public abstract Builder trackedEntityAttribute(String trackedEntityAttribute);

        public abstract Builder value(String value);

        public abstract TrackedEntityAttributeValueModel build();
    }
}
