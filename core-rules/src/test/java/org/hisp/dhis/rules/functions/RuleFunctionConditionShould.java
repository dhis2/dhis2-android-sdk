package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RuleFunctionConditionShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void return_correct_values_as_equals() {
        RuleFunction ruleFunction = RuleFunctionCondition.create();

        String result = ruleFunction.evaluate(Arrays.asList("word", "word"),
                new HashMap<String, RuleVariableValue>());

        assertThat(result).isEqualTo("true");
    }

    @Test
    public void return_incorrect_values_as_not_equals() {
        RuleFunction ruleFunction = RuleFunctionCondition.create();

        String result = ruleFunction.evaluate(Arrays.asList("word", "5"),
                new HashMap<String, RuleVariableValue>());

        assertThat(result).isEqualTo("false");
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionCondition.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionCondition.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
    @Test
    public void thrown_illegal_argument_exception_when_evaluate_only_one_condition() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCondition.create().evaluate(Arrays.asList("word"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_when_evaluate_more_than_two_conditions() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCondition.create().evaluate(Arrays.asList("word","23","word"),
                new HashMap<String, RuleVariableValue>());
    }

}
