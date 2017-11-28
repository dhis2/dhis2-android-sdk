package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleFunctionDaysBetweenShould {

    @Test
    public void evaluate_correct_number_of_days() {
        RuleFunction daysBetween = RuleFunctionDaysBetween.create();

        String days = daysBetween.evaluate(Arrays.asList(
                "2016-01-01", "2016-01-31"), new HashMap<String, RuleVariableValue>());
        assertThat(days).isEqualTo("30");
    }

    @Test
    public void throw_illegal_argument_exception_on_wrong_argument_count() {
        try {
            RuleFunctionDaysBetween.create().evaluate(Arrays.asList("one"),
                    new HashMap<String, RuleVariableValue>());
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }

        try {
            RuleFunctionDaysBetween.create().evaluate(Arrays.asList("one", "two", "three"),
                    new HashMap<String, RuleVariableValue>());
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }
}
