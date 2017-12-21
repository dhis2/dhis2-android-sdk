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

public class RuleFunctionConcatenateShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    private Map<String, RuleVariableValue> variableValues = new HashMap<>();

    @Test
    public void return_concatenated_strings() {
        RuleFunction concatenateFunction = RuleFunctionConcatenate.create();

        assertThat(concatenateFunction.evaluate(null, variableValues), is(""));
        assertThat(concatenateFunction.evaluate(asList("hello"), variableValues), is("hello"));
        assertThat(concatenateFunction.evaluate(asList("hello", null), variableValues),
                is("hello"));
        assertThat(concatenateFunction.evaluate(Arrays.<String>asList(null, null), variableValues),
                is(""));
        assertThat(concatenateFunction.evaluate(asList("hello", " ", "there", "!"), variableValues),
                is("hello there!"));
    }
}
