package org.hisp.dhis.rules.android;

import android.support.test.runner.AndroidJUnit4;

import com.squareup.duktape.Duktape;

import org.hisp.dhis.rules.RuleEngine;
import org.hisp.dhis.rules.RuleEngineContext;
import org.hisp.dhis.rules.models.Rule;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionAssign;
import org.hisp.dhis.rules.models.RuleActionCreateEvent;
import org.hisp.dhis.rules.models.RuleActionDisplayKeyValuePair;
import org.hisp.dhis.rules.models.RuleActionDisplayText;
import org.hisp.dhis.rules.models.RuleActionErrorOnCompletion;
import org.hisp.dhis.rules.models.RuleActionHideField;
import org.hisp.dhis.rules.models.RuleActionHideSection;
import org.hisp.dhis.rules.models.RuleActionSetMandatoryField;
import org.hisp.dhis.rules.models.RuleActionShowError;
import org.hisp.dhis.rules.models.RuleActionShowWarning;
import org.hisp.dhis.rules.models.RuleActionWarningOnCompletion;
import org.hisp.dhis.rules.models.RuleDataValue;
import org.hisp.dhis.rules.models.RuleEffect;
import org.hisp.dhis.rules.models.RuleEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

// ToDo: function tests (check that function invocations are producing expected values; check nested function invocation)
// ToDo: various source type tests (referencing variables from different events)
@RunWith(AndroidJUnit4.class)
public class RuleEngineEffectTypesShould {
    private Duktape duktape;

    @Before
    public void setUp() throws Exception {
        duktape = Duktape.create();
    }

    @Test
    public void return_assigned_effect_when_create_simple_condition() throws Exception {
        RuleAction ruleAction = RuleActionAssign.create(
                "test_action_content", "\'test_string\'", "test_data_element");
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
        assertThat(ruleEffects.get(0).data()).isEqualTo("test_string");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @Test
    public void return_assigned_effect_when_create_simple_event() throws Exception {
        RuleAction ruleAction = RuleActionCreateEvent.create(
                "test_action_content", "'event_uid;test_data_value_one'", "test_program_stage");
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
        assertThat(ruleEffects.get(0).data()).isEqualTo("event_uid;test_data_value_one");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @Test
    public void return_display_key_value_pair_effect_after_create_simple_condition() throws Exception {
        RuleAction ruleAction = RuleActionDisplayKeyValuePair.createForFeedback(
                "test_action_content", "2 + 2");
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
        assertThat(ruleEffects.get(0).data()).isEqualTo("4.0");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @Test
    public void return_display_text_effect_after_create_simple_condition() throws Exception {
        RuleAction ruleAction = RuleActionDisplayText.createForFeedback(
                "test_action_content", "2 + 2");
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
        assertThat(ruleEffects.get(0).data()).isEqualTo("4.0");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @Test
    public void return_error_effect_after_create_simple_condition() throws Exception {
        RuleAction ruleAction = RuleActionErrorOnCompletion.create(
                "test_action_content", "2 + 2", "test_data_element");
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
        assertThat(ruleEffects.get(0).data()).isEqualTo("4.0");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @Test
    public void return_hide_field_effect_after_create_simple_condition() throws Exception {
        RuleAction ruleAction = RuleActionHideField.create(
                "test_action_content", "test_data_element");
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
    public void return_hide_section_effect_after_create_simple_condition() throws Exception {
        RuleAction ruleAction = RuleActionHideSection.create("test_section");
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
    public void return_set_mandatory_effect_after_create_simple_condition() throws Exception {
        RuleAction ruleAction = RuleActionSetMandatoryField.create("test_data_element");
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
    public void return_warning_effect_after_create_simple_condition() throws Exception {
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
    public void return_on_error_effect_after_create_simple_condition() throws Exception {
        RuleAction ruleAction = RuleActionShowError.create(
                "test_error_message", "2 + 2", "target_field");
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
        assertThat(ruleEffects.get(0).data()).isEqualTo("4.0");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @Test
    public void return_on_completion_effect_after_create_simple_condition() throws Exception {
        RuleAction ruleAction = RuleActionWarningOnCompletion.create(
                "test_warning_message", "2 + 2", "target_field");
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
        assertThat(ruleEffects.get(0).data()).isEqualTo("4.0");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @After
    public void tearDown() throws Exception {
        duktape.close();
    }
}
