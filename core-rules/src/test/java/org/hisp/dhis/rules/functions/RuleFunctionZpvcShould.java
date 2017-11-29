package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RuleFunctionZpvcShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void return_correct_values() {
        RuleFunction zpvc = RuleFunctionZpvc.create();

        String oizpNumber = zpvc.evaluate(Arrays.asList("0","1", "-1", "2", "-2", "3"),
                new HashMap<String, RuleVariableValue>());

        assertThat(oizpNumber).isEqualTo("4");
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction oizp = RuleFunctionZpvc.create();

        oizp.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction oizp = RuleFunctionZpvc.create();

        oizp.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
