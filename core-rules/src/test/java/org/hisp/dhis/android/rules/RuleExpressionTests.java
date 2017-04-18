package org.hisp.dhis.android.rules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleExpressionTests {

    @Test
    public void fromShouldReturnExpressionWithDataElementVariables() {
        String expression = "#{test_variable_one} <0 && #{test_variable_two} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables().get(0)).isEqualTo("#{test_variable_one}");
        assertThat(ruleExpression.variables().get(1)).isEqualTo("#{test_variable_two}");
    }

    @Test
    public void fromShouldReturnExpressionWithAttributeVariables() {
        String expression = "A{test_variable_one} <0 && A{test_variable_two} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables().get(0)).isEqualTo("A{test_variable_one}");
        assertThat(ruleExpression.variables().get(1)).isEqualTo("A{test_variable_two}");
    }

    @Test
    public void fromShouldReturnExpressionWithEnvironmentVariables() {
        String expression = "V{test_variable_one} <0 && V{test_variable_two} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables().get(0)).isEqualTo("V{test_variable_one}");
        assertThat(ruleExpression.variables().get(1)).isEqualTo("V{test_variable_two}");
    }

    @Test
    public void fromShouldReturnExpressionWithConstantVariables() {
        String expression = "C{test_variable_one} <0 && C{test_variable_two} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables().get(0)).isEqualTo("C{test_variable_one}");
        assertThat(ruleExpression.variables().get(1)).isEqualTo("C{test_variable_two}");
    }

    @Test
    public void fromShouldReturnExpressionWithAllVariables() {
        String expression = "A{test_variable_one} <0 && C{test_variable_two} == '' && " +
                "V{test_variable_three} <0 && #{test_variable_four} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(4);
        assertThat(ruleExpression.variables().get(0)).isEqualTo("A{test_variable_one}");
        assertThat(ruleExpression.variables().get(1)).isEqualTo("C{test_variable_two}");
        assertThat(ruleExpression.variables().get(2)).isEqualTo("V{test_variable_three}");
        assertThat(ruleExpression.variables().get(3)).isEqualTo("#{test_variable_four}");
    }

    @Test
    public void fromShouldIgnoreEmptyVariables() {
        String expression = "#{} && A{} && V{}";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(0);
    }

    @Test
    public void fromShouldPropagateExpressionToTheModel() {
        RuleExpression ruleExpression = RuleExpression.from("test_expression");
        assertThat(ruleExpression.expression()).isEqualTo("test_expression");
    }

    @Test
    public void fromShouldThrowOnNullExpression() {
        try {
            RuleExpression.from(null);
            fail("NullPointerException was expected, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void fromShouldReturnExpressionWithImmutableVariables() {
        RuleExpression ruleExpression = RuleExpression.from("");

        try {
            ruleExpression.variables().add("another_variable");
            fail("UnsupportedOperationException was expected, but nothing was thrown.");
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // noop
        }
    }
}
