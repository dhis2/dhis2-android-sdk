package org.hisp.dhis.rules.functions;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.hisp.dhis.rules.RuleVariableValue;
import org.hisp.dhis.rules.RuleVariableValueMapBuilder;
import org.hisp.dhis.rules.models.RuleDataValue;
import org.hisp.dhis.rules.models.RuleEvent;
import org.hisp.dhis.rules.models.RuleValueType;
import org.hisp.dhis.rules.models.RuleVariable;
import org.hisp.dhis.rules.models.RuleVariableCurrentEvent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RuleFunctionSubStringShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void return_correct_values() {
        RuleFunction ruleFunction = RuleFunctionSubString.create();

        String result = ruleFunction.evaluate(Arrays.asList("testing", "0", "4"), new HashMap<String, RuleVariableValue>());

        assertThat(result).isEqualTo("test");
        result = ruleFunction.evaluate(Arrays.asList("testing", "4", "10"), new HashMap<String, RuleVariableValue>());

        assertThat(result).isEqualTo("ing");
        result = ruleFunction.evaluate(Arrays.asList("testing", "4", "7"), new HashMap<String, RuleVariableValue>());

        assertThat(result).isEqualTo("ing");
        result = ruleFunction.evaluate(Arrays.asList("testing", "3", "4"), new HashMap<String, RuleVariableValue>());

        assertThat(result).isEqualTo("t");
    }

    @Test
    public void thrown_illegal_argument_exception_if_end_number_is_string() {
        thrown.expect(NumberFormatException.class);
        RuleFunction ruleFunction = RuleFunctionSubString.create();

        ruleFunction.evaluate(Arrays.asList("testing", "3", "letter"), new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_start_number_is_string() {
        thrown.expect(NumberFormatException.class);
        RuleFunction ruleFunction = RuleFunctionSubString.create();

        ruleFunction.evaluate(Arrays.asList("testing", "letter", "3"), new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionSubString.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionSubString.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
