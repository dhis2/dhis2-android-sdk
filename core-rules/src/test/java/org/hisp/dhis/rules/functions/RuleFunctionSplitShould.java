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

public class RuleFunctionSplitShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void return_correct_values() {
        RuleFunction ruleFunction = RuleFunctionSplit.create();
        String result = ruleFunction.evaluate(Arrays.asList("test_variable_one", "variable", "1"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("_one");
        result = ruleFunction.evaluate(Arrays.asList("test_variable_one", "_", "0"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("test");
        result = ruleFunction.evaluate(Arrays.asList("test_variable_one", "_", "1"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("variable");
        result = ruleFunction.evaluate(Arrays.asList("test_variable_one", "_", "2"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("one");
        result = ruleFunction.evaluate(Arrays.asList("test_variable_one", "_", "3"),new HashMap<String, RuleVariableValue>());
        assertThat(result).isEqualTo("");
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionSplit.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_pass_only_two_parameters() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionSplit.create();

        ruleFunction.evaluate(Arrays.asList("test_variable_one", "variable"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_pass_four_parameters() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionSplit.create();

        ruleFunction.evaluate(Arrays.asList("test_variable_one", "variable", "1", "2"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_position_is_a_text() {
        thrown.expect(NumberFormatException.class);
        RuleFunction ruleFunction = RuleFunctionSplit.create();

        ruleFunction.evaluate(Arrays.asList("test_variable_one", "variable", "text"),
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionSplit.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
