package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RuleFunctionConcatenateShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void return_correct_values() {
        RuleFunction ruleFunction = RuleFunctionConcatenate.create();

        String result = ruleFunction.evaluate(Arrays.asList("w","o", "r", "d"),
                new HashMap<String, RuleVariableValue>());

        assertThat(result).isEqualTo("word");
    }

    @Test
    public void throw_illegal_argument_exception_if_only_one_parameter() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionConcatenate.create();

        ruleFunction.evaluate(Arrays.asList("w"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_null_pointer_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionConcatenate.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionConcatenate.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
