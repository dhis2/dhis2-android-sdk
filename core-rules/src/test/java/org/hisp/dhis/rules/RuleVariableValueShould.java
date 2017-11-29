package org.hisp.dhis.rules;

import org.hisp.dhis.rules.models.RuleDataValue;
import org.hisp.dhis.rules.models.RuleValueType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleVariableValueShould {

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void wrap_text_values() {
        RuleVariableValue variableValue = RuleVariableValue.create(
                "test_value", RuleValueType.TEXT, Arrays.asList(
                        "test_value_candidate_one", "test_value_candidate_two"));

        assertThat(variableValue.value()).isEqualTo("'test_value'");
        assertThat(variableValue.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValue.candidates().size()).isEqualTo(2);
        assertThat(variableValue.candidates().get(0)).isEqualTo("test_value_candidate_one");
        assertThat(variableValue.candidates().get(1)).isEqualTo("test_value_candidate_two");
    }


    @Test
    public void not_be_double_wrapped_text_value_when_wrapped() {
        RuleVariableValue variableValue = RuleVariableValue.create(
                "'test_value'", RuleValueType.TEXT, Arrays.asList(
                        "test_value_candidate_one", "test_value_candidate_two"));

        assertThat(variableValue.value()).isEqualTo("'test_value'");
        assertThat(variableValue.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValue.candidates().size()).isEqualTo(2);
        assertThat(variableValue.candidates().get(0)).isEqualTo("test_value_candidate_one");
        assertThat(variableValue.candidates().get(1)).isEqualTo("test_value_candidate_two");
    }

    @Test
    public void not_wrap_numeric_values() {
        RuleVariableValue variableValue = RuleVariableValue.create(
                "1", RuleValueType.NUMERIC, Arrays.asList("2", "3"));

        assertThat(variableValue.value()).isEqualTo("1");
        assertThat(variableValue.type()).isEqualTo(RuleValueType.NUMERIC);
        assertThat(variableValue.candidates().size()).isEqualTo(2);
        assertThat(variableValue.candidates().get(0)).isEqualTo("2");
        assertThat(variableValue.candidates().get(1)).isEqualTo("3");
    }

    @Test
    public void not_wrap_boolean_values() {
        RuleVariableValue variableValue = RuleVariableValue.create(
                "true", RuleValueType.BOOLEAN, Arrays.asList("false", "false"));

        assertThat(variableValue.value()).isEqualTo("true");
        assertThat(variableValue.type()).isEqualTo(RuleValueType.BOOLEAN);
        assertThat(variableValue.candidates().size()).isEqualTo(2);
        assertThat(variableValue.candidates().get(0)).isEqualTo("false");
        assertThat(variableValue.candidates().get(1)).isEqualTo("false");
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_value_type() {
        try {
            RuleVariableValue.create("test_value", null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_candidate_list() {
        try {
            RuleVariableValue.create("test_value", RuleValueType.TEXT, null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }
}
