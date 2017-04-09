package org.hisp.dhis.android.rules.models.variables;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.rules.models.Option;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
public abstract class AttributeVariable extends RuleVariable {

    @Nonnull
    public static AttributeVariable create(@Nonnull String name, @Nonnull String field,
            @Nonnull ValueType valueType, @Nonnull Boolean useCode, @Nonnull List<Option> options) {
        return new AutoValue_AttributeVariable(name, field, valueType, useCode, options);
    }

    @Nonnull
    public RuleVariableValue createValue(@Nullable List<DataValue> dataValues) {
        if (dataValues != null && dataValues.isEmpty()) {

        }
//
//        if (valueMap.containsKey(variable.trackedEntityAttribute())) {
//            TrackedEntityAttributeValue attributeValue
//                    = valueMap.get(variable.trackedEntityAttribute());
//            return create(attributeValue.value(), variable.trackedEntityAttributeType(), true);
//        }
//
//        return create(variable.trackedEntityAttributeType());
        return null;
    }
}
