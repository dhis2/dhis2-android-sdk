package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.models.dataelement.DataElement;
import org.hisp.dhis.client.models.trackedentity.TrackedEntityAttribute;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramRuleVariable.Builder.class)
public abstract class ProgramRuleVariable extends BaseIdentifiableObject {
    private static final String PROGRAM_STAGE = "programStage";
    private static final String PROGRAM_RULE_VARIABLE_SOURCE_TYPE = "programRuleVariableSourceType";
    private static final String USE_CODE_FOR_OPTION_SET = "useCodeForOptionSet";
    private static final String PROGRAM = "program";
    private static final String DATA_ELEMENT = "dataElement";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";

    @Nullable
    @JsonProperty(PROGRAM_STAGE)
    public abstract ProgramStage programStage();

    @Nullable
    @JsonProperty(PROGRAM_RULE_VARIABLE_SOURCE_TYPE)
    public abstract ProgramRuleVariableSourceType programRuleVariableSourceType();

    @Nullable
    @JsonProperty(USE_CODE_FOR_OPTION_SET)
    public abstract Boolean useCodeForOptionSet();

    @Nullable
    @JsonProperty(PROGRAM)
    public abstract Program program();

    @Nullable
    @JsonProperty(DATA_ELEMENT)
    public abstract DataElement dataElement();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_ATTRIBUTE)
    public abstract TrackedEntityAttribute trackedEntityAttribute();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(PROGRAM_STAGE)
        public abstract Builder programStage(@Nullable ProgramStage programStage);

        @JsonProperty(PROGRAM_RULE_VARIABLE_SOURCE_TYPE)
        public abstract Builder programRuleVariableSourceType(@Nullable ProgramRuleVariableSourceType programRuleVariableSourceType);

        @JsonProperty(USE_CODE_FOR_OPTION_SET)
        public abstract Builder useCodeForOptionSet(@Nullable Boolean useCodeForOptionSet);

        @JsonProperty(PROGRAM)
        public abstract Builder program(@Nullable Program program);

        @JsonProperty(DATA_ELEMENT)
        public abstract Builder dataElement(@Nullable DataElement dataElement);

        @JsonProperty(TRACKED_ENTITY_ATTRIBUTE)
        public abstract Builder trackedEntityAttribute(@Nullable TrackedEntityAttribute trackedEntityAttribute);

        public abstract ProgramRuleVariable build();
    }
}