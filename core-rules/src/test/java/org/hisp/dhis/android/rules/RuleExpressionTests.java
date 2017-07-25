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
        assertThat(ruleExpression.variables()).contains("#{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("#{test_variable_two}");
        assertThat(ruleExpression.functions().size()).isEqualTo(0);
    }

    @Test
    public void fromShouldReturnExpressionWithAttributeVariables() {
        String expression = "A{test_variable_one} <0 && A{test_variable_two} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables()).contains("A{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("A{test_variable_two}");
        assertThat(ruleExpression.functions().size()).isEqualTo(0);
    }

    @Test
    public void fromShouldReturnExpressionWithEnvironmentVariables() {
        String expression = "V{test_variable_one} <0 && V{test_variable_two} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables()).contains("V{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("V{test_variable_two}");
        assertThat(ruleExpression.functions().size()).isEqualTo(0);
    }

    @Test
    public void fromShouldReturnExpressionWithConstantVariables() {
        String expression = "C{test_variable_one} <0 && C{test_variable_two} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables()).contains("C{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("C{test_variable_two}");
        assertThat(ruleExpression.functions().size()).isEqualTo(0);
    }

    @Test
    public void fromShouldReturnExpressionWithAllVariables() {
        String expression = "A{test_variable_one} <0 && C{test_variable_two} == '' && " +
                "V{test_variable_three} <0 && #{test_variable_four} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(4);
        assertThat(ruleExpression.variables()).contains("A{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("C{test_variable_two}");
        assertThat(ruleExpression.variables()).contains("V{test_variable_three}");
        assertThat(ruleExpression.variables()).contains("#{test_variable_four}");
        assertThat(ruleExpression.functions().size()).isEqualTo(0);
    }

    @Test
    public void fromShouldReturnExpressionWithAllVariablesWithoutDuplicates() {
        String expression = "A{test_variable_one} <0 && C{test_variable_two} == '' && " +
                "V{test_variable_three} <0 && #{test_variable_four} == '' && " +
                "A{test_variable_one} + C{test_variable_two} == 10";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(4);
        assertThat(ruleExpression.variables()).contains("A{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("C{test_variable_two}");
        assertThat(ruleExpression.variables()).contains("V{test_variable_three}");
        assertThat(ruleExpression.variables()).contains("#{test_variable_four}");
        assertThat(ruleExpression.functions().size()).isEqualTo(0);
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

    @Test
    public void fromShouldReturnExpressionWithImmutableFunctions() {
        RuleExpression ruleExpression = RuleExpression.from("");

        try {
            ruleExpression.functions().add("another_function");
            fail("UnsupportedOperationException was expected, but nothing was thrown.");
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // noop
        }
    }

    @Test
    public void fromShouldReturnExpressionWithFunctions() {
        RuleExpression ruleExpression = RuleExpression.from("d2:floor(16.4) + d2:ceil(8.7)");

        assertThat(ruleExpression.functions().size()).isEqualTo(2);
        assertThat(ruleExpression.functions()).contains("d2:floor(16.4)");
        assertThat(ruleExpression.functions()).contains("d2:ceil(8.7)");
    }

    @Test
    public void fromShouldReturnExpressionWithFunctionsWithoutDuplicates() {
        RuleExpression ruleExpression = RuleExpression.from("d2:floor(16.4) + " +
                "d2:ceil(8.7) + d2:ceil(8.7) + d2:floor(15.9)");

        assertThat(ruleExpression.functions().size()).isEqualTo(3);
        assertThat(ruleExpression.functions()).contains("d2:floor(16.4)");
        assertThat(ruleExpression.functions()).contains("d2:ceil(8.7)");
        assertThat(ruleExpression.functions()).contains("d2:floor(15.9)");
    }

    @Test
    public void fromShouldReturnExpressionWithInnerFunctionCallOnly() {
        RuleExpression ruleExpression = RuleExpression.from("d2:some(1, d2:ceil(8.7)) == 9 " +
                "&& d2:hasValue(A{test_variable_one}) " +
                "&& d2:ceil(d2:floor(d2:floor(9.8))) == A{test_variable_two}");

        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables()).contains("A{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("A{test_variable_two}");

        assertThat(ruleExpression.functions().size()).isEqualTo(2);
        assertThat(ruleExpression.functions()).contains("d2:ceil(8.7)");
        assertThat(ruleExpression.functions()).contains("d2:floor(9.8)");
    }

    @Test
    public void fromShouldReturnExpressionWithFunctionsAndVariables() {
        RuleExpression ruleExpression = RuleExpression.from("A{test_variable_one} <0 && " +
                "C{test_variable_two} == '' && (d2:floor(16.4) + d2:ceil(8.7)) == 20");

        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables()).contains("A{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("C{test_variable_two}");

        assertThat(ruleExpression.functions().size()).isEqualTo(2);
        assertThat(ruleExpression.functions()).contains("d2:floor(16.4)");
        assertThat(ruleExpression.functions()).contains("d2:ceil(8.7)");
    }
}
