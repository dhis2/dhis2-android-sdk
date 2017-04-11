package org.hisp.dhis.android.rules.models;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RuleVariableCurrentEvent extends RuleVariableDataElement {

//
//    @Nonnull
//    RuleVariableValue value(@Nonnull Map<String, RuleDataValue> valueMap) {
//        if (valueMap.containsKey(dataElement())) {
//            RuleDataValue value = valueMap.get(dataElement());
//            return RuleVariableValue.create(value.value(), dataElementType());
//        }
//
//        return RuleVariableValue.create(dataElementType());
//    }
}
