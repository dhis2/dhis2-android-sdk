package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import static java.util.Collections.unmodifiableList;

@AutoValue
abstract class ProgramRuleVariableValue {

    // current value of the ProgramRuleVariable instance
    @Nonnull
    abstract String value();

    // all the value candidates for this variable
    @Nonnull
    abstract List<String> valueCandidates();

    @Nonnull
    abstract ValueType valueType();

    // true if the value has been set explicitly
    @Nonnull
    abstract Boolean hasValue();

    // ToDo: put in event date

    @Nonnull
    ProgramRuleVariableValue assignValue(@Nonnull String dataValue) {
        return create(dataValue, valueType(), true);
    }

    @Nonnull
    static ProgramRuleVariableValue create(@Nonnull ValueType valueType) {
        String defaultDataValue;
        if (valueType.isNumeric()) {
            defaultDataValue = "0";
        } else if (valueType.isBoolean()) {
            defaultDataValue = "false";
        } else {
            defaultDataValue = "''";
        }

        return create(defaultDataValue, valueType, false);
    }

    @Nonnull
    static ProgramRuleVariableValue create(@Nonnull String dataValue,
            @Nonnull ValueType valueType, @Nonnull Boolean hasValue) {
        return create(dataValue, unmodifiableList(Arrays.asList(dataValue)), valueType, hasValue);
    }

    @Nonnull
    static ProgramRuleVariableValue create(@Nonnull String dataValue, @Nonnull List<String> candidates,
            @Nonnull ValueType valueType, @Nonnull Boolean hasValue) {
        return new AutoValue_ProgramRuleVariableValue(dataValue,
                unmodifiableList(new ArrayList<>(candidates)), valueType, hasValue);
    }
}
