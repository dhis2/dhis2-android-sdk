package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.HashMap;

public class RuleFunctionMonthsBetweenShould {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void evaluate_correct_number_of_days() {
        RuleFunction monthsBetween = RuleFunctionMonthsBetween.create();

        String months = monthsBetween.evaluate(Arrays.asList(
                "2010-10-15", "2010-10-22"), new HashMap<String, RuleVariableValue>());
        assertThat(months).isEqualTo("0");
    }

    @Test
    public void evaluate_correct_number_of_days_with_months_of_difference() {
        RuleFunction monthsBetween = RuleFunctionMonthsBetween.create();

        String months = monthsBetween.evaluate(Arrays.asList(
                "2016-01-01", "2016-06-31"), new HashMap<String, RuleVariableValue>());
        assertThat(months).isEqualTo("6");
    }
    @Test
    public void evaluate_correct_number_of_days_with_years_of_difference() {
        RuleFunction monthsBetween = RuleFunctionMonthsBetween.create();

        String months = monthsBetween.evaluate(Arrays.asList(
                "2015-01-01", "2016-06-31"), new HashMap<String, RuleVariableValue>());
        assertThat(months).isEqualTo("18");
    }

    @Test
    public void throw_illegal_argument_exception_when_evaluate_only_one_day() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionMonthsBetween.create().evaluate(Arrays.asList("2016-01-01"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_when_evaluate_more_than_two_days() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionMonthsBetween.create().evaluate(Arrays.asList("2016-01-01","2016-01-01","2016-01-01"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_runtime_exception_when_evaluate_with_no_date_strings() {
        thrown.expect(RuntimeException.class);
        RuleFunctionMonthsBetween.create().evaluate(Arrays.asList("one","two"),
                new HashMap<String, RuleVariableValue>());
    }
}
