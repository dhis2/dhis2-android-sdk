package org.hisp.dhis.rules.functions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleFunctionShould {

    @Test
    public void evaluate_correct_rule_function() {
        assertThat(RuleFunction.create("d2:daysBetween"))
                .isInstanceOf(RuleFunctionDaysBetween.class);
    }

    @Test
    public void evaluate_incorrect_rule_function_as_null() {
        assertThat(RuleFunction.create("d2:fake")).isNull();
    }
}
