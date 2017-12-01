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

public class RuleFunctionHasValueShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void return_true_if_value_exists() {
        RuleFunction ruleFunction = RuleFunctionHasValue.create();

        RuleVariableValue variableValue = RuleVariableValue.create(
                "test_variable_one", RuleValueType.TEXT, Arrays.asList("1", "2", "3" ));
        RuleVariableValue variableValueTwo = RuleVariableValue.create(
                "test_variable_two", RuleValueType.TEXT, Arrays.asList("-1", "-2", "3" ));
        RuleVariableValue variableValueThree = RuleVariableValue.create(
                "test_variable_three", RuleValueType.TEXT, Arrays.asList("-1", "2", "-3" ));
        RuleVariableValue variableValueFour = RuleVariableValue.create(
                "test_variable_three", RuleValueType.TEXT, Arrays.asList("-1", null, "3" ));
        Map<String, RuleVariableValue> valueMap = new HashMap<>();
        valueMap.put("test_variable_one", variableValue);
        valueMap.put("test_variable_two", variableValueTwo);
        valueMap.put("test_variable_three", variableValueThree);
        valueMap.put("value_name", variableValueFour);

        String result = ruleFunction.evaluate(Arrays.asList("value_name"),valueMap);
        assertThat(result).isEqualTo("true");
    }

    @Test
    public void return_false_if_value_not_exists() {
        RuleFunction ruleFunction = RuleFunctionHasValue.create();


        RuleVariableValue variableValue = RuleVariableValue.create(
                "test_variable_one", RuleValueType.TEXT, Arrays.asList("1", "2", "3" ));
        RuleVariableValue variableValueTwo = RuleVariableValue.create(
                "test_variable_two", RuleValueType.TEXT, Arrays.asList("-1", "-2", "3" ));
        RuleVariableValue variableValueThree = RuleVariableValue.create(
                "test_variable_three", RuleValueType.TEXT, Arrays.asList("-1", "2", "-3" ));
        RuleVariableValue variableValueFour = RuleVariableValue.create(
                "test_variable_three", RuleValueType.TEXT, Arrays.asList("-1", null, "3" ));
        Map<String, RuleVariableValue> valueMap = new HashMap<>();
        valueMap.put("test_variable_one", variableValue);
        valueMap.put("test_variable_two", variableValueTwo);
        valueMap.put("test_variable_three", variableValueThree);

        String result = ruleFunction.evaluate(Arrays.asList("value_name"), valueMap);
        assertThat(result).isEqualTo("false");
        result = ruleFunction.evaluate(Arrays.asList("value_name"), new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("false");
    }

    @Test
    public void thrown_null_pointer_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionHasValue.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionHasValue.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
