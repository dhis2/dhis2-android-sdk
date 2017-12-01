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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RuleFunctionLastEventDateShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void return_correct_value() {
        RuleFunction ruleFunction = RuleFunctionLastEventDate.create();

        RuleVariable ruleVariableOne = RuleVariableCurrentEvent.create(
                "variable_name", "test_dataelement_one", RuleValueType.TEXT);

        Date eventDate = new Date();
        Date dueDate = new Date();

        RuleEvent currentEvent = RuleEvent.create("test_event_uid", "test_program_stage",
                RuleEvent.Status.ACTIVE, eventDate, dueDate, Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_one", "0.0")));

        Map<String, RuleVariableValue> valueMap = RuleVariableValueMapBuilder.target(currentEvent)
                .ruleVariables(Arrays.asList(ruleVariableOne))
                .build();


        String result = ruleFunction.evaluate(Arrays.asList("variable_name"), valueMap);
        assertThat(result).isEqualTo(new SimpleDateFormat("yyyy-MM-dd").format(eventDate));
        result = ruleFunction.evaluate(Arrays.asList("variable_name"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("");
    }

    @Test
    public void thrown_null_pointer_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionLastEventDate.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_pass_two_parameters() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionLastEventDate.create();

        ruleFunction.evaluate(Arrays.asList("test_variable_one", "variable"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionLastEventDate.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
