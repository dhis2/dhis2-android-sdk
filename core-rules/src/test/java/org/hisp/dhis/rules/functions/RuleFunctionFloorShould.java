package org.hisp.dhis.rules.functions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static java.util.Arrays.asList;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class RuleFunctionFloorShould {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_argument_rounded_down_to_nearest_whole_number() {
        RuleFunction floorFunction = RuleFunctionFloor.create();

        assertThat(floorFunction.evaluate(asList("0"), variableValues), is("0"));
        assertThat(floorFunction.evaluate(asList("0.8"), variableValues), is("0"));
        assertThat(floorFunction.evaluate(asList("1.0"), variableValues), is("1"));
        assertThat(floorFunction.evaluate(asList("-9.3"), variableValues), is("-10"));
        assertThat(floorFunction.evaluate(asList("5.9"), variableValues), is("5"));
        assertThat(floorFunction.evaluate(asList("5"), variableValues), is("5"));
        assertThat(floorFunction.evaluate(asList("-5"), variableValues), is("-5"));
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionFloor.create().evaluate(asList("5.9", "6.8"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionFloor.create().evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionFloor.create().evaluate(null, variableValues);
    }
}
