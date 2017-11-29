package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.HashMap;

public class RuleFunctionYearsBetweenShould {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void evaluate_correct_number_of_days() {
        RuleFunction yearsBetween = RuleFunctionYearsBetween.create();

        String years = yearsBetween.evaluate(Arrays.asList(
                "2010-10-15", "2010-10-22"), new HashMap<String, RuleVariableValue>());
        assertThat(years).isEqualTo("0");
    }

    @Test
    public void evaluate_correct_number_of_days_with_years_of_difference() {
        RuleFunction yearsBetween = RuleFunctionYearsBetween.create();

        String years = yearsBetween.evaluate(Arrays.asList(
                "2010-01-01", "2016-06-31"), new HashMap<String, RuleVariableValue>());
        assertThat(years).isEqualTo("6");
    }

    @Test
    public void thrown_illegal_argument_exception_when_evaluate_only_one_day() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionYearsBetween.create().evaluate(Arrays.asList("2016-01-01"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_when_evaluate_more_than_two_days() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionYearsBetween.create().evaluate(Arrays.asList("2016-01-01","2016-01-01","2016-01-01"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_when_evaluate_with_no_date_strings() {
        thrown.expect(RuntimeException.class);
        RuleFunctionYearsBetween.create().evaluate(Arrays.asList("one","two"),
                new HashMap<String, RuleVariableValue>());
    }
}
