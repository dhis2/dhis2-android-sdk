package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.Rule;
import org.hisp.dhis.android.rules.models.RuleEvent;
import org.hisp.dhis.android.rules.models.RuleVariable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleEngineTests {

    @Mock
    private RuleExpressionEvaluator evaluator;

    @Mock
    private Rule rule;

    @Mock
    private RuleVariable ruleVariable;

    @Mock
    private RuleEvent ruleEvent;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void builderShouldThrowException() {
        try {
            RuleEngine.builder(null);

            fail("IllegalArgumentException was expected, but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception.getMessage()).isEqualTo("evaluator == null");
        }
    }

    @Test
    public void builderShouldThrowExceptionIfNoRules() {
        try {
            RuleEngine.builder(evaluator)
                    .programRules(null)
                    .programRuleVariables(new ArrayList<RuleVariable>());

            fail("IllegalArgumentException was expected, but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception.getMessage()).isEqualTo("rules == null");
        }
    }

    @Test
    public void builderShouldThrowExceptionIfNoVariables() {
        try {
            RuleEngine.builder(evaluator)
                    .programRules(new ArrayList<Rule>())
                    .programRuleVariables(null);

            fail("IllegalArgumentException was expected, but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception.getMessage()).isEqualTo("ruleVariables == null");
        }
    }

    @Test
    public void programRulesShouldBeCopiedWithinBuilder() {
        List<Rule> rules = new ArrayList<>();
        rules.add(rule);

        RuleEngine.Builder builder = RuleEngine.builder(evaluator)
                .programRules(rules)
                .programRuleVariables(new ArrayList<RuleVariable>());

        rules.clear();
        RuleEngine ruleEngine = builder.build();

        assertThat(ruleEngine.programRules().size()).isEqualTo(1);
        assertThat(ruleEngine.programRules()).contains(rule);
    }

    @Test
    public void programRuleVariablesShouldBeCopiedWithinBuilder() {
        List<RuleVariable> ruleVariables = new ArrayList<>();
        ruleVariables.add(ruleVariable);

        RuleEngine.Builder builder = RuleEngine.builder(evaluator)
                .programRules(new ArrayList<Rule>())
                .programRuleVariables(ruleVariables);

        ruleVariables.clear();
        RuleEngine ruleEngine = builder.build();

        assertThat(ruleEngine.programRuleVariables().size()).isEqualTo(1);
        assertThat(ruleEngine.programRuleVariables()).contains(ruleVariable);
    }

    @Test
    public void eventsShouldBeCopiedWithinBuilder() {
        List<RuleEvent> ruleEvents = new ArrayList<>();
        ruleEvents.add(ruleEvent);

        RuleEngine.Builder builder = RuleEngine.builder(evaluator)
                .events(ruleEvents)
                .programRules(new ArrayList<Rule>())
                .programRuleVariables(new ArrayList<RuleVariable>());

        ruleEvents.clear();
        RuleEngine ruleEngine = builder.build();

        assertThat(ruleEngine.events().size()).isEqualTo(1);
        assertThat(ruleEngine.events()).contains(ruleEvent);
    }

    @Test
    public void programRulesShouldBeImmutable() {
        List<Rule> rules = new ArrayList<>();
        rules.add(rule);

        RuleEngine ruleEngine = RuleEngine.builder(evaluator)
                .programRules(rules)
                .programRuleVariables(new ArrayList<RuleVariable>())
                .build();

        try {
            ruleEngine.programRules().add(rule);
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void programRuleVariablesShouldBeImmutable() {
        List<RuleVariable> ruleVariables = new ArrayList<>();
        ruleVariables.add(ruleVariable);

        RuleEngine ruleEngine = RuleEngine.builder(evaluator)
                .programRules(new ArrayList<Rule>())
                .programRuleVariables(ruleVariables)
                .build();

        try {
            ruleEngine.programRuleVariables().add(ruleVariable);
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void eventsShouldBeImmutable() {
        List<RuleEvent> ruleEvents = new ArrayList<>();
        ruleEvents.add(ruleEvent);

        RuleEngine ruleEngine = RuleEngine.builder(evaluator)
                .events(ruleEvents)
                .programRules(new ArrayList<Rule>())
                .programRuleVariables(new ArrayList<RuleVariable>())
                .build();

        try {
            ruleEngine.events().add(ruleEvent);
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void builderShouldSupplyImmutableEmptyEventList() {
        RuleEngine ruleEngine = RuleEngine.builder(evaluator)
                .programRules(new ArrayList<Rule>())
                .programRuleVariables(new ArrayList<RuleVariable>())
                .build();

        assertThat(ruleEngine.events()).isNotNull();
        assertThat(ruleEngine.events()).isEmpty();

        try {
            ruleEngine.events().add(ruleEvent);
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void calculateShouldThrowIfNull() {
        RuleEngine ruleEngine = RuleEngine.builder(evaluator)
                .programRules(new ArrayList<Rule>())
                .programRuleVariables(new ArrayList<RuleVariable>())
                .build();

        try {
            ruleEngine.calculate(null);
            fail("IllegalArgumentException expected, but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception.getMessage()).isEqualTo("event == null");
        }
    }
}
