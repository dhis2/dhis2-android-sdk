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
import java.util.List;
import java.util.Map;

public class RuleFunctionCountIfZeroPosShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_zero_for_non_existing_variable() {
        RuleFunction ifZeroPosFunction = RuleFunctionCountIfZeroPos.create();

        variableValues = givenAEmptyVariableValues();

        assertThat(ifZeroPosFunction.evaluate(
                asList("nonexisting"), variableValues), is("0"));
    }

    @Test
    public void return_zero_for_variable_without_values() {
        RuleFunction ifZeroPosFunction = RuleFunctionCountIfZeroPos.create();

        String variableName = "non_value_var";

        variableValues = givenAVariableValuesAndOneWithoutValue(variableName);

        assertThat(ifZeroPosFunction.evaluate(
                asList(variableName), variableValues), is("0"));
    }

    @Test
    public void return_size_of_zero_or_positive_values_for_variable_with_value_and_candidates() {
        RuleFunction ifZeroPosFunction = RuleFunctionCountIfZeroPos.create();

        String variableName = "with_value_var";

        variableValues = givenAVariableValuesAndOneWithCandidates(
                variableName, Arrays.asList("0", "-1", "2"));

        assertThat(ifZeroPosFunction.evaluate(
                asList(variableName), variableValues), is("2"));
    }

    @Test
    public void
    return_zero_for_non_zero_or_positive_values_for_variable_with_value_and_candidates() {
        RuleFunction ifZeroPosFunction = RuleFunctionCountIfZeroPos.create();

        String variableName = "with_value_var";

        variableValues = givenAVariableValuesAndOneWithCandidates(
                variableName, Arrays.asList("ddcdc", "-1", null));

        assertThat(ifZeroPosFunction.evaluate(
                asList(variableName), variableValues), is("0"));
    }

    @Test
    public void return_one_zero_or_positive_value_for_variable_with_value_and_without_candidates() {
        RuleFunction ifZeroPosFunction = RuleFunctionCountIfZeroPos.create();

        String variableName = "with_value_var";

        variableValues = givenAVariableValuesAndOneWithUndefinedCandidates(variableName, "100");

        assertThat(ifZeroPosFunction.evaluate(
                asList(variableName), variableValues), is("1"));


        variableValues = givenAVariableValuesAndOneWithUndefinedCandidates(variableName, "0");

        assertThat(ifZeroPosFunction.evaluate(
                asList(variableName), variableValues), is("1"));
    }

    @Test
    public void
    return_zero_for_non_zero_or_positive_value_for_variable_with_value_and_without_candidates() {
        RuleFunction ifZeroPosFunction = RuleFunctionCountIfZeroPos.create();

        String variableName = "with_value_var";

        variableValues = givenAVariableValuesAndOneWithUndefinedCandidates(variableName, "-10");

        assertThat(ifZeroPosFunction.evaluate(
                asList(variableName), variableValues), is("0"));

        variableValues = givenAVariableValuesAndOneWithUndefinedCandidates(variableName, "dcdcdc");

        assertThat(ifZeroPosFunction.evaluate(
                asList(variableName), variableValues), is("0"));
    }

    @Test
    public void throw_illegal_argument_exception_when_variable_map_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCountIfZeroPos.create().evaluate(asList("variable_name"), null);
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCountIfZeroPos.create().evaluate(asList("variable_name", "6.8"),
                variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCountIfZeroPos.create().evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCountIfZeroPos.create().evaluate(null, variableValues);
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

    private Map<String, RuleVariableValue> givenAVariableValuesAndOneWithCandidates(
            String variableNameWithValueAndCandidates, List<String> candidates) {
        variableValues.put("test_variable_one", null);

        variableValues.put(variableNameWithValueAndCandidates,
                RuleVariableValueBuilder.create()
                        .withValue(candidates.get(0))
                        .withCandidates(candidates)
                        .build());

        return variableValues;
    }

    private Map<String, RuleVariableValue> givenAVariableValuesAndOneWithUndefinedCandidates(
            String variableNameWithValueAndNonCandidates, String value) {
        variableValues.put("test_variable_one", null);

        variableValues.put(variableNameWithValueAndNonCandidates,
                RuleVariableValueBuilder.create()
                        .withValue(value)
                        .build());

        return variableValues;
    }
}
