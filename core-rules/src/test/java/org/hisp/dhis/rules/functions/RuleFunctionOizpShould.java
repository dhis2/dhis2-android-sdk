package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RuleFunctionOizpShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void return_zero_after_negative_numbers() {
        RuleFunction oizp = RuleFunctionOizp.create();

        String oizpNumber = oizp.evaluate(Arrays.asList("-0.9"),
                new HashMap<String, RuleVariableValue>());

        assertThat(oizpNumber).isEqualTo("0.0");

        oizpNumber = oizp.evaluate(Arrays.asList("-1"),
                new HashMap<String, RuleVariableValue>());

        assertThat(oizpNumber).isEqualTo("0.0");

        oizpNumber = oizp.evaluate(Arrays.asList("-10"),
                new HashMap<String, RuleVariableValue>());

        assertThat(oizpNumber).isEqualTo("0.0");
    }

    @Test
    public void return_correct_number_after_positive_number() {
        RuleFunction oizp = RuleFunctionOizp.create();

        String oizpNumber = oizp.evaluate(Arrays.asList("0"),
                new HashMap<String, RuleVariableValue>());

        assertThat(oizpNumber).isEqualTo("1.0");

        oizpNumber = oizp.evaluate(Arrays.asList("1"),
                new HashMap<String, RuleVariableValue>());

        assertThat(oizpNumber).isEqualTo("1.0");

        oizpNumber = oizp.evaluate(Arrays.asList("10"),
                new HashMap<String, RuleVariableValue>());

        assertThat(oizpNumber).isEqualTo("1.0");
    }
    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction oizp = RuleFunctionOizp.create();

        oizp.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }
    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction oizp = RuleFunctionOizp.create();

        oizp.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
