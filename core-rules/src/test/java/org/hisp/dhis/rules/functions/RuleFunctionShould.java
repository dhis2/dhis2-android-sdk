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
