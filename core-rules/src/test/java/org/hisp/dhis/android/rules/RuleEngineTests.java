package org.hisp.dhis.android.rules;

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
    private ExpressionEvaluator evaluator;

    @Mock
    private ProgramRule programRule;

    @Mock
    private ProgramRuleVariable programRuleVariable;

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
                    .programRuleVariables(new ArrayList<ProgramRuleVariable>());

            fail("IllegalArgumentException was expected, but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception.getMessage()).isEqualTo("programRules == null");
        }
    }

    @Test
    public void builderShouldThrowExceptionIfNoVariables() {
        try {
            RuleEngine.builder(evaluator)
                    .programRules(new ArrayList<ProgramRule>())
                    .programRuleVariables(null);

            fail("IllegalArgumentException was expected, but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception.getMessage()).isEqualTo("programRuleVariables == null");
        }
    }

    @Test
    public void programRulesShouldBeCopiedWithinBuilder() {
        List<ProgramRule> programRules = new ArrayList<>();
        programRules.add(programRule);

        RuleEngine.Builder builder = RuleEngine.builder(evaluator)
                .programRules(programRules)
                .programRuleVariables(new ArrayList<ProgramRuleVariable>());

        programRules.clear();
        RuleEngine ruleEngine = builder.build();

        assertThat(ruleEngine.programRules().size()).isEqualTo(1);
        assertThat(ruleEngine.programRules()).contains(programRule);
    }

    @Test
    public void programRuleVariablesShouldBeCopiedWithinBuilder() {
        List<ProgramRuleVariable> programRuleVariables = new ArrayList<>();
        programRuleVariables.add(programRuleVariable);

        RuleEngine.Builder builder = RuleEngine.builder(evaluator)
                .programRules(new ArrayList<ProgramRule>())
                .programRuleVariables(programRuleVariables);

        programRuleVariables.clear();
        RuleEngine ruleEngine = builder.build();

        assertThat(ruleEngine.programRuleVariables().size()).isEqualTo(1);
        assertThat(ruleEngine.programRuleVariables()).contains(programRuleVariable);
    }

    @Test
    public void programRulesShouldBeImmutable() {
        List<ProgramRule> programRules = new ArrayList<>();
        programRules.add(programRule);

        RuleEngine ruleEngine = RuleEngine.builder(evaluator)
                .programRules(programRules)
                .programRuleVariables(new ArrayList<ProgramRuleVariable>())
                .build();

        try {
            ruleEngine.programRules().add(programRule);
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void programRuleVariablesShouldBeImmutable() {
        List<ProgramRuleVariable> programRuleVariables = new ArrayList<>();
        programRuleVariables.add(programRuleVariable);

        RuleEngine ruleEngine = RuleEngine.builder(evaluator)
                .programRules(new ArrayList<ProgramRule>())
                .programRuleVariables(programRuleVariables)
                .build();

        try {
            ruleEngine.programRuleVariables().add(programRuleVariable);
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }
}
