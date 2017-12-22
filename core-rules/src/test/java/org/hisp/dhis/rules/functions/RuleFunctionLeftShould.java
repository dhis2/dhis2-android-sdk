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

public class RuleFunctionLeftShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_empty_string_for_null_input() {
        RuleFunction leftFunction = RuleFunctionLeft.create();

        assertThat(leftFunction.evaluate(Arrays.asList(null, "0"), variableValues), is(""));
        assertThat(leftFunction.evaluate(Arrays.asList(null, "10"), variableValues), is(""));
        assertThat(leftFunction.evaluate(Arrays.asList(null, "-10"), variableValues), is(""));
    }

    @Test
    public void return_substring_of_first_argument_from_the_beginning() {
        RuleFunction leftFunction = RuleFunctionLeft.create();

        assertThat(leftFunction.evaluate(
                Arrays.asList("abcdef", "0"), variableValues), is(""));

        assertThat(leftFunction.evaluate(
                Arrays.asList("abcdef", "-5"), variableValues), is(""));

        assertThat(leftFunction.evaluate(
                Arrays.asList("abcdef", "2"), variableValues), is("ab"));

        assertThat(leftFunction.evaluate(
                Arrays.asList("abcdef", "30"), variableValues), is("abcdef"));
    }

    @Test
    public void throw_illegal_argument_exception_if_position_is_a_text() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionLeft.create();

        ruleFunction.evaluate(asList("test_variable_one", "text"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction leftFunction = RuleFunctionLeft.create();

        leftFunction.evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionLeft.create().evaluate(
                asList("cdcdcd", "2", "2016-01-01"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionLeft.create().evaluate(asList("cdcdcdcdc"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionLeft.create().evaluate(null, variableValues);
    }
}
