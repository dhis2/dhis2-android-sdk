package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.RuleValueType;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Java6Assertions.assertThat;

final class RuleVariableValueAssert {
    private final RuleVariableValue variableValue;

    private RuleVariableValueAssert(RuleVariableValue variableValue) {
        this.variableValue = variableValue;
    }

    @Nonnull
    static RuleVariableValueAssert assertThatVariable(@Nonnull RuleVariableValue variableValue) {
        return new RuleVariableValueAssert(variableValue);
    }

    @Nonnull
    RuleVariableValueAssert hasValue(@Nonnull String value) {
        assertThat(variableValue.value()).isEqualTo(value);
        return this;
    }

    @Nonnull
    RuleVariableValueAssert hasCandidates(@Nonnull String... candidates) {
        assertThat(variableValue.candidates().size()).isEqualTo(candidates.length);
        for (int index = 0; index < candidates.length; index++) {
            assertThat(variableValue.candidates().get(index)).isEqualTo(candidates[index]);
        }
        return this;
    }

    @Nonnull
    RuleVariableValueAssert isTypeOf(@Nonnull RuleValueType valueType) {
        assertThat(variableValue.type()).isEqualTo(valueType);
        return this;
    }
}
