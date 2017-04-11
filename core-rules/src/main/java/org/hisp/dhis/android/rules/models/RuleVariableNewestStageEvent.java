package org.hisp.dhis.android.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleVariableNewestStageEvent extends RuleVariableDataElement {

    @Nonnull
    abstract String programStage();

//    @Nonnull
//    RuleVariableValue value(@Nonnull Map<String, List<RuleDataValue>> valueMap) {
//        List<RuleDataValue> ruleDataValues = valueMap.get(dataElement());
//        if (ruleDataValues != null && !ruleDataValues.isEmpty()) {
//            // Iterate over all candidates and find the one which
//            // matches to program stage in variable.
//            for (RuleDataValue ruleDataValue : ruleDataValues) {
//                if (programStage().equals(ruleDataValue.programStage())) {
//                    return RuleVariableValue.create(ruleDataValue.value(), dataElementType());
//                }
//            }
//        }
//
//        return RuleVariableValue.create(dataElementType());
//    }
}
