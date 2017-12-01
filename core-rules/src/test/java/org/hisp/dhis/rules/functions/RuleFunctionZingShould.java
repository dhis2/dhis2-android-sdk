package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RuleFunctionZingShould {
    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void return_zero_after_negative_number() {
        RuleFunction zing = RuleFunctionZing.create();

        String zingNumber = zing.evaluate(Arrays.asList("-0.1"),
                new HashMap<String, RuleVariableValue>());

        assertThat(zingNumber).isEqualTo("0.0");
        zingNumber = zing.evaluate(Arrays.asList("-1"),
                new HashMap<String, RuleVariableValue>());

        assertThat(zingNumber).isEqualTo("0.0");
        zingNumber = zing.evaluate(Arrays.asList("-10"),
                new HashMap<String, RuleVariableValue>());

        assertThat(zingNumber).isEqualTo("0.0");
    }

    @Test
    public void return_number_after_positive_number() {
        RuleFunction zing = RuleFunctionZing.create();

        String zingNumber = zing.evaluate(Arrays.asList("0"),
                new HashMap<String, RuleVariableValue>());

        assertThat(zingNumber).isEqualTo("0.0");
        zingNumber = zing.evaluate(Arrays.asList("1"),
                new HashMap<String, RuleVariableValue>());

        assertThat(zingNumber).isEqualTo("1.0");
        zingNumber = zing.evaluate(Arrays.asList("5"),
                new HashMap<String, RuleVariableValue>());

        assertThat(zingNumber).isEqualTo("5.0");
    }

    @Test
    public void return_number_after_positive_number_with_decimals() {
        RuleFunction zing = RuleFunctionZing.create();

        String zingNumber = zing.evaluate(Arrays.asList("0.1"),
                new HashMap<String, RuleVariableValue>());

        assertThat(zingNumber).isEqualTo("0.0");
        zingNumber = zing.evaluate(Arrays.asList("1.1"),
                new HashMap<String, RuleVariableValue>());

        assertThat(zingNumber).isEqualTo("1.0");
        zingNumber = zing.evaluate(Arrays.asList("-1.1"),
                new HashMap<String, RuleVariableValue>());

        assertThat(zingNumber).isEqualTo("0.0");
    }

    @Test
    public void throw_null_pointer_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction zing = RuleFunctionZing.create();

        zing.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void throw_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction zing = RuleFunctionZing.create();

        zing.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
