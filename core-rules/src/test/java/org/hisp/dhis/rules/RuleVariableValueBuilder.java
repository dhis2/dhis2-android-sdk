package org.hisp.dhis.rules;

import org.hisp.dhis.rules.models.RuleValueType;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class RuleVariableValueBuilder {

    private String value;
    private RuleValueType type = RuleValueType.TEXT;
    private List<String> candidates = new ArrayList<>();

    public static RuleVariableValueBuilder create() {
        return new RuleVariableValueBuilder();
    }

    public RuleVariableValueBuilder withValue(@Nonnull String value) {
        this.value = value;

        return this;
    }

    public RuleVariableValueBuilder withCandidates(@Nonnull List<String> candidates) {
        this.candidates = candidates;

        return this;
    }

    public RuleVariableValue build() {
        RuleVariableValue ruleVariableValue = RuleVariableValue.create(value, type, candidates);

        return ruleVariableValue;
    }
}
