package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RuleFunctionAddDaysShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void return_correct_values() {
        RuleFunction ruleFunction = RuleFunctionAddDays.create();

        String result = ruleFunction.evaluate(Arrays.asList("2016-01-01", "6"),
                new HashMap<String, RuleVariableValue>());

        assertThat(result).isEqualTo("2016-01-07");
    }

    @Test
    public void throw_null_pointer_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionAddDays.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionAddDays.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
    @Test
    public void throw_illegal_argument_exception_when_evaluate_only_one_condition() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionAddDays.create().evaluate(Arrays.asList("2016-01-01"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_when_evaluate_more_than_two_conditions() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionAddDays.create().evaluate(Arrays.asList("2016-01-01","23","2016-01-01"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_runtime_exception_when_evaluate_string_instead_of_add_days() {
        thrown.expect(RuntimeException.class);
        RuleFunctionAddDays.create().evaluate(Arrays.asList("2016-01-01","word"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_runtime_exception_when_evaluate_string_instead_of_date() {
        thrown.expect(RuntimeException.class);
        RuleFunctionAddDays.create().evaluate(Arrays.asList("word","2"),
                new HashMap<String, RuleVariableValue>());
    }

}
