package org.hisp.dhis.rules.functions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static java.util.Arrays.asList;

import org.hisp.dhis.rules.RuleVariableValue;
import org.hisp.dhis.rules.RuleVariableValueBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RuleFunctionHasValueShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_false_for_non_existing_variable() {

        RuleFunction hasValueFunction = RuleFunctionHasValue.create();

        variableValues = givenAEmptyVariableValues();

        assertThat(hasValueFunction.evaluate(
                asList("nonexisting"), variableValues), is("false"));
    }

    @Test
    public void return_false_for_existing_variable_without_value() {
        RuleFunction hasValueFunction = RuleFunctionHasValue.create();

        String variableName = "non_value_var";

        variableValues = givenAVariableValuesAndOneWithoutValue(variableName);

        assertThat(hasValueFunction.evaluate(
                asList(variableName), variableValues), is("false"));
    }

    @Test
    public void return_true_for_existing_variable_with_value() {
        RuleFunction hasValueFunction = RuleFunctionHasValue.create();

        String variableName = "with_value_var";

        variableValues = givenAVariableValuesAndOneWithValue(variableName);

        assertThat(hasValueFunction.evaluate(
                asList(variableName), variableValues), is("true"));
    }

    @Test
    public void throw_illegal_argument_exception_when_variable_map_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionHasValue.create().evaluate(asList("variable_name"), null);
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionHasValue.create().evaluate(asList("variable_name", "6.8"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionHasValue.create().evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionHasValue.create().evaluate(null, variableValues);
    }

    private Map<String, RuleVariableValue> givenAEmptyVariableValues() {
        return new HashMap<>();
    }

    private Map<String, RuleVariableValue> givenAVariableValuesAndOneWithoutValue(
            String variableNameWithoutValue) {
        variableValues.put(variableNameWithoutValue, null);

        variableValues.put("test_variable_two",
                RuleVariableValueBuilder.create()
                        .withValue("Value two")
                        .build());

        return variableValues;
    }

    private Map<String, RuleVariableValue> givenAVariableValuesAndOneWithValue(
            String variableNameWithValue) {
        variableValues.put("test_variable_one", null);

        variableValues.put(variableNameWithValue,
                RuleVariableValueBuilder.create()
                        .withValue("Value two")
                        .build());

        return variableValues;
    }

}
