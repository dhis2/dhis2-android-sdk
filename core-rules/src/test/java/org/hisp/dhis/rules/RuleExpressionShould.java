package org.hisp.dhis.rules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleExpressionShould {

    @Test
    public void return_expression_with_data_element_variables() {
        String expression = "#{test_variable_one} <0 && #{test_variable_two} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables()).contains("#{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("#{test_variable_two}");
        assertThat(ruleExpression.functions().size()).isEqualTo(0);
    }

    @Test
    public void return_expression_with_attribute_variables() {
        String expression = "A{test_variable_one} <0 && A{test_variable_two} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables()).contains("A{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("A{test_variable_two}");
        assertThat(ruleExpression.functions().size()).isEqualTo(0);
    }

    @Test
    public void return_expression_with_environment_variables() {
        String expression = "V{test_variable_one} <0 && V{test_variable_two} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables()).contains("V{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("V{test_variable_two}");
        assertThat(ruleExpression.functions().size()).isEqualTo(0);
    }

    @Test
    public void return_expression_with_constants_variables() {
        String expression = "C{test_variable_one} <0 && C{test_variable_two} == ''";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables()).contains("C{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("C{test_variable_two}");
        assertThat(ruleExpression.functions().size()).isEqualTo(0);
    }

    @Test
    public void return_expression_with_all_variables() {
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
    public void return_expression_with_all_variables_without_duplicates() {
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
    public void ignore_empty_variables() {
        String expression = "#{} && A{} && V{}";

        RuleExpression ruleExpression = RuleExpression.from(expression);
        assertThat(ruleExpression.variables().size()).isEqualTo(0);
    }

    @Test
    public void propagate_expression_to_model() {
        RuleExpression ruleExpression = RuleExpression.from("test_expression");
        assertThat(ruleExpression.expression()).isEqualTo("test_expression");
    }

    @Test
    public void throw_null_pointer_exception_if_expression_is_null() {
        try {
            RuleExpression.from(null);
            fail("NullPointerException was expected, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void throw_unsupported_operation_exception_when_try_to_modify_immutable_variables() {
        RuleExpression ruleExpression = RuleExpression.from("");

        try {
            ruleExpression.variables().add("another_variable");
            fail("UnsupportedOperationException was expected, but nothing was thrown.");
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // noop
        }
    }

    @Test
    public void throw_unsupported_operation_exception_when_try_to_modify_immutable_functions() {
        RuleExpression ruleExpression = RuleExpression.from("");

        try {
            ruleExpression.functions().add("another_function");
            fail("UnsupportedOperationException was expected, but nothing was thrown.");
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // noop
        }
    }

    @Test
    public void return_expression_with_functions() {
        RuleExpression ruleExpression = RuleExpression.from("d2:floor(16.4) + d2:ceil(8.7)");

        assertThat(ruleExpression.functions().size()).isEqualTo(2);
        assertThat(ruleExpression.functions()).contains("d2:floor(16.4)");
        assertThat(ruleExpression.functions()).contains("d2:ceil(8.7)");
    }

    @Test
    public void return_expression_with_functions_without_duplicates() {
        RuleExpression ruleExpression = RuleExpression.from("d2:floor(16.4) + " +
                "d2:ceil(8.7) + d2:ceil(8.7) + d2:floor(15.9)");

        assertThat(ruleExpression.functions().size()).isEqualTo(3);
        assertThat(ruleExpression.functions()).contains("d2:floor(16.4)");
        assertThat(ruleExpression.functions()).contains("d2:ceil(8.7)");
        assertThat(ruleExpression.functions()).contains("d2:floor(15.9)");
    }

    @Test
    public void return_expression_with_inner_function_call_only() {
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
    public void return_expression_with_functions_and_variables() {
        RuleExpression ruleExpression = RuleExpression.from("A{test_variable_one} <0 && " +
                "C{test_variable_two} == '' && (d2:floor(16.4) + d2:ceil(8.7)) == 20");

        assertThat(ruleExpression.variables().size()).isEqualTo(2);
        assertThat(ruleExpression.variables()).contains("A{test_variable_one}");
        assertThat(ruleExpression.variables()).contains("C{test_variable_two}");

        assertThat(ruleExpression.functions().size()).isEqualTo(2);
        assertThat(ruleExpression.functions()).contains("d2:floor(16.4)");
        assertThat(ruleExpression.functions()).contains("d2:ceil(8.7)");
    }

    @Test
    public void return_variable_name_after_unwrap() {
        assertThat(RuleExpression.unwrapVariableName("A{test_variable_one}"))
                .isEqualTo("test_variable_one");
        assertThat(RuleExpression.unwrapVariableName("C{test_variable_two}"))
                .isEqualTo("test_variable_two");
        assertThat(RuleExpression.unwrapVariableName("V{test_variable_three}"))
                .isEqualTo("test_variable_three");
        assertThat(RuleExpression.unwrapVariableName("#{test_variable_four}"))
                .isEqualTo("test_variable_four");
    }

    @Test
    public void return_unmodified_string_if_no_variables_or_functions() {
        RuleExpression ruleExpression = RuleExpression.from("'test_expression'");
        assertThat(ruleExpression.expression()).isEqualTo("'test_expression'");
    }

    @Test
    public void return_functions_call_with_parameter() {
        RuleExpression ruleExpression = RuleExpression.from("d2:hasValue('test_value')");

        assertThat(ruleExpression.expression()).isEqualTo("d2:hasValue('test_value')");
        assertThat(ruleExpression.variables().size()).isEqualTo(0);
        assertThat(ruleExpression.functions().size()).isEqualTo(1);
        assertThat(ruleExpression.functions()).contains("d2:hasValue('test_value')");
    }
}
