package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleVariableNewestEvent extends RuleVariable {

    @Nonnull
    public abstract String dataElement();

    @Nonnull
    public abstract RuleValueType dataElementType();

    @Nonnull
    RuleVariableValue value(@Nonnull Map<String, List<RuleDataValue>> valueMap) {
        List<RuleDataValue> ruleDataValues = valueMap.get(dataElement());
        if (ruleDataValues != null && !ruleDataValues.isEmpty()) {
            return RuleVariableValue.create(ruleDataValues.get(0).value(), dataElementType());
        }

        return RuleVariableValue.create(dataElementType());
    }
}
