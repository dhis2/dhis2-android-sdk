package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RuleFunctionLeftShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void return_correct_values() {
        RuleFunction ruleFunction = RuleFunctionLeft.create();
        String result = ruleFunction.evaluate(Arrays.asList("test_variable_one", "1"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("t");
        result = ruleFunction.evaluate(Arrays.asList("test_variable_one", "0"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("");
        result = ruleFunction.evaluate(Arrays.asList("test_variable_one", "20"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("test_variable_one");
        result = ruleFunction.evaluate(Arrays.asList("test_variable_one", "2"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("te");
        result = ruleFunction.evaluate(Arrays.asList("test_variable_one", "4"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("test");
    }

    @Test
    public void thrown_null_pointer_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionLeft.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_pass_only_one_parameters() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionLeft.create();

        ruleFunction.evaluate(Arrays.asList("test_variable_one"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_pass_three_parameters() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionLeft.create();

        ruleFunction.evaluate(Arrays.asList("test_variable_one", "variable", "1"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_number_format_exception_if_position_is_a_text() {
        thrown.expect(NumberFormatException.class);
        RuleFunction ruleFunction = RuleFunctionLeft.create();

        ruleFunction.evaluate(Arrays.asList("test_variable_one", "text"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionLeft.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
