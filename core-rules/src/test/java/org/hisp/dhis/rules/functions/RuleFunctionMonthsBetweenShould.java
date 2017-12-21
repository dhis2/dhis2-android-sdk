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

public class RuleFunctionMonthsBetweenShould {
    @Rule
    public ExpectedException thrown = ExpectedException.none();


    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_zero_if_some_date_is_not_present() {
        RuleFunction monthsBetween = RuleFunctionMonthsBetween.create();

        assertThat(
                monthsBetween.evaluate(Arrays.<String>asList(null, null), variableValues),
                is(("0")));
        assertThat(monthsBetween.evaluate(asList(null, ""), variableValues),
                is(("0")));
        assertThat(monthsBetween.evaluate(asList("", null), variableValues),
                is(("0")));
        assertThat(monthsBetween.evaluate(asList("", ""), variableValues), is(("0")));
    }

    @Test
    public void throw_illegal_argument_exception_if_first_date_is_invalid() {
        thrown.expect(IllegalArgumentException.class);

        RuleFunction monthsBetween = RuleFunctionMonthsBetween.create();

        monthsBetween.evaluate(asList("bad date", "2010-01-01"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_if_second_date_is_invalid() {
        thrown.expect(IllegalArgumentException.class);

        RuleFunction monthsBetween = RuleFunctionMonthsBetween.create();

        monthsBetween.evaluate(asList("2010-01-01", "bad date"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_if_first_and_second_date_is_invalid() {
        thrown.expect(RuntimeException.class);
        RuleFunctionMonthsBetween.create().evaluate(asList("bad date", "bad date"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void return_difference_of_months_of_two_dates() {
        RuleFunction monthsBetween = RuleFunctionMonthsBetween.create();

        assertThat(monthsBetween.evaluate(asList("2010-10-15", "2010-10-22"), variableValues),
                is(("0")));
        assertThat(monthsBetween.evaluate(asList("2010-09-30", "2010-10-31"), variableValues),
                is(("1")));
        assertThat(monthsBetween.evaluate(asList("2013-01-31", "2013-02-01"), variableValues),
                is(("1")));
        assertThat(monthsBetween.evaluate(asList("2016-01-01", "2016-07-31"), variableValues),
                is(("6")));
        assertThat(monthsBetween.evaluate(asList("2015-01-01", "2016-06-30"), variableValues),
                is(("17")));

        assertThat(monthsBetween.evaluate(asList("2010-10-22", "2010-10-15"), variableValues),
                is(("0")));
        assertThat(monthsBetween.evaluate(asList("2010-10-31", "2010-09-30"), variableValues),
                is(("-1")));
        assertThat(monthsBetween.evaluate(asList("2013-02-01", "2013-01-31"), variableValues),
                is(("-1")));
        assertThat(monthsBetween.evaluate(asList("2016-07-31", "2016-01-01"), variableValues),
                is(("-6")));
        assertThat(monthsBetween.evaluate(asList("2016-06-30", "2015-01-01"), variableValues),
                is(("-17")));
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionMonthsBetween.create().evaluate(
                Arrays.asList("2016-01-01", "2016-01-01", "2016-01-01"),
                variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionMonthsBetween.create().evaluate(Arrays.asList("2016-01-01"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionMonthsBetween.create().evaluate(null, variableValues);
    }
}
