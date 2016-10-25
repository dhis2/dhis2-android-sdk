package org.hisp.dhis.client.models.trackedentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseModel;

import javax.annotation.Nullable;

//TODO: Tests
@AutoValue
@JsonDeserialize(builder = AutoValue_TrackedEntityDataValue.Builder.class)
public abstract class TrackedEntityDataValue extends BaseModel {
    private final static String JSON_PROPERTY_DATA_ELEMENT = "dataElement";
    private final static String JSON_PROPERTY_STORED_BY = "storedBy";
    private final static String JSON_PROPERTY_VALUE = "value";

    @Nullable
    @JsonIgnore
    public abstract String event();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @JsonProperty(JSON_PROPERTY_STORED_BY)
    public abstract String storedBy();

    @Nullable
    @JsonProperty(JSON_PROPERTY_VALUE)
    public abstract String value();

    public static Builder builder() {
        return new AutoValue_TrackedEntityDataValue.Builder();
    }

    @Override
    public boolean isValid() {
        if (event() == null) {
            return false;
        }

        if (dataElement() == null) {
            return false;
        }

        if (storedBy() == null) {
            return false;
        }

        if (value() == null) {
            return false;
        }

        return true;
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_DATA_ELEMENT)
        public abstract Builder dataElement(@Nullable String dataElement);

        @JsonProperty(JSON_PROPERTY_STORED_BY)
        public abstract Builder storedBy(@Nullable String storedBy);

        @JsonProperty(JSON_PROPERTY_VALUE)
        public abstract Builder value(@Nullable String value);

        @JsonIgnore
        public abstract Builder event(@Nullable String event);

        public abstract TrackedEntityDataValue build();
    }
}
