package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.Rule;
import org.hisp.dhis.android.rules.models.RuleVariable;
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
import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class RuleEngineContextTests {

    @Mock
    private RuleExpressionEvaluator ruleExpressionEvaluator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void builderShouldThrowOnNullExpressionEvaluator() {
        try {
            RuleEngineContext.builder(null);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void builderShouldThrowOnNullVariableList() {
        try {
            RuleEngineContext.builder(ruleExpressionEvaluator)
                    .rules(new ArrayList<Rule>())
                    .variables(null);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void builderShouldThrowOnNullRulesList() {
        try {
            RuleEngineContext.builder(ruleExpressionEvaluator)
                    .variables(new ArrayList<RuleVariable>())
                    .variables(null);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void builderShouldContainImmutableCopyOfRules() {
        RuleVariable ruleVariableOne = mock(RuleVariable.class);
        RuleVariable ruleVariableTwo = mock(RuleVariable.class);

        Rule ruleOne = mock(Rule.class);
        Rule ruleTwo = mock(Rule.class);

        List<RuleVariable> ruleVariables = new ArrayList<>();
        List<Rule> rules = new ArrayList<>();

        ruleVariables.add(ruleVariableOne);
        rules.add(ruleOne);

        RuleEngineContext ruleEngineContext = RuleEngineContext.builder(ruleExpressionEvaluator)
                .variables(ruleVariables)
                .rules(rules)
                .build();

        ruleVariables.add(ruleVariableTwo);
        rules.add(ruleTwo);

        assertThat(ruleEngineContext.expressionEvaluator()).isEqualTo(ruleExpressionEvaluator);
        assertThat(ruleEngineContext.variables().size()).isEqualTo(1);
        assertThat(ruleEngineContext.variables().get(0)).isEqualTo(ruleVariableOne);

        assertThat(ruleEngineContext.rules().size()).isEqualTo(1);
        assertThat(ruleEngineContext.rules().get(0)).isEqualTo(ruleOne);

        try {
            ruleEngineContext.variables().clear();
            fail("UnsupportedOperationException was expected, but nothing was thrown.");
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // noop
        }

        try {
            ruleEngineContext.rules().clear();
            fail("UnsupportedOperationException was expected, but nothing was thrown.");
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // noop
        }
    }

    @Test
    public void builderShouldThrowIfRulesListIsEmpty() {
        try {
            RuleEngineContext.builder(ruleExpressionEvaluator)
                    .rules(new ArrayList<Rule>())
                    .build();
            fail("IllegalArgumentException was expected, but nothing was thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void toEngineBuilderShouldReturnNewInstances() {
        RuleEngineContext ruleEngineContext = RuleEngineContext.builder(ruleExpressionEvaluator)
                .variables(Arrays.asList(mock(RuleVariable.class)))
                .rules(Arrays.asList(mock(Rule.class)))
                .build();

        RuleEngine.Builder ruleEngineBuilderOne = ruleEngineContext.toEngineBuilder();
        RuleEngine.Builder ruleEngineBuilderTwo = ruleEngineContext.toEngineBuilder();

        assertThat(ruleEngineBuilderOne).isNotEqualTo(ruleEngineBuilderTwo);
    }
}
