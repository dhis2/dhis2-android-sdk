package org.hisp.dhis.client.models.program;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseIdentifiableObject;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramRule.Builder.class)
public abstract class ProgramRule extends BaseIdentifiableObject {

    private static final String PROGRAM_STAGE = "programStage";
    private static final String PROGRAM = "program";
    private static final String PRIORITY = "priority";
    private static final String CONDITION = "condition";
    private static final String PROGRAM_RULE_ACTIONS = "programRuleActions";

    @Nullable
    @JsonProperty(PROGRAM_STAGE)
    public abstract ProgramStage programStage();

    @Nullable
    @JsonProperty(PROGRAM)
    public abstract Program program();

    @Nullable
    @JsonProperty(PRIORITY)
    public abstract Integer priority();

    @Nullable
    @JsonProperty(CONDITION)
    public abstract String condition();

    @Nullable
    @JsonProperty(PROGRAM_RULE_ACTIONS)
    public abstract List<ProgramRuleAction> programRuleActions();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(PROGRAM_STAGE)
        public abstract Builder programStage(@Nullable ProgramStage programStage);

        @JsonProperty(PROGRAM)
        public abstract Builder program(@Nullable Program program);

        @JsonProperty(PRIORITY)
        public abstract Builder priority(@Nullable Integer priority);

        @JsonProperty(CONDITION)
        public abstract Builder condition(@Nullable String condition);

        @JsonProperty(PROGRAM_RULE_ACTIONS)
        public abstract Builder programRuleActions(@Nullable List<ProgramRuleAction> programRuleActions);

        abstract ProgramRule autoBuild();

        abstract java.util.List<ProgramRuleAction> programRuleActions();

        public ProgramRule build() {
            if (programRuleActions() != null) {
                programRuleActions(Collections.unmodifiableList(programRuleActions()));
            }
            return autoBuild();
        }
    }
}