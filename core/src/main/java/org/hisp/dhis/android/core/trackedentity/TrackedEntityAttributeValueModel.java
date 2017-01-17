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

    public static class Columns extends BaseDataModel.Columns {
        public static final String VALUE = "value";
        public static final String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
        public static final String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";
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
    @ColumnName(Columns.VALUE)
    public abstract String value();

    @Nullable
    @ColumnName(Columns.TRACKED_ENTITY_ATTRIBUTE)
    public abstract String trackedEntityAttribute();

    @Nullable
    @ColumnName(Columns.TRACKED_ENTITY_INSTANCE)
    public abstract String trackedEntityInstance();

    @AutoValue.Builder
    public static abstract class Builder extends BaseDataModel.Builder<Builder> {

        public abstract Builder value(String value);

        public abstract Builder trackedEntityAttribute(String trackedEntityAttribute);

        public abstract Builder trackedEntityInstance(String trackedEntityInstance);

        public abstract TrackedEntityAttributeValueModel build();
    }
}
