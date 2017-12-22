package org.hisp.dhis.rules;

import org.hisp.dhis.rules.models.RuleValueType;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

public class RuleVariableValueBuilder {

    private String value;
    private RuleValueType type = RuleValueType.TEXT;
    private List<String> candidates = null;

    public static RuleVariableValueBuilder create() {
        return new RuleVariableValueBuilder();
    }

    public RuleVariableValueBuilder withValue(@Nonnull String value) {
        this.value = value;

        return this;
    }

    public RuleVariableValue build() {
        if (candidates == null) {
            candidates = Arrays.asList(value);
        }

        RuleVariableValue ruleVariableValue = RuleVariableValue.create(value, type, candidates);

        return ruleVariableValue;
    }
}
