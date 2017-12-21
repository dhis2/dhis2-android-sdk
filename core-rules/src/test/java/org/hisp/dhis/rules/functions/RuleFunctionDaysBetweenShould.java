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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class RuleFunctionDaysBetweenShould {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_zero_if_some_date_is_not_present() {
        RuleFunction daysBetween = RuleFunctionDaysBetween.create();

        assertThat(daysBetween.evaluate(Arrays.<String>asList(null, null), variableValues),
                is(("0")));
        assertThat(daysBetween.evaluate(asList(null, ""), variableValues), is(("0")));
        assertThat(daysBetween.evaluate(asList("", null), variableValues), is(("0")));
        assertThat(daysBetween.evaluate(asList("", ""), variableValues), is(("0")));
    }

    @Test
    public void throw_illegal_argument_exception_if_first_date_is_invalid() {
        thrown.expect(IllegalArgumentException.class);

        RuleFunction daysBetween = RuleFunctionDaysBetween.create();

        daysBetween.evaluate(asList("bad date", "2010-01-01"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_if_second_date_is_invalid() {
        thrown.expect(IllegalArgumentException.class);

        RuleFunction daysBetween = RuleFunctionDaysBetween.create();

        daysBetween.evaluate(asList("2010-01-01", "bad date"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_if_first_and_second_date_is_invalid() {
        thrown.expect(RuntimeException.class);
        RuleFunctionDaysBetween.create().evaluate(asList("bad date", "bad date"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void evaluate_correct_number_of_days() {
        RuleFunction daysBetween = RuleFunctionDaysBetween.create();

        assertThat(daysBetween.evaluate(asList("2010-10-15", "2010-10-20"), variableValues),
                is(("5")));
        assertThat(daysBetween.evaluate(asList("2010-09-30", "2010-10-15"), variableValues),
                is(("15")));
        assertThat(daysBetween.evaluate(asList("2010-12-31", "2011-01-01"), variableValues),
                is(("1")));

        assertThat(daysBetween.evaluate(asList("2010-10-20", "2010-10-15"), variableValues),
                is(("-5")));
        assertThat(daysBetween.evaluate(asList("2010-10-15", "2010-09-30"), variableValues),
                is(("-15")));
        assertThat(daysBetween.evaluate(asList("2011-01-01", "2010-12-31"), variableValues),
                is(("-1")));
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionDaysBetween.create().evaluate(
                Arrays.asList("2016-01-01", "2016-01-01", "2016-01-01"),
                variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionDaysBetween.create().evaluate(Arrays.asList("2016-01-01"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionDaysBetween.create().evaluate(null, variableValues);
    }
}
