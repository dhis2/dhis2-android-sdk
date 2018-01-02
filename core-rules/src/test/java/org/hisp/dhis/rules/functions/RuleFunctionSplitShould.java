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

public class RuleFunctionSplitShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_empty_string_for_null_inputs() {
        RuleFunction splitFunction = RuleFunctionSplit.create();

        assertThat(splitFunction.evaluate(asList(null, null, "0"), variableValues), is(""));
        assertThat(splitFunction.evaluate(asList("", null, "0"), variableValues), is(""));
        assertThat(splitFunction.evaluate(asList(null, "", "0"), variableValues), is(""));
    }

    @Test
    public void return_the_nth_field_of_the_splited_first_argument() {
        RuleFunction splitFunction = RuleFunctionSplit.create();

        assertThat(splitFunction.evaluate(asList("a,b,c", ",", "0"), variableValues), is("a"));
        assertThat(splitFunction.evaluate(asList("a,b,c", ",", "2"), variableValues), is("c"));
        assertThat(splitFunction.evaluate(asList("a,;b,;c", ",;", "1"), variableValues), is("b"));
    }

    @Test
    public void return_empty_string_if_field_index_is_out_of_bounds() {
        RuleFunction splitFunction = RuleFunctionSplit.create();

        assertThat(splitFunction.evaluate(asList("a,b,c", ",", "10"), variableValues), is(""));
        assertThat(splitFunction.evaluate(asList("a,b,c", ",", "-1"), variableValues), is(""));
    }

    @Test
    public void throw_illegal_argument_exception_if_position_is_a_text() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction splitFunction = RuleFunctionSplit.create();

        splitFunction.evaluate(
                Arrays.asList("test_variable_one", "variable", "text"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction splitFunction = RuleFunctionSplit.create();

        splitFunction.evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionSplit.create().evaluate(
                asList("test_variable_one", ",", "1", "2"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionSplit.create().evaluate(
                asList("test_variable_one", ","), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionSplit.create().evaluate(null, variableValues);
    }
}
