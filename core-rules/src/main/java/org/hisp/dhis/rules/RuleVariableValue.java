package org.hisp.dhis.rules;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.rules.models.RuleValueType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
public abstract class RuleVariableValue {

    @Nullable
    public abstract String value();

    @Nonnull
    public abstract RuleValueType type();

    @Nonnull
    public abstract List<String> candidates();

    @Nonnull
    static RuleVariableValue create(@Nonnull RuleValueType ruleValueType) {
        return new AutoValue_RuleVariableValue(null, ruleValueType,
                Collections.unmodifiableList(new ArrayList<String>()));
    }

    @Nonnull
    static RuleVariableValue create(@Nonnull String value,
            @Nonnull RuleValueType ruleValueType) {

        // clean-up the value before processing it
        value = value.replace("'", "");

        // if text value, wrap it
        if (RuleValueType.TEXT.equals(ruleValueType)) {
            value = String.format(Locale.US, "'%s'", value);
        }

        return new AutoValue_RuleVariableValue(value, ruleValueType,
                Collections.unmodifiableList(new ArrayList<String>()));
    }

    @Nonnull
    static RuleVariableValue create(@Nonnull String value,
            @Nonnull RuleValueType ruleValueType, @Nonnull List<String> candidates) {
        // clean-up the value before processing it
        value = value.replace("'", "");

        // if text value, wrap it
        if (RuleValueType.TEXT.equals(ruleValueType)) {
            value = String.format(Locale.US, "'%s'", value);
        }

        return new AutoValue_RuleVariableValue(value, ruleValueType,
                Collections.unmodifiableList(candidates));
    }
}
