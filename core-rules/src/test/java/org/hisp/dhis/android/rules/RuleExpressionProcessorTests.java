package org.hisp.dhis.android.rules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleExpressionProcessorTests {

    @Test
    public void monkeyTest() {
        String expression = "A{test_variable_one} <0 && C{test_variable_two} == '' && " +
                "V{test_variable_three} <0 && #{test_variable_four} == ''";
        RuleExpression ruleExpression = RuleExpression.from(expression);

        String result = RuleExpressionProcessor.from(ruleExpression)
                .bind("A{test_variable_one}", "1")
                .bind("C{test_variable_two}", "2")
                .bind("V{test_variable_three}", "3")
                .bind("#{test_variable_four}", "4")
                .build();

        System.out.println(result);
        assertThat(result).isEqualTo("1 <0 && 2 == '' && 3 <0 && 4 == ''");
    }
}
