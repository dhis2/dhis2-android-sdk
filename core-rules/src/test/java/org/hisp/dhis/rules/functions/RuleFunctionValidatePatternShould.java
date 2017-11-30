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

public class RuleFunctionValidatePatternShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void return_true_if_pattern_match() {
        RuleFunction ruleFunction = RuleFunctionValidatePattern.create();

        HashMap<String, RuleVariableValue> ruleVariableValueHashMap = new HashMap<>();
        String pattern="\\d{4}/\\d{2}/\\d";
        String result = ruleFunction.evaluate(Arrays.asList(pattern, "9999/99/9"), ruleVariableValueHashMap);
        assertThat(result).isEqualTo("true");

        pattern="[0-9]{4}/[0-9]{2}/[0-9]";
        result = ruleFunction.evaluate(Arrays.asList(pattern, "9999/99/9"), ruleVariableValueHashMap);
        assertThat(result).isEqualTo("true");
    }

    @Test
    public void return_false_if_pattern_not_match() {
        RuleFunction ruleFunction = RuleFunctionValidatePattern.create();

        HashMap<String, RuleVariableValue> ruleVariableValueHashMap = new HashMap<>();
        String pattern="\\[9]{4}/\\d{2}/\\d";
        String result = ruleFunction.evaluate(Arrays.asList(pattern, "1999/99/9"), ruleVariableValueHashMap);
        assertThat(result).isEqualTo("false");

        pattern="[0-9]{4}/[0-9]{2}/[0-9]";
        result = ruleFunction.evaluate(Arrays.asList(pattern, "9999/99/"), ruleVariableValueHashMap);
        assertThat(result).isEqualTo("false");
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_null() {
        thrown.expect(NullPointerException.class);
        RuleFunction ruleFunction = RuleFunctionValidatePattern.create();

        ruleFunction.evaluate(null,
                new HashMap<String, RuleVariableValue>());
    }

    @Test
    public void thrown_illegal_argument_exception_if_first_parameter_is_empty_list() {
        thrown.expect(IllegalArgumentException.class);
        RuleFunction ruleFunction = RuleFunctionValidatePattern.create();

        ruleFunction.evaluate(new ArrayList<String>(),
                new HashMap<String, RuleVariableValue>());
    }
}
