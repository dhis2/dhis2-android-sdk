package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.models.dataelement.DataElement;
import org.hisp.dhis.client.models.trackedentity.TrackedEntityAttribute;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramRuleAction.Builder.class)
public abstract class ProgramRuleAction extends BaseIdentifiableObject {
    private static final String DATA = "data";
    private static final String CONTENT = "content";
    private static final String LOCATION = "location";
    private static final String ATTRIBUTE = "attribute";
    private static final String PROGRAM_INDICATOR = "programIndicator";
    private static final String PROGRAM_STAGE_SECTION = "programStageSection";
    private static final String PROGRAM_RULE_ACTION_TYPE = "programRuleActionType";
    private static final String PROGRAM_STAGE = "programStage";
    private static final String DATA_ELEMENT = "dataElement";

    @Nullable
    @JsonProperty(DATA)
    public abstract String data();

    @Nullable
    @JsonProperty(CONTENT)
    public abstract String content();

    @Nullable
    @JsonProperty(LOCATION)
    public abstract String location();

    @Nullable
    @JsonProperty(ATTRIBUTE)
    public abstract TrackedEntityAttribute attribute();

    @Nullable
    @JsonProperty(PROGRAM_INDICATOR)
    public abstract ProgramIndicator programIndicator();

    @Nullable
    @JsonProperty(PROGRAM_STAGE_SECTION)
    public abstract ProgramStageSection programStageSection();

    @Nullable
    @JsonProperty(PROGRAM_RULE_ACTION_TYPE)
    public abstract ProgramRuleActionType programRuleActionType();

    @Nullable
    @JsonProperty(PROGRAM_STAGE)
    public abstract ProgramStage programStage();

    @Nullable
    @JsonProperty(DATA_ELEMENT)
    public abstract DataElement dataElement();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {
        @JsonProperty(DATA)
        public abstract Builder data(@Nullable String data);

        @JsonProperty(CONTENT)
        public abstract Builder content(@Nullable String content);

        @JsonProperty(LOCATION)
        public abstract Builder location(@Nullable String location);

        @JsonProperty(ATTRIBUTE)
        public abstract Builder attribute(@Nullable TrackedEntityAttribute trackedEntityAttribute);

        @JsonProperty(PROGRAM_INDICATOR)
        public abstract Builder programIndicator(@Nullable ProgramIndicator programIndicator);

        @JsonProperty(PROGRAM_STAGE_SECTION)
        public abstract Builder programStageSection(@Nullable ProgramStageSection programStageSection);

        @JsonProperty(PROGRAM_RULE_ACTION_TYPE)
        public abstract Builder programRuleActionType(@Nullable ProgramRuleActionType programRuleActionType);

        @JsonProperty(PROGRAM_STAGE)
        public abstract Builder programStage(@Nullable ProgramStage programStage);

        @JsonProperty(DATA_ELEMENT)
        public abstract Builder dataElement(@Nullable DataElement dataElement);

        public abstract ProgramRuleAction build();
    }
}
