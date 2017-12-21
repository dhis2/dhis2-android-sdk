package org.hisp.dhis.rules.functions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static java.util.Arrays.asList;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RuleFunctionAddDaysShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_new_date_with_days_added() {
        RuleFunction addDaysFunction = RuleFunctionAddDays.create();

        assertThat(addDaysFunction.evaluate(asList("2011-01-01", "6"), variableValues),
                is(("2011-01-07")));
        assertThat(addDaysFunction.evaluate(asList("2010-10-10", "1"), variableValues),
                is(("2010-10-11")));
        assertThat(addDaysFunction.evaluate(asList("2010-10-31", "1"), variableValues),
                is(("2010-11-01")));
        assertThat(addDaysFunction.evaluate(asList("2010-12-01", "31"), variableValues),
                is(("2011-01-01")));
    }

    @Test
    public void throw_illegal_argument_exception_if_first_argument_is_invalid() {
        thrown.expect(IllegalArgumentException.class);

        RuleFunction addDaysFunction = RuleFunctionAddDays.create();

        addDaysFunction.evaluate(asList("bad date", "2"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_if_second_argument_is_invalid() {
        thrown.expect(IllegalArgumentException.class);

        RuleFunction addDaysFunction = RuleFunctionAddDays.create();

        addDaysFunction.evaluate(asList("2010-01-01", "bad number"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_if_first_and_second_argument_is_invalid() {
        thrown.expect(RuntimeException.class);
        RuleFunctionAddDays.create().evaluate(asList("bad date", "bad number"),
                new HashMap<String, RuleVariableValue>());
    }


    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionAddDays.create().evaluate(
                Arrays.asList("2016-01-01", "2", "2016-01-01"),
                variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionAddDays.create().evaluate(Arrays.asList("2016-01-01"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionAddDays.create().evaluate(null, variableValues);
    }
}
