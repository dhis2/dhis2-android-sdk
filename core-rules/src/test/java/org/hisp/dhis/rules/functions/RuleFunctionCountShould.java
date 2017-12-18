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
    public void count_one_when_one_variable_value() {
        RuleFunction ruleFunction = RuleFunctionCount.create();
        RuleVariableValue variableValue = RuleVariableValue.create(
                "test_dataelement_one", RuleValueType.TEXT, Arrays.asList("test_value_one" ));
        RuleVariableValue variableValueTwo = RuleVariableValue.create(
                "test_dataelement_two", RuleValueType.TEXT, Arrays.asList("test_value_two" ));

        Map<String, RuleVariableValue> valueMap = new HashMap<>();
        valueMap.put("test_variable_one", variableValue);
        valueMap.put("test_variable_two", variableValueTwo);

        String result = ruleFunction.evaluate(Arrays.asList("test_variable_one"),valueMap);
        assertThat(result).isEqualTo("1");
    }

    @Test
    public void count_two_when_two_variable_values() {
        RuleFunction ruleFunction = RuleFunctionCount.create();
        RuleVariableValue variableValue = RuleVariableValue.create(
                "test_dataelement_one", RuleValueType.TEXT, Arrays.asList("test_value_one", "two" ));
        RuleVariableValue variableValueTwo = RuleVariableValue.create(
                "test_dataelement_two", RuleValueType.TEXT, Arrays.asList("test_value_two" ));

        Map<String, RuleVariableValue> valueMap = new HashMap<>();
        valueMap.put("test_variable_one", variableValue);
        valueMap.put("test_variable_two", variableValueTwo);

        String result = ruleFunction.evaluate(Arrays.asList("test_variable_one"),valueMap);
        assertThat(result).isEqualTo("2");
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionCount.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionCount.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
