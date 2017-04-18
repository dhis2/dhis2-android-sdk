package org.hisp.dhis.android.rules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RuleExpressionBinderTests {

    @Mock
    private RuleExpression ruleExpression;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        String expression = "A{test_variable_one} <0 && C{test_variable_two} == '' && " +
                "V{test_variable_three} <0 && #{test_variable_four} == ''";

        List<String> variables = Arrays.asList(
                "A{test_variable_one}",
                "C{test_variable_two}",
                "V{test_variable_three}",
                "#{test_variable_four}"
        );

        when(ruleExpression.expression()).thenReturn(expression);
        when(ruleExpression.variables()).thenReturn(variables);
    }

    @Test
    public void buildShouldBindValuesCorrectly() {
        String result = RuleExpressionBinder.from(ruleExpression)
                .bind("A{test_variable_one}", "1")
                .bind("C{test_variable_two}", "2")
                .bind("V{test_variable_three}", "3")
                .bind("#{test_variable_four}", "4")
                .build();

        assertThat(result).isEqualTo("1 <0 && 2 == '' && 3 <0 && 4 == ''");
    }

    @Test
    public void buildShouldNotFailIfNoVariablesInExpression() {
        when(ruleExpression.expression()).thenReturn("1 < 0");
        when(ruleExpression.variables()).thenReturn(new ArrayList<String>());

        String expression = RuleExpressionBinder.from(ruleExpression).build();
        assertThat(expression).isEqualTo("1 < 0");
    }

    @Test
    public void buildShouldThrowIfNotEnoughValues() {
        try {
            RuleExpressionBinder.from(ruleExpression)
                    .bind("A{test_variable_one}", "1")
                    .bind("C{test_variable_two}", "2")
                    .bind("V{test_variable_three}", "3")
                    .build();
            fail("IllegalStateException was expected, but nothing was thrown.");
        } catch (IllegalStateException illegalStateException) {
            // noop
        }
    }

    @Test
    public void buildShouldThrowIfPassingIllegalVariable() {
        try {
            RuleExpressionBinder.from(ruleExpression).bind("V{test_variable_five}", "3");
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }
}
