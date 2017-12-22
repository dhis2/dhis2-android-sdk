package org.hisp.dhis.rules.functions;

import static org.hamcrest.CoreMatchers.is;

import static java.util.Arrays.asList;

import org.hamcrest.MatcherAssert;
import org.hisp.dhis.rules.RuleVariableValue;
import org.hisp.dhis.rules.RuleVariableValueBuilder;
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

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_zero_for_non_existing_variable() {
        RuleFunction countFunction = RuleFunctionCount.create();

        variableValues = givenAEmptyVariableValues();

        MatcherAssert.assertThat(countFunction.evaluate(
                asList("nonexisting"), variableValues), is("0"));
    }

    @Test
    public void return_zero_for_variable_without_values() {
        RuleFunction countFunction = RuleFunctionCount.create();

        String variableName = "non_value_var";

        variableValues = givenAVariableValuesAndOneWithoutValue(variableName);

        MatcherAssert.assertThat(countFunction.evaluate(
                asList(variableName), variableValues), is("0"));
    }

    @Test
    public void return_size_of_values_for_variable_with_value_and_candidates() {
        RuleFunction countFunction = RuleFunctionCount.create();

        String variableName = "with_value_var";

        variableValues = givenAVariableValuesAndOneWithTwoCandidates(variableName);

        MatcherAssert.assertThat(countFunction.evaluate(
                asList(variableName), variableValues), is("2"));
    }

    @Test
    public void return_one_for_variable_with_value_and_without_candidates() {
        RuleFunction countFunction = RuleFunctionCount.create();

        String variableName = "with_value_var";

        variableValues = givenAVariableValuesAndOneWithUndefinedCandidates(variableName);

        MatcherAssert.assertThat(countFunction.evaluate(
                asList(variableName), variableValues), is("1"));
    }

    @Test
    public void throw_illegal_argument_exception_when_variable_map_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCount.create().evaluate(asList("variable_name"), null);
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCount.create().evaluate(asList("variable_name", "6.8"), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCount.create().evaluate(new ArrayList<String>(), variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionCount.create().evaluate(null, variableValues);
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

    private Map<String, RuleVariableValue> givenAVariableValuesAndOneWithTwoCandidates(
            String variableNameWithValueAndCandidates) {
        variableValues.put("test_variable_one", null);

        variableValues.put(variableNameWithValueAndCandidates,
                RuleVariableValueBuilder.create()
                        .withValue("one")
                        .withCandidates(Arrays.asList("one", "two"))
                        .build());

        return variableValues;
    }

    private Map<String, RuleVariableValue> givenAVariableValuesAndOneWithUndefinedCandidates(
            String variableNameWithValueAndNonCandidates) {
        variableValues.put("test_variable_one", null);

        variableValues.put(variableNameWithValueAndNonCandidates,
                RuleVariableValueBuilder.create()
                        .withValue("one")
                        .build());

        return variableValues;
    }
}
