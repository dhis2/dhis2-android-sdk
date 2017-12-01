package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RuleFunctionRoundShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void return_correct_values() {
        RuleFunction ruleFunction = RuleFunctionRound.create();
        String result = ruleFunction.evaluate(Arrays.asList("10.9"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("11");
        result = ruleFunction.evaluate(Arrays.asList("10.5"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("11");
        result = ruleFunction.evaluate(Arrays.asList("10.49"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("10");
        result = ruleFunction.evaluate(Arrays.asList("10.1"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("10");
    }

    @Test
    public void throw_null_pointer_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionRound.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_number_format_exception_if_pass_text() {
        thrown.expect(NumberFormatException.class);
        RuleFunction ruleFunction = RuleFunctionRound.create();

        ruleFunction.evaluate(Arrays.asList("text"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_if_pass_two_parameters() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionRound.create();

        ruleFunction.evaluate(Arrays.asList("test_variable_one", "variable"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionRound.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
