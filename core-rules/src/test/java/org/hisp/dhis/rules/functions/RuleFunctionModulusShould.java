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

public class RuleFunctionModulusShould {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_argument_rounded_down_to_nearest_whole_number() {
        RuleFunction modulusFunction = RuleFunctionModulus.create();

        assertThat(modulusFunction.evaluate(asList("0", "2"), variableValues), is("0"));
        assertThat(modulusFunction.evaluate(asList("11", "3"), variableValues), is("2"));
        assertThat(modulusFunction.evaluate(asList("-11", "3"), variableValues), is("-2"));
        assertThat(modulusFunction.evaluate(asList("11.5", "3.2"), variableValues), is("2"));
        assertThat(modulusFunction.evaluate(asList("-11.5", "3.2"), variableValues), is("-2"));
    }

    @Test
    public void throw_arithmetic_exception_if_zero_dividend() {
        thrown.expect(ArithmeticException.class);
        RuleFunctionModulus.create().evaluate(asList("2", "0"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionModulus.create().evaluate(asList("5.9", "6.8", "3.4"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionModulus.create().evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionModulus.create().evaluate(Arrays.<String>asList(null, null), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_not_number() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionModulus.create().evaluate(asList("bad number", "bad number"), variableValues);
    }

}
