package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
abstract class RuleVariableValue {

    @Nullable
    abstract String value();

    @Nonnull
    abstract RuleValueType valueType();

    @Nonnull
    static RuleVariableValue create(@Nonnull RuleValueType ruleValueType) {
        return new AutoValue_RuleVariableValue(null, ruleValueType);
    }

    @Nonnull
    static RuleVariableValue create(@Nonnull String value, @Nonnull RuleValueType ruleValueType) {
        return new AutoValue_RuleVariableValue(value, ruleValueType);
    }
}
