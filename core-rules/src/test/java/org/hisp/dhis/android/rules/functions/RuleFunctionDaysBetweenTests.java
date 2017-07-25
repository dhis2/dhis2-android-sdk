package org.hisp.dhis.android.rules.functions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleFunctionDaysBetweenTests {

    @Test
    public void evaluateMustReturnCorrectNumberOfDays() {
        RuleFunction daysBetween = RuleFunctionDaysBetween.create();

        String days = daysBetween.evaluate(Arrays.asList(
                "2016-01-01", "2016-01-31"));
        assertThat(days).isEqualTo("30");
    }

    @Test
    public void evaluateMustFailOnWrongArgumentCount() {
        try {
            RuleFunctionDaysBetween.create().evaluate(Arrays.asList("one"));
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }

        try {
            RuleFunctionDaysBetween.create().evaluate(Arrays.asList("one", "two", "three"));
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }
}
