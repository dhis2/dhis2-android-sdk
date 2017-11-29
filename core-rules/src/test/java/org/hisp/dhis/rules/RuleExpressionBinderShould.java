package org.hisp.dhis.rules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RuleExpressionBinderShould {

    @Mock
    private RuleExpression ruleExpression;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        String expression = "A{test_variable_one} <0 && C{test_variable_two} == '' && " +
                "V{test_variable_three} <0 && #{test_variable_four} == '' && " +
                "d2:floor(d2:ceil(3.8)) + d2:ceil(3.8) == 6 && " +
                "d2:hasValue(4.5)";

        Set<String> variables = new HashSet<>(Arrays.asList(
                "A{test_variable_one}",
                "C{test_variable_two}",
                "V{test_variable_three}",
                "#{test_variable_four}"
        ));

        Set<String> functions = new HashSet<>(Arrays.asList(
                "d2:ceil(3.8)",
                "d2:hasValue(4.5)"
        ));

        when(ruleExpression.expression()).thenReturn(expression);
        when(ruleExpression.variables()).thenReturn(variables);
        when(ruleExpression.functions()).thenReturn(functions);
    }

    @Test
    public void bind_values_correctly() {
        String result = RuleExpressionBinder.from(ruleExpression)
                .bindVariable("A{test_variable_one}", "1")
                .bindVariable("C{test_variable_two}", "2")
                .bindVariable("V{test_variable_three}", "3")
                .bindVariable("#{test_variable_four}", "4")
                .bindFunction("d2:hasValue(4.5)", "true")
                .bindFunction("d2:ceil(3.8)", "4")
                .build();

        assertThat(result).isEqualTo("1 <0 && 2 == '' && 3 <0 && 4 == '' && " +
                "d2:floor(4) + 4 == 6 && true");
    }

    @Test
    public void bind_values_for_duplicate_variable_references() {
        String expression = "A{test_variable_one} <0 && C{test_variable_two} == '' && " +
                "V{test_variable_three} <0 && #{test_variable_four} == '' && " +
                "A{test_variable_one} + C{test_variable_two} == 10 && " +
                "d2:floor(d2:ceil(3.8)) + d2:ceil(3.8) == 6 && " +
                "d2:hasValue(4.5) && d2:hasValue(4.5)";
        when(ruleExpression.expression()).thenReturn(expression);

        String result = RuleExpressionBinder.from(ruleExpression)
                .bindVariable("A{test_variable_one}", "1")
                .bindVariable("C{test_variable_two}", "2")
                .bindVariable("V{test_variable_three}", "3")
                .bindVariable("#{test_variable_four}", "4")
                .bindFunction("d2:hasValue(4.5)", "true")
                .bindFunction("d2:ceil(3.8)", "4")
                .build();

        assertThat(result).isEqualTo("1 <0 && 2 == '' && 3 <0 && 4 == '' && 1 + 2 == 10 && " +
                "d2:floor(4) + 4 == 6 && true && true");
    }

    @Test
    public void fail_when_no_variables_in_expression() {
        when(ruleExpression.expression()).thenReturn("1 < 0");
        when(ruleExpression.variables()).thenReturn(new HashSet<String>());
        when(ruleExpression.functions()).thenReturn(new HashSet<String>());

        String expression = RuleExpressionBinder.from(ruleExpression).build();
        assertThat(expression).isEqualTo("1 < 0");
    }

    @Test
    public void throw_illegal_argument_exception_when_bind_variable_passing_illegal_variable() {
        try {
            RuleExpressionBinder.from(ruleExpression).bindVariable("V{test_variable_five}", "3");
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void return_unmodified_string_if_no_variables_or_functions() {
        RuleExpression ruleExpression = RuleExpression.from("'test_expression'");
        RuleExpressionBinder ruleExpressionBinder = RuleExpressionBinder.from(ruleExpression);
        assertThat(ruleExpressionBinder.build()).isEqualTo("'test_expression'");
    }
}
