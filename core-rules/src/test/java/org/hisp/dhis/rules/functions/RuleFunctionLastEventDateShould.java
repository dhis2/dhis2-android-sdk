package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.hisp.dhis.rules.models.RuleDataValue;
import org.hisp.dhis.rules.models.RuleEvent;
import org.hisp.dhis.rules.models.RuleValueType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RuleFunctionLastEventDateShould {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void return_correct_value() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        RuleFunction ruleFunction = RuleFunctionLastEventDate.create();
        RuleEvent ruleEvent = RuleEvent.create("test_event_uid", "program_stage_uid",
                RuleEvent.Status.ACTIVE,
                dateFormat.parse("1945-10-02"), dateFormat.parse("1946-11-03"), new ArrayList
                        <RuleDataValue>());
        RuleVariableValue ruleVariableValue = RuleVariableValue.create("", RuleValueType.TEXT,
                new ArrayList<String>());
        ruleVariableValue.setTarget(ruleEvent);
        Map<String, RuleVariableValue> ruleVariableValueHashMap =
                new HashMap<>();
        ruleVariableValueHashMap.put("test_event_rule", ruleVariableValue);
        String result = ruleFunction.evaluate(Arrays.asList("test_event_uid"),
                ruleVariableValueHashMap);
        assertThat(result).isEqualTo(
                new SimpleDateFormat(DATE_PATTERN, Locale.US).format(ruleEvent.eventDate()));
    }

    @Test
    public void throw_null_pointer_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionLastEventDate.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_if_pass_two_parameters() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionLastEventDate.create();

        ruleFunction.evaluate(Arrays.asList("test_variable_one", "variable"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionLastEventDate.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
