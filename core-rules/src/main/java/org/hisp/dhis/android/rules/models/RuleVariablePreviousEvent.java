package org.hisp.dhis.android.rules.models;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RuleVariablePreviousEvent extends RuleVariableDataElement {

//    /**
//     *
//     * @param currentRuleEvent RuleEvent which is set in the execution context.
//     * @param valueMap Map of data elements to data values, which are
//     *                 sorted by event date (from newest to oldest).
//     * @return Value found for the given variable.
//     */
//    @Nonnull
//    RuleVariableValue value(@Nonnull RuleEvent currentRuleEvent,
//            @Nonnull Map<String, List<RuleDataValue>> valueMap) {
//        List<RuleDataValue> ruleDataValues = valueMap.get(dataElement());
//        if (ruleDataValues != null && !ruleDataValues.isEmpty()) {
//            for (RuleDataValue ruleDataValue : ruleDataValues) {
//                // We found preceding value to the current event,
//                // which is assumed to be best candidate.
//                if (currentRuleEvent.eventDate().compareTo(ruleDataValue.eventDate()) < 0) {
//                    return RuleVariableValue.create(ruleDataValue.value(), dataElementType());
//                }
//            }
//        }
//
//        return RuleVariableValue.create(dataElementType());
//    }
}
