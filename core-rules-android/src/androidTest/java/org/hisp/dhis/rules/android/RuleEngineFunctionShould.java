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

@RunWith(JUnit4.class)
public class RuleEngineFunctionShould {
    private Duktape duktape;

    @Before
    public void setUp() throws Exception {
        duktape = Duktape.create();
    }

    @Test
    public void return_true_if_evaluate_value_specified_has_value() throws Exception {
        RuleAction ruleAction = RuleActionDisplayKeyValuePair.createForFeedback(
                "test_action_content", "d2:hasValue('test_variable')");
        RuleVariable ruleVariable = RuleVariableCurrentEvent.create(
                "test_variable", "test_data_element", RuleValueType.TEXT);
        Rule rule = Rule.create(null, null, "true", Arrays.asList(ruleAction));

        RuleEngine ruleEngine = RuleEngineContext
                .builder(new DuktapeEvaluator(duktape))
                .rules(Arrays.asList(rule))
                .ruleVariables(Arrays.asList(ruleVariable))
                .build().toEngineBuilder()
                .build();

        RuleEvent ruleEvent = RuleEvent.create("test_event", "test_program_stage",
                RuleEvent.Status.ACTIVE, new Date(), new Date(), Arrays.asList(RuleDataValue.create(
                        new Date(), "test_program_stage", "test_data_element", "test_value")));
        List<RuleEffect> ruleEffects = ruleEngine.evaluate(ruleEvent).call();

        assertThat(ruleEffects.size()).isEqualTo(1);
        assertThat(ruleEffects.get(0).data()).isEqualTo("true");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @Test
    public void return_false_if_no_evaluate_value_specified_has_value() throws Exception {
        RuleAction ruleAction = RuleActionDisplayKeyValuePair.createForFeedback(
                "test_action_content", "d2:hasValue('test_variable')");
        RuleVariable ruleVariable = RuleVariableCurrentEvent.create(
                "test_variable", "test_data_element", RuleValueType.TEXT);
        Rule rule = Rule.create(null, null, "true", Arrays.asList(ruleAction));

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
    public void return_correct_diff_on_evaluate_days_between() throws Exception {
        RuleAction ruleAction = RuleActionDisplayKeyValuePair.createForFeedback(
                "test_action_content", "d2:daysBetween(#{test_var_one}, #{test_var_two})");
        RuleVariable ruleVariableOne = RuleVariableCurrentEvent.create(
                "test_var_one", "test_data_element_one", RuleValueType.TEXT);
        RuleVariable ruleVariableTwo = RuleVariableCurrentEvent.create(
                "test_var_two", "test_data_element_two", RuleValueType.TEXT);
        Rule rule = Rule.create(null, null, "true", Arrays.asList(ruleAction));

        RuleEngine ruleEngine = RuleEngineContext
                .builder(new DuktapeEvaluator(duktape))
                .rules(Arrays.asList(rule))
                .ruleVariables(Arrays.asList(ruleVariableOne, ruleVariableTwo))
                .build().toEngineBuilder()
                .build();

        RuleEvent ruleEvent = RuleEvent.create("test_event", "test_program_stage",
                RuleEvent.Status.ACTIVE, new Date(), new Date(), Arrays.asList(
                        RuleDataValue.create(new Date(), "test_program_stage", "test_data_element_one", "2017-01-01"),
                        RuleDataValue.create(new Date(), "test_program_stage", "test_data_element_two", "2017-02-01")));
        List<RuleEffect> ruleEffects = ruleEngine.evaluate(ruleEvent).call();

        assertThat(ruleEffects.size()).isEqualTo(1);
        assertThat(ruleEffects.get(0).data()).isEqualTo("31.0");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @Test
    public void return_expected_values_on_nested_functions_calls() throws Exception {
        RuleAction ruleAction = RuleActionDisplayKeyValuePair.createForFeedback(
                "test_action_content", "d2:floor(#{test_var_one} + d2:ceil(#{test_var_three})) " +
                        "/ 5 * d2:ceil(#{test_var_two})");

        RuleVariable ruleVariableOne = RuleVariableCurrentEvent.create(
                "test_var_one", "test_data_element_one", RuleValueType.NUMERIC);
        RuleVariable ruleVariableTwo = RuleVariableCurrentEvent.create(
                "test_var_two", "test_data_element_two", RuleValueType.NUMERIC);
        RuleVariable ruleVariableThree = RuleVariableCurrentEvent.create(
                "test_var_three", "test_data_element_three", RuleValueType.NUMERIC);

        Rule rule = Rule.create(null, null, "true", Arrays.asList(ruleAction));

        RuleEngine ruleEngine = RuleEngineContext
                .builder(new DuktapeEvaluator(duktape))
                .rules(Arrays.asList(rule))
                .ruleVariables(Arrays.asList(ruleVariableOne,
                        ruleVariableTwo, ruleVariableThree))
                .build().toEngineBuilder()
                .build();

        RuleEvent ruleEvent = RuleEvent.create("test_event", "test_program_stage",
                RuleEvent.Status.ACTIVE, new Date(), new Date(), Arrays.asList(
                        RuleDataValue.create(new Date(), "test_program_stage", "test_data_element_one", "19.9"),
                        RuleDataValue.create(new Date(), "test_program_stage", "test_data_element_two", "0.9"),
                        RuleDataValue.create(new Date(), "test_program_stage", "test_data_element_three", "10.6")));
        List<RuleEffect> ruleEffects = ruleEngine.evaluate(ruleEvent).call();

        assertThat(ruleEffects.size()).isEqualTo(1);
        assertThat(ruleEffects.get(0).data()).isEqualTo("6.0");
        assertThat(ruleEffects.get(0).ruleAction()).isEqualTo(ruleAction);
    }

    @After
    public void tearDown() throws Exception {
        duktape.close();
    }
}
