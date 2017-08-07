package org.hisp.dhis.rules.android;

import android.support.test.runner.AndroidJUnit4;

import com.squareup.duktape.Duktape;

import org.hisp.dhis.rules.RuleEngine;
import org.hisp.dhis.rules.RuleEngineContext;
import org.hisp.dhis.rules.models.Rule;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionShowWarning;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class RuleEngineExpressionTests {
    private Duktape duktape;

    @Before
    public void setUp() throws Exception {
        duktape = Duktape.create();
    }

    @Test
    public void simpleWarningEffectMustBeProduced() throws Exception {
        RuleAction ruleAction = RuleActionShowWarning.create(
                "test_warning_message", null, "target_field");
        Rule rule = Rule.create(null, null, "true", Arrays.asList(ruleAction));

        RuleEngine ruleEngine = RuleEngineContext
                .builder(new DuktapeEvaluator(duktape))
                .rules(Arrays.asList(rule))
                .build().toEngineBuilder()
                .build();

        RuleEvent ruleEvent = RuleEvent.create("test_event", "test_program_stage",
                RuleEvent.Status.ACTIVE, new Date(), new Date(), Arrays.asList(RuleDataValue.create(
                        new Date(), "test_program_stage", "test_data_element", "test_value")));
        List<RuleEffect> ruleEffects = ruleEngine.evaluate(ruleEvent).call();

        assertThat(ruleEffects.size()).isEqualTo(1);
        assertThat(ruleEffects.get(0).data()).isEqualTo("");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @Test
    public void simpleExpressionMustBeProcessedCorrectly() throws Exception {
        RuleAction ruleAction = RuleActionShowWarning.create(
                "test_warning_message", null, "test_data_element");
        RuleVariable ruleVariable = RuleVariableCurrentEvent.create(
                "simple_variable", "test_data_element", RuleValueType.NUMERIC);
        Rule rule = Rule.create(null, null,
                "#{simple_variable} == 2", Arrays.asList(ruleAction));

        RuleEngine ruleEngine = RuleEngineContext
                .builder(new DuktapeEvaluator(duktape))
                .ruleVariables(Arrays.asList(ruleVariable))
                .rules(Arrays.asList(rule))
                .build().toEngineBuilder()
                .build();

        RuleEvent ruleEvent = RuleEvent.create("test_event", "test_program_stage",
                RuleEvent.Status.ACTIVE, new Date(), new Date(), Arrays.asList(RuleDataValue.create(
                        new Date(), "test_program_stage", "test_data_element", "2")));
        List<RuleEffect> ruleEffects = ruleEngine.evaluate(ruleEvent).call();

        assertThat(ruleEffects.size()).isEqualTo(1);
        assertThat(ruleEffects.get(0).data()).isEqualTo("");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @After
    public void tearDown() throws Exception {
        duktape.close();
    }
}
