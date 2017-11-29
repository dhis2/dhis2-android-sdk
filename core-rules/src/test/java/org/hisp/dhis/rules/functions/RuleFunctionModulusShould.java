package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RuleFunctionModulusShould {
    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void return_correct_modulus() {
        RuleFunction ruleFunction = RuleFunctionModulus.create();

        String result = ruleFunction.evaluate(Arrays.asList("11", "3"),
                new HashMap<String, RuleVariableValue>());

        assertThat(result).isEqualTo("2");

        result = ruleFunction.evaluate(Arrays.asList("-11", "3"),
                new HashMap<String, RuleVariableValue>());

        assertThat(result).isEqualTo("-2");

        result = ruleFunction.evaluate(Arrays.asList("0", "2"),
                new HashMap<String, RuleVariableValue>());

        assertThat(result).isEqualTo("0");
    }

    @Test
    public void thrown_illegal_argument_exception_if_zero_dividend() {
        thrown.expect(ArithmeticException.class);
        RuleFunction ruleFunction = RuleFunctionModulus.create();

        String result = ruleFunction.evaluate(Arrays.asList("0", "0"),
                new HashMap<String, RuleVariableValue>());

        assertThat(result).isEqualTo("0");
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionModulus.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionModulus.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_not_numeric_arguments() {
        thrown.expect(NumberFormatException.class);
        RuleFunction ruleFunction = RuleFunctionModulus.create();

        String result = ruleFunction.evaluate(Arrays.asList("word", "word"),
                new HashMap<String, RuleVariableValue>());
    }
}
