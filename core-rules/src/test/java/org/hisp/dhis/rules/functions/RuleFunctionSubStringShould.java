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

public class RuleFunctionSubStringShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_empty_string_for_null_inputs() {
        RuleFunction subStringFunction = RuleFunctionSubString.create();

        assertThat(subStringFunction.evaluate(asList(null, "0", "0"), variableValues), is(""));
        assertThat(subStringFunction.evaluate(asList(null, "0", "10"), variableValues), is(""));
    }

    @Test
    public void return_substring_from_start_index_to_end_index_of_input_string() {
        RuleFunction subStringFunction = RuleFunctionSubString.create();

        assertThat(subStringFunction.evaluate(
                asList("abcdef", "0", "0"), variableValues), is(""));

        assertThat(subStringFunction.evaluate(
                asList("abcdef", "0", "1"), variableValues), is("a"));

        assertThat(subStringFunction.evaluate(
                asList("abcdef", "-10", "1"), variableValues), is("a"));

        assertThat(subStringFunction.evaluate(
                asList("abcdef", "2", "4"), variableValues), is("cd"));

        assertThat(subStringFunction.evaluate(
                asList("abcdef", "2", "10"), variableValues), is("cdef"));
    }

    @Test
    public void throw_illegal_argument_exception_if_start_index_is_a_text() {
        thrown.expect(IllegalArgumentException.class);

        RuleFunctionSubString.create().evaluate(
                Arrays.asList("test_variable_one", "variable", "3"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_if_end_index_is_a_text() {
        thrown.expect(IllegalArgumentException.class);

        RuleFunctionSubString.create().evaluate(
                Arrays.asList("test_variable_one", "3", "ede"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionSubString.create().evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionSubString.create().evaluate(
                asList("test_variable_one", "1", "2", "4"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionSubString.create().evaluate(
                asList("test_variable_one", "0"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionSubString.create().evaluate(null, variableValues);
    }
}
