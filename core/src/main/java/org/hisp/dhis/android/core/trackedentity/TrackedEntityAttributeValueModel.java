package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;

@AutoValue
public abstract class TrackedEntityAttributeValueModel extends BaseModel {

    public interface Columns extends BaseModel.Columns {
        String ATTRIBUTE = "attribute";
        String VALUE = "value";
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
    public abstract String trackedEntityInstance();

    @Nullable
    @JsonProperty(Columns.ATTRIBUTE)
    public abstract String trackedEntityAttribute();

    @Nullable
    @JsonProperty(Columns.VALUE)
    public abstract String value();

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {

        public abstract Builder trackedEntityInstance(@Nullable String trackedEntityInstance);

        public abstract Builder trackedEntityAttribute(String trackedEntityAttribute);

        public abstract Builder value(String value);

        public abstract TrackedEntityAttributeValueModel build();
    }
}
