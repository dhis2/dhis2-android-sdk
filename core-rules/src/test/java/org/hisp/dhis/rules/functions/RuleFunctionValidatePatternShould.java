package org.hisp.dhis.rules.functions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static java.util.Arrays.asList;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RuleFunctionValidatePatternShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_true_if_pattern_match() {
        RuleFunction validatePattern = RuleFunctionValidatePattern.create();

        assertThat(validatePattern.evaluate(
                asList("123", "123"), variableValues), is("true"));

        assertThat(validatePattern.evaluate(
                asList("27123456789", "27\\d{2}\\d{3}\\d{4}"), variableValues), is("true"));

        assertThat(validatePattern.evaluate(
                asList("27123456789", "27\\d{9}"), variableValues), is("true"));


        assertThat(validatePattern.evaluate(
                asList("abc123", "abc123"), variableValues), is("true"));


        assertThat(validatePattern.evaluate(
                asList("9999/99/9", "\\d{4}/\\d{2}/\\d"), variableValues), is("true"));


        assertThat(validatePattern.evaluate(
                asList("9999/99/9", "[0-9]{4}/[0-9]{2}/[0-9]"), variableValues), is("true"));
    }

    @Test
    public void return_false_for_non_matching_pairs() {
        RuleFunction validatePattern = RuleFunctionValidatePattern.create();

        assertThat(validatePattern.evaluate(
                asList("1999/99/9", "\\[9]{4}/\\d{2}/\\d"), variableValues), is("false"));

        assertThat(validatePattern.evaluate(
                asList("9999/99/", "[0-9]{4}/[0-9]{2}/[0-9]"), variableValues), is("false"));


        assertThat(validatePattern.evaluate(
                asList("abc123", "xyz"), variableValues), is("false"));

        assertThat(validatePattern.evaluate(
                asList("abc123", "^bc"), variableValues), is("false"));

        assertThat(validatePattern.evaluate(
                asList("abc123", "abc12345"), variableValues), is("false"));

        assertThat(validatePattern.evaluate(
                asList("123", "567"), variableValues), is("false"));
    }

    @Test
    public void throw_illegal_argument_exception_when_argument_count_is_greater_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionValidatePattern.create().evaluate(
                Arrays.asList("\\d{4}/\\d{2}/\\d", "9999/99/9", "2016-01-01"),
                variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_count_is_lower_than_expected() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionValidatePattern.create().evaluate(Arrays.asList("\\d{4}/\\d{2}/\\d"),
                variableValues);
    }

    @Test
    public void throw_illegal_argument_exception_when_arguments_is_null() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunctionValidatePattern.create().evaluate(null, variableValues);
    }
}
