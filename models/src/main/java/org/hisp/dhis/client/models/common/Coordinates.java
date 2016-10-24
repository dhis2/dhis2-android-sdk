package org.hisp.dhis.client.models.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;


//TODO: Implement test

@AutoValue
@JsonDeserialize(builder = AutoValue_Coordinates.Builder.class)
public abstract class Coordinates {
    private final static String JSON_PROPERTY_LATITUDE = "latitude";
    private final static String JSON_PROPERTY_LONGITUDE = "longitude";

    @JsonProperty(JSON_PROPERTY_LATITUDE)
    public abstract Double latitude();

    @JsonProperty(JSON_PROPERTY_LONGITUDE)
    public abstract Double longitude();

    public static Builder builder() {
        return new AutoValue_Coordinates.Builder();
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder extends Builder<Builder> {
        @JsonProperty(JSON_PROPERTY_LATITUDE)
        public abstract Builder latitude(Double latitude);

        @JsonProperty(JSON_PROPERTY_LONGITUDE)
        public abstract Builder longitude(Double longitude);

        public abstract Coordinates build();
    }


}
