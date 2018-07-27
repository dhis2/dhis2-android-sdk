package org.hisp.dhis.rules;

import org.hisp.dhis.rules.models.Rule;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionAssign;
import org.hisp.dhis.rules.models.RuleEffect;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RuleEngineExecutionShould {

    @Mock
    private RuleExpressionEvaluator evaluator;

    @Mock
    private Rule rule;

    @Mock
    private List<RuleAction> actions;

    @Mock
    private HashMap<String, RuleVariableValue> variableMap;

    // Object to test
    private RuleEngineExecution ruleEngineExecution;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(rule.actions()).thenReturn(actions);
        when(actions.get(0)).thenReturn(RuleActionAssign.create("content", "data", "field"));

        ruleEngineExecution = new RuleEngineExecution(evaluator, Collections.singletonList(rule), variableMap);
    }

    @Test
    public void do_not_crash_on_malformed_expression() throws Exception {
        when(rule.condition()).thenReturn("d2:ceil(4 > 2");

        List<RuleEffect> effects = ruleEngineExecution.call();

        assertThat(effects).isEmpty();
    }

}
