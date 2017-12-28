package org.hisp.dhis.rules.android;

import com.squareup.duktape.Duktape;

import org.hisp.dhis.rules.RuleEngine;
import org.hisp.dhis.rules.RuleEngineContext;
import org.hisp.dhis.rules.models.Rule;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionDisplayKeyValuePair;
import org.hisp.dhis.rules.models.RuleDataValue;
import org.hisp.dhis.rules.models.RuleEffect;
import org.hisp.dhis.rules.models.RuleEvent;
import org.hisp.dhis.rules.models.RuleValueType;
import org.hisp.dhis.rules.models.RuleVariable;
import org.hisp.dhis.rules.models.RuleVariableCurrentEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

import android.support.test.filters.SmallTest;

@RunWith(JUnit4.class)
public class RuleEngineValueTypesShould {

    private Duktape duktape;

    @Before
    public void setUp() throws Exception {
        duktape = Duktape.create();
    }

    @After
    public void tearDown() throws Exception {
        duktape.close();
    }

    @Test
    @SmallTest
    public void fallback_to_default_boolean_value_when_boolean_variable_without_valueM() throws Exception {
        RuleAction ruleAction = RuleActionDisplayKeyValuePair
                .createForFeedback("test_action_content", "#{test_variable}");
        Rule rule = Rule.create(null, null, "true", Arrays.asList(ruleAction));
        RuleVariable ruleVariable = RuleVariableCurrentEvent
                .create("test_variable", "test_data_element", RuleValueType.BOOLEAN);

        RuleEngine ruleEngine = RuleEngineContext
                .builder(new DuktapeEvaluator(duktape))
                .rules(Arrays.asList(rule))
                .ruleVariables(Arrays.asList(ruleVariable))
                .build().toEngineBuilder()
                .build();

        RuleEvent ruleEvent = RuleEvent.create("test_event", "test_program_stage",
                RuleEvent.Status.ACTIVE, new Date(), new Date(), new ArrayList<RuleDataValue>());
        List<RuleEffect> ruleEffects = ruleEngine.evaluate(ruleEvent).call();

        assertThat(ruleEffects.size()).isEqualTo(1);
        assertThat(ruleEffects.get(0).data()).isEqualTo("false");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @Test
    @SmallTest
    public void fallback_to_default_numeric_value_on_numeric_variable_without_value() throws Exception {
        RuleAction ruleAction = RuleActionDisplayKeyValuePair
                .createForFeedback("test_action_content", "#{test_variable}");
        Rule rule = Rule.create(null, null, "true", Arrays.asList(ruleAction));
        RuleVariable ruleVariable = RuleVariableCurrentEvent
                .create("test_variable", "test_data_element", RuleValueType.NUMERIC);

        RuleEngine ruleEngine = RuleEngineContext
                .builder(new DuktapeEvaluator(duktape))
                .rules(Arrays.asList(rule))
                .ruleVariables(Arrays.asList(ruleVariable))
                .build().toEngineBuilder()
                .build();

        RuleEvent ruleEvent = RuleEvent.create("test_event", "test_program_stage",
                RuleEvent.Status.ACTIVE, new Date(), new Date(), new ArrayList<RuleDataValue>());
        List<RuleEffect> ruleEffects = ruleEngine.evaluate(ruleEvent).call();

        assertThat(ruleEffects.size()).isEqualTo(1);
        assertThat(ruleEffects.get(0).data()).isEqualTo("0.0");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @Test
    @SmallTest
    public void fallback_to_default_text_value_on_variable_without_value() throws Exception {
        RuleAction ruleAction = RuleActionDisplayKeyValuePair
                .createForFeedback("test_action_content", "#{test_variable}");
        Rule rule = Rule.create(null, null, "true", Arrays.asList(ruleAction));
        RuleVariable ruleVariable = RuleVariableCurrentEvent
                .create("test_variable", "test_data_element", RuleValueType.TEXT);

        RuleEngine ruleEngine = RuleEngineContext
                .builder(new DuktapeEvaluator(duktape))
                .rules(Arrays.asList(rule))
                .ruleVariables(Arrays.asList(ruleVariable))
                .build().toEngineBuilder()
                .build();

        RuleEvent ruleEvent = RuleEvent.create("test_event", "test_program_stage",
                RuleEvent.Status.ACTIVE, new Date(), new Date(), new ArrayList<RuleDataValue>());
        List<RuleEffect> ruleEffects = ruleEngine.evaluate(ruleEvent).call();

        assertThat(ruleEffects.size()).isEqualTo(1);
        assertThat(ruleEffects.get(0).data()).isEqualTo("");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }
}
