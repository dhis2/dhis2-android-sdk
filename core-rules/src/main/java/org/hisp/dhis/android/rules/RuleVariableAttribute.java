package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.Map;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleVariableAttribute extends RuleVariable {

    @Nonnull
    public abstract String trackedEntityAttribute();

    @Nonnull
    public abstract RuleValueType trackedEntityAttributeType();

    @Nonnull
    public static RuleVariableAttribute create(@Nonnull String name,
            @Nonnull String trackedEntityAttribute, @Nonnull RuleValueType trackedEntityAttributeType) {
        return new AutoValue_RuleVariableAttribute(name, trackedEntityAttribute, trackedEntityAttributeType);
    }

    @Nonnull
    RuleVariableValue value(@Nonnull Map<String, RuleAttributeValue> valueMap) {
        if (valueMap.containsKey(trackedEntityAttribute())) {
            RuleAttributeValue value = valueMap.get(trackedEntityAttribute());
            return RuleVariableValue.create(value.value(), trackedEntityAttributeType());
        }

        return RuleVariableValue.create(trackedEntityAttributeType());
    }
}
