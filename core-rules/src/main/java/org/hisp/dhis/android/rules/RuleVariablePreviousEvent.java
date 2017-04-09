package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleVariablePreviousEvent extends RuleVariable {

    @Nonnull
    public abstract String dataElement();

    @Nonnull
    public abstract RuleValueType dataElementType();

    /**
     *
     * @param currentRuleEvent RuleEvent which is set in the execution context.
     * @param valueMap Map of data elements to data values, which are
     *                 sorted by event date (from newest to oldest).
     * @return Value found for the given variable.
     */
    @Nonnull
    RuleVariableValue value(@Nonnull RuleEvent currentRuleEvent,
            @Nonnull Map<String, List<RuleDataValue>> valueMap) {
        List<RuleDataValue> ruleDataValues = valueMap.get(dataElement());
        if (ruleDataValues != null && !ruleDataValues.isEmpty()) {
            for (RuleDataValue ruleDataValue : ruleDataValues) {
                // We found preceding value to the current event,
                // which is assumed to be best candidate.
                if (currentRuleEvent.eventDate().compareTo(ruleDataValue.eventDate()) < 0) {
                    return RuleVariableValue.create(ruleDataValue.value(), dataElementType());
                }
            }
        }

        return RuleVariableValue.create(dataElementType());
    }
}
