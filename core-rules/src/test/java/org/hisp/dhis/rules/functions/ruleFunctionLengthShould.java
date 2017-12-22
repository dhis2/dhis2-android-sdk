package org.hisp.dhis.rules.functions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static java.util.Arrays.asList;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ruleFunctionLengthShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_length_of_argument() {
        RuleFunction lengthFunction = RuleFunctionLength.create();

        assertThat(lengthFunction.evaluate(Arrays.asList("abc"), variableValues),
                is("3"));

        assertThat(lengthFunction.evaluate(Arrays.asList("abcdef"), variableValues),
                is("6"));
    }

    @Test
    public void return_zero_for_empty_argument() {
        RuleFunction lengthFunction = RuleFunctionLength.create();

        assertThat(lengthFunction.evaluate(Arrays.asList(""), variableValues),
                is("0"));
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction lengthFunction = RuleFunctionLength.create();

        lengthFunction.evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionLength.create().evaluate(
                asList("cdcdcd", "2"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionLength.create().evaluate(null, variableValues);
    }

}
