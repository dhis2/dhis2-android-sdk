package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleFunctionWeeksBetweenShould {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void evaluate_correct_number_of_days() {
        RuleFunction weeksBetween = RuleFunctionWeeksBetween.create();

        String weeks = weeksBetween.evaluate(Arrays.asList(
                "2016-01-01", "2016-01-31"), new HashMap<String, RuleVariableValue>());
        assertThat(weeks).isEqualTo("4");
    }

    @Test
    public void thrown_illegal_argument_exception_when_evaluate_only_one_day() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionWeeksBetween.create().evaluate(Arrays.asList("2016-01-01"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_when_evaluate_more_than_two_days() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionWeeksBetween.create().evaluate(Arrays.asList("2016-01-01","2016-01-01","2016-01-01"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_when_evaluate_with_no_date_strings() {
        thrown.expect(ParseException.class);
        RuleFunctionWeeksBetween.create().evaluate(Arrays.asList("one","two"),
                new HashMap<String, RuleVariableValue>());
    }
}
