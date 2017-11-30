package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.hisp.dhis.rules.RuleVariableValueMapBuilder;
import org.hisp.dhis.rules.models.RuleDataValue;
import org.hisp.dhis.rules.models.RuleEvent;
import org.hisp.dhis.rules.models.RuleValueType;
import org.hisp.dhis.rules.models.RuleVariable;
import org.hisp.dhis.rules.models.RuleVariableCurrentEvent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RuleFunctionCountShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void return_zero_if_variable_not_exist() {
        RuleFunction ruleFunction = RuleFunctionCount.create();

        HashMap<String, RuleVariableValue> ruleVariableValueHashMap = new HashMap<>();
        String result = ruleFunction.evaluate(Arrays.asList(""), ruleVariableValueHashMap);

        assertThat(result).isEqualTo("0");
    }

    @Test
    public void return_value_if_variable_exist() {
        RuleFunction ruleFunction = RuleFunctionCount.create();

        RuleVariable ruleVariableOne = RuleVariableCurrentEvent.create(
                "test_variable_one", "test_dataelement_one", RuleValueType.TEXT);
        RuleVariable ruleVariableTwo = RuleVariableCurrentEvent.create(
                "test_variable_two", "test_dataelement_two", RuleValueType.TEXT);

        Date eventDate = new Date();
        Date dueDate = new Date();

        RuleEvent currentEvent = RuleEvent.create("test_event_uid", "test_program_stage",
                RuleEvent.Status.ACTIVE, eventDate, dueDate, Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_one"),
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_two")));

        Map<String, RuleVariableValue> valueMap = RuleVariableValueMapBuilder.target(currentEvent)
                .ruleVariables(Arrays.asList(ruleVariableOne, ruleVariableTwo))
                .build();


        String result = ruleFunction.evaluate(Arrays.asList("test_variable_one"),valueMap);
        assertThat(result).isEqualTo("1");
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionCount.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionCount.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
