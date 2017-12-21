package org.hisp.dhis.rules.functions;

import org.hamcrest.MatcherAssert;
import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import static java.util.Arrays.asList;

@RunWith(JUnit4.class)
public class RuleFunctionCeilShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_argument_rounded_up_to_nearest_whole_number() {
        RuleFunction ceilFunction = RuleFunctionCeil.create();

        MatcherAssert.assertThat(ceilFunction.evaluate(asList("0"), variableValues), is("0"));
        MatcherAssert.assertThat(ceilFunction.evaluate(asList("0.8"), variableValues), is("1"));
        MatcherAssert.assertThat(ceilFunction.evaluate(asList("5.1"), variableValues), is("6"));
        MatcherAssert.assertThat(ceilFunction.evaluate(asList("1"), variableValues), is("1"));
        MatcherAssert.assertThat(ceilFunction.evaluate(asList("-9.3"), variableValues), is("-9"));
        MatcherAssert.assertThat(ceilFunction.evaluate(asList("-5.9"), variableValues), is("-5"));
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCeil.create().evaluate(asList("5.9", "6.8"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCeil.create().evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCeil.create().evaluate(null, variableValues);
    }

}
