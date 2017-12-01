package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ruleFunctionLengthShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void return_correct_values() {
        RuleFunction ruleFunction = RuleFunctionLength.create();
        String result = ruleFunction.evaluate(Arrays.asList("word"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("4");
        result = ruleFunction.evaluate(Arrays.asList("a"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("1");
        result = ruleFunction.evaluate(Arrays.asList(""),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("0");
    }

    @Test
    public void throw_null_pointer_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionLength.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_if_pass_two_parameters() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionLength.create();

        ruleFunction.evaluate(Arrays.asList("test_variable_one", "variable"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionLength.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
