package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleVariableNewestStageEvent extends RuleVariable {

    @Nonnull
    public abstract String dataElement();

    @Nonnull
    public abstract RuleValueType dataElementType();

    @Nonnull
    abstract String programStage();

    @Nonnull
    RuleVariableValue value(@Nonnull Map<String, List<RuleDataValue>> valueMap) {
        List<RuleDataValue> ruleDataValues = valueMap.get(dataElement());
        if (ruleDataValues != null && !ruleDataValues.isEmpty()) {
            // Iterate over all candidates and find the one which
            // matches to program stage in variable.
            for (RuleDataValue ruleDataValue : ruleDataValues) {
                if (programStage().equals(ruleDataValue.programStage())) {
                    return RuleVariableValue.create(ruleDataValue.value(), dataElementType());
                }
            }
        }

        return RuleVariableValue.create(dataElementType());
    }
}
