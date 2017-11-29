package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleFunctionCeilShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void evaluate_correct_ceiled_value() {
        RuleFunction ceil = RuleFunctionCeil.create();

        String ceiledNumber = ceil.evaluate(Arrays.asList("5.1"),
                new HashMap<String, RuleVariableValue>());

        assertThat(ceiledNumber).isEqualTo("6");
    }
    @Test
    public void evaluate_correct_ceiled_value_without_decimals() {
        RuleFunction ceil = RuleFunctionCeil.create();

        String ceiledNumber = ceil.evaluate(Arrays.asList("5"),
                new HashMap<String, RuleVariableValue>());

        assertThat(ceiledNumber).isEqualTo("5");
    }

    @Test
    public void evaluate_correct_ceiled_negative_value_without_decimals() {
        RuleFunction ceil = RuleFunctionCeil.create();

        String ceiledNumber = ceil.evaluate(Arrays.asList("-5"),
                new HashMap<String, RuleVariableValue>());

        assertThat(ceiledNumber).isEqualTo("-5");
    }

    @Test
    public void evaluate_correct_ceiled_negative_value() {
        RuleFunction ceil = RuleFunctionCeil.create();

        String ceiledNumber = ceil.evaluate(Arrays.asList("-5.9"),
                new HashMap<String, RuleVariableValue>());

        assertThat(ceiledNumber).isEqualTo("-5");
    }

    @Test
    public void throw_illegal_argument_exception_on_more_arguments_count_than_expected() {
        thrown.expect(IllegalArgumentException.class);
            RuleFunctionCeil.create().evaluate(Arrays.asList("5.9", "6.8"),
                    new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_on_less_arguments_count_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCeil.create().evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
