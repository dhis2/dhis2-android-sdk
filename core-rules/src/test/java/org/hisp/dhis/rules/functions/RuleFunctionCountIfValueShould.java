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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RuleFunctionCountIfValueShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_zero_for_non_existing_variable() {
        RuleFunction countIfValueFunction = RuleFunctionCountIfValue.create();

        variableValues = givenAEmptyVariableValues();

        assertThat(countIfValueFunction.evaluate(
                asList("non existing variable", "value"), variableValues), is("0"));
    }

    @Test
    public void return_zero_for_for_empty_value_to_compare() {
        RuleFunction countIfValueFunction = RuleFunctionCountIfValue.create();

        variableValues = givenAEmptyVariableValues();

        assertThat(countIfValueFunction.evaluate(
                asList("var1", null), variableValues), is("0"));

        assertThat(countIfValueFunction.evaluate(
                asList("var1", ""), variableValues), is("0"));
    }

    @Test
    public void return_zero_for_variable_without_values() {
        RuleFunction countIfValueFunction = RuleFunctionCountIfValue.create();

        String variableName = "non_value_var";

        variableValues = givenAVariableValuesAndOneWithoutValue(variableName);

        assertThat(countIfValueFunction.evaluate(
                asList(variableName, "valueToCompare"), variableValues), is("0"));
    }

    @Test
    public void return_size_of_matched_values_for_variable_with_value_and_candidates() {
        RuleFunction countIfValueFunction = RuleFunctionCountIfValue.create();

        String variableName = "with_value_var";
        String value = "valueA";

        variableValues = givenAVariableValuesAndOneWithTwoExpectedCountCandidates(
                variableName, value);

        assertThat(countIfValueFunction.evaluate(
                asList(variableName, value), variableValues), is("2"));
    }

    @Test
    public void return_zero_for_variable_with_no_matched_value_and_candidates() {
        RuleFunction countIfValueFunction = RuleFunctionCountIfValue.create();

        String variableName = "with_value_var";
        String value = "valueA";

        variableValues = givenAVariableValuesAndOneWithTwoExpectedCountCandidates(
                variableName, value);

        assertThat(countIfValueFunction.evaluate(
                asList(variableName, "NoMatchedValue"), variableValues), is("0"));
    }

    @Test
    public void return_one_matched_value_for_variable_with_value_and_without_candidates() {
        RuleFunction countIfValueFunction = RuleFunctionCountIfValue.create();

        String variableName = "with_value_var";
        String value = "valueA";

        variableValues = givenAVariableValuesAndOneWithUndefinedCandidates(
                variableName, value);

        assertThat(countIfValueFunction.evaluate(
                asList(variableName, value), variableValues), is("1"));
    }

    @Test
    public void return_zero_for_no_matched_variable_with_value_and_without_candidates() {
        RuleFunction countIfValueFunction = RuleFunctionCountIfValue.create();

        String variableName = "with_value_var";
        String value = "valueA";

        variableValues = givenAVariableValuesAndOneWithUndefinedCandidates(
                variableName, value);

        assertThat(countIfValueFunction.evaluate(
                asList(variableName, "NoMatchedValue"), variableValues), is("0"));
    }

    @Test
    public void throw_illegal_argument_exception_when_variable_map_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCountIfValue.create().evaluate(asList("variable_name", "Value_to_compare"),
                null);
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCountIfValue.create().evaluate(asList("variable_name", "ded", "5"),
                variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCountIfValue.create().evaluate(asList("variable_name"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_empty() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCountIfValue.create().evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCountIfValue.create().evaluate(null, variableValues);
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

    private Map<String, RuleVariableValue> givenAVariableValuesAndOneWithTwoExpectedCountCandidates(
            String variableNameWithValueAndCandidates, String valueToCompare) {
        variableValues.put("test_variable_one", null);

        variableValues.put(variableNameWithValueAndCandidates,
                RuleVariableValueBuilder.create()
                        .withValue(valueToCompare)
                        .withCandidates(Arrays.asList("one", valueToCompare, valueToCompare))
                        .build());

        return variableValues;
    }

    private Map<String, RuleVariableValue> givenAVariableValuesAndOneWithUndefinedCandidates(
            String variableNameWithValueAndNonCandidates, String valueToCompare) {
        variableValues.put("test_variable_one", null);

        variableValues.put(variableNameWithValueAndNonCandidates,
                RuleVariableValueBuilder.create()
                        .withValue(valueToCompare)
                        .build());

        return variableValues;
    }
}
