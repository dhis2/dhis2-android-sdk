package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.hisp.dhis.rules.models.RuleValueType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RuleFunctionCountIfValueShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void return_zero_if_variable_not_exist() {
        RuleFunction ruleFunction = RuleFunctionCountIfValue.create();

        HashMap<String, RuleVariableValue> ruleVariableValueHashMap = new HashMap<>();
        String result = ruleFunction.evaluate(Arrays.asList(""), ruleVariableValueHashMap);

        assertThat(result).isEqualTo("0");
    }

    @Test
    public void return_value_if_variable_exist() {
        RuleFunction ruleFunction = RuleFunctionCountIfValue.create();


        RuleVariableValue variableValue = RuleVariableValue.create(
                "test_variable_one", RuleValueType.TEXT, Arrays.asList("test_variable_one", "test_variable_one", "test_variable_one" ));
        RuleVariableValue variableValueTwo = RuleVariableValue.create(
                "test_variable_two", RuleValueType.TEXT, Arrays.asList("test_variable_two", "-2", "test_variable_two" ));
        RuleVariableValue variableValueThree = RuleVariableValue.create(
                "test_variable_three", RuleValueType.TEXT, Arrays.asList("-1", "2", "test_variable_three" ));
        RuleVariableValue variableValueFour = RuleVariableValue.create(
                "test_variable_three", RuleValueType.TEXT, Arrays.asList("test_variable_three", null, "test_variable_two" ));
        Map<String, RuleVariableValue> valueMap = new HashMap<>();
        valueMap.put("test_variable_one", variableValue);
        valueMap.put("test_variable_two", variableValueTwo);
        valueMap.put("test_variable_three", variableValueThree);
        valueMap.put("test_variable_four", variableValueFour);


        String result = ruleFunction.evaluate(Arrays.asList("test_variable_one"),valueMap);
        assertThat(result).isEqualTo("3");
        result = ruleFunction.evaluate(Arrays.asList("test_variable_two"),valueMap);
        assertThat(result).isEqualTo("2");
        result = ruleFunction.evaluate(Arrays.asList("test_variable_three"),valueMap);
        assertThat(result).isEqualTo("1");
        result = ruleFunction.evaluate(Arrays.asList("test_variable_four"),valueMap);
        assertThat(result).isEqualTo("1");
    }

    @Test
    public void throw_null_pointer_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionCountIfValue.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionCountIfValue.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
