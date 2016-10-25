package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseNameableObject;
import org.hisp.dhis.client.models.common.ValueType;
import org.hisp.dhis.client.models.trackedentity.TrackedEntityAttribute;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramTrackedEntityAttribute.Builder.class)
public abstract class ProgramTrackedEntityAttribute extends BaseNameableObject {

    private static final String MANDATORY = "mandatory";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
    private static final String VALUE_TYPE = "valueType";
    private static final String ALLOW_FUTURE_DATE = "allowFutureDate";
    private static final String DISPLAY_IN_LIST = "displayInList";

    @Nullable
    @JsonProperty(MANDATORY)
    public abstract Boolean mandatory();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_ATTRIBUTE)
    public abstract TrackedEntityAttribute trackedEntityAttribute();

    @Nullable
    @JsonProperty(VALUE_TYPE)
    public abstract ValueType valueType();

    @Nullable
    @JsonProperty(ALLOW_FUTURE_DATE)
    public abstract Boolean allowFutureDate();

    @Nullable
    @JsonProperty(DISPLAY_IN_LIST)
    public abstract Boolean displayInList();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {
        @JsonProperty(MANDATORY)
        public abstract Builder mandatory(@Nullable Boolean mandatory);

        @JsonProperty(TRACKED_ENTITY_ATTRIBUTE)
        public abstract Builder trackedEntityAttribute(@Nullable TrackedEntityAttribute trackedEntityAttribute);

        @JsonProperty(VALUE_TYPE)
        public abstract Builder valueType(@Nullable ValueType valueType);

        @JsonProperty(ALLOW_FUTURE_DATE)
        public abstract Builder allowFutureDate(@Nullable Boolean allowFutureFate);

        @JsonProperty(DISPLAY_IN_LIST)
        public abstract Builder displayInList(@Nullable Boolean displayInList);

        abstract ProgramTrackedEntityAttribute build();
    }

}
