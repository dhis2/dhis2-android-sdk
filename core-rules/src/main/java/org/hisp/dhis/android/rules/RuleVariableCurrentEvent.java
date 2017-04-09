package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.Map;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleVariableCurrentEvent extends RuleVariable {

    @Nonnull
    public abstract String dataElement();

    @Nonnull
    public abstract RuleValueType dataElementType();

    @Nonnull
    RuleVariableValue value(@Nonnull Map<String, RuleDataValue> valueMap) {
        if (valueMap.containsKey(dataElement())) {
            RuleDataValue value = valueMap.get(dataElement());
            return RuleVariableValue.create(value.value(), dataElementType());
        }

        return RuleVariableValue.create(dataElementType());
    }
}
