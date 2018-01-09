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
        String processedValue = value.replace("'", "");

        // if text processedValue, wrap it
        if (RuleValueType.TEXT.equals(ruleValueType)) {
            processedValue = String.format(Locale.US, "'%s'", processedValue);
        }

        return new AutoValue_RuleVariableValue(processedValue, ruleValueType,
                Collections.unmodifiableList(new ArrayList<String>()));
    }

    @Nonnull
    static RuleVariableValue create(@Nonnull String value,
            @Nonnull RuleValueType ruleValueType, @Nonnull List<String> candidates) {
        // clean-up the value before processing it
        String processedValue = value.replace("'", "");

        // if text processedValue, wrap it
        if (RuleValueType.TEXT.equals(ruleValueType)) {
            processedValue = String.format(Locale.US, "'%s'", processedValue);
        }

        return new AutoValue_RuleVariableValue(processedValue, ruleValueType,
                Collections.unmodifiableList(candidates));
    }
}
