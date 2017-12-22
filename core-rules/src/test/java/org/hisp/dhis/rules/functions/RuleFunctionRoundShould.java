package org.hisp.dhis.rules.functions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static java.util.Arrays.asList;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RuleFunctionRoundShould {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_argument_rounded_up_to_nearest_whole_number() {
        RuleFunction roundFunction = RuleFunctionRound.create();

        assertThat(roundFunction.evaluate(asList("0"), variableValues), is("0"));
        assertThat(roundFunction.evaluate(asList("0.8"), variableValues), is("1"));
        assertThat(roundFunction.evaluate(asList("0.4999"), variableValues), is("0"));
        assertThat(roundFunction.evaluate(asList("0.5001"), variableValues), is("1"));
        assertThat(roundFunction.evaluate(asList("-9.3"), variableValues), is("-9"));
        assertThat(roundFunction.evaluate(asList("-9.8"), variableValues), is("-10"));
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionRound.create().evaluate(asList("5.9", "6.8"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionRound.create().evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionRound.create().evaluate(null, variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_is_non_number() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionRound.create().evaluate(asList("non number"), variableValues);
    }

}
