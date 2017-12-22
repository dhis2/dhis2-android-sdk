package org.hisp.dhis.rules.functions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleFunctionShould {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void evaluate_correct_rule_function() {
        assertThat(RuleFunction.create("d2:daysBetween"))
                .isInstanceOf(RuleFunctionDaysBetween.class);

        assertThat(RuleFunction.create("d2:addDays"))
                .isInstanceOf(RuleFunctionAddDays.class);

        assertThat(RuleFunction.create("d2:ceil"))
                .isInstanceOf(RuleFunctionCeil.class);

        assertThat(RuleFunction.create("d2:concatenate"))
                .isInstanceOf(RuleFunctionConcatenate.class);

        assertThat(RuleFunction.create("d2:countIfValue"))
                .isInstanceOf(RuleFunctionCountIfValue.class);

        assertThat(RuleFunction.create("d2:countIfZeroPos"))
                .isInstanceOf(RuleFunctionCountIfZeroPos.class);

        assertThat(RuleFunction.create("d2:count"))
                .isInstanceOf(RuleFunctionCount.class);

        assertThat(RuleFunction.create("d2:floor"))
                .isInstanceOf(RuleFunctionFloor.class);

        assertThat(RuleFunction.create("d2:hasValue"))
                .isInstanceOf(RuleFunctionHasValue.class);

        assertThat(RuleFunction.create("d2:left"))
                .isInstanceOf(RuleFunctionLeft.class);

        assertThat(RuleFunction.create("d2:length"))
                .isInstanceOf(RuleFunctionLength.class);

        assertThat(RuleFunction.create("d2:modulus"))
                .isInstanceOf(RuleFunctionModulus.class);

        assertThat(RuleFunction.create("d2:monthsBetween"))
                .isInstanceOf(RuleFunctionMonthsBetween.class);

        assertThat(RuleFunction.create("d2:oizp"))
                .isInstanceOf(RuleFunctionOizp.class);

        assertThat(RuleFunction.create("d2:right"))
                .isInstanceOf(RuleFunctionRight.class);

        assertThat(RuleFunction.create("d2:round"))
                .isInstanceOf(RuleFunctionRound.class);

        assertThat(RuleFunction.create("d2:split"))
                .isInstanceOf(RuleFunctionSplit.class);

        assertThat(RuleFunction.create("d2:substring"))
                .isInstanceOf(RuleFunctionSubString.class);

        assertThat(RuleFunction.create("d2:validatePattern"))
                .isInstanceOf(RuleFunctionValidatePattern.class);

        assertThat(RuleFunction.create("d2:weeksBetween"))
                .isInstanceOf(RuleFunctionWeeksBetween.class);

        assertThat(RuleFunction.create("d2:yearsBetween"))
                .isInstanceOf(RuleFunctionYearsBetween.class);

        assertThat(RuleFunction.create("d2:zing"))
                .isInstanceOf(RuleFunctionZing.class);

        assertThat(RuleFunction.create("d2:zpvc"))
                .isInstanceOf(RuleFunctionZpvc.class);
    }

    @Test
    public void evaluate_incorrect_rule_function_as_null() {
        assertThat(RuleFunction.create("d2:fake")).isNull();
    }
    @Test
    public void throw_null_pointer_exception_on_create_rule_function_with_null_parameter() {
        thrown.expect(NullPointerException.class);
        assertThat(RuleFunction.create(null)).isNull();
    }
    @Test
    public void evaluate_incorrect_rule_function_empty_string_parameter() {
        assertThat(RuleFunction.create("")).isNull();
    }
}
