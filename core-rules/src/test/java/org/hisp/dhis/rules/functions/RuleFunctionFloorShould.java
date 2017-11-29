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
public class RuleFunctionFloorShould {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void evaluate_correct_floored_value() {
        RuleFunction ceil = RuleFunctionFloor.create();

        String flooredNumber = ceil.evaluate(Arrays.asList("5.9"),
                new HashMap<String, RuleVariableValue>());

        assertThat(flooredNumber).isEqualTo("5");
    }
    @Test
    public void evaluate_correct_floored_value_without_decimals() {
        RuleFunction ceil = RuleFunctionFloor.create();

        String flooredNumber = ceil.evaluate(Arrays.asList("5"),
                new HashMap<String, RuleVariableValue>());

        assertThat(flooredNumber).isEqualTo("5");
    }

    @Test
    public void evaluate_correct_floored_negative_value_without_decimals() {
        RuleFunction ceil = RuleFunctionFloor.create();

        String flooredNumber = ceil.evaluate(Arrays.asList("-5"),
                new HashMap<String, RuleVariableValue>());

        assertThat(flooredNumber).isEqualTo("-5");
    }

    @Test
    public void evaluate_correct_floored_negative_value() {
        RuleFunction ceil = RuleFunctionFloor.create();

        String flooredNumber = ceil.evaluate(Arrays.asList("-5.9"),
                new HashMap<String, RuleVariableValue>());

        assertThat(flooredNumber).isEqualTo("-5");
    }

    @Test
    public void throw_illegal_argument_exception_on_more_arguments_count_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionFloor.create().evaluate(Arrays.asList("5.9", "6.8"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_on_less_arguments_count_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionFloor.create().evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
