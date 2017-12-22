package org.hisp.dhis.rules.functions;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RuleFunctionZpvcShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_count_of_non_negative_values_in_arguments() {
        RuleFunction zpvc = RuleFunctionZpvc.create();

        List<String> arguments = Arrays.asList("sxsx", null, "0", "1", "-1", "2", "-2", "3");

        assertThat(zpvc.evaluate(arguments, variableValues), is("4"));
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionZpvc.create().evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionZpvc.create().evaluate(null, variableValues);
    }
}
