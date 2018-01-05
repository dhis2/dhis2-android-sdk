package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleVariableNewestStageEventShould {

    @Test
    public void throw_null_pointer_exception_when_create_with_null_name() {
        try {
            RuleVariableNewestStageEvent.create(null, "test_dataelement", "test_programstage", RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_data_element() {
        try {
            RuleVariableNewestStageEvent.create("test_variable", null, "test_programstage", RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_program_stage() {
        try {
            RuleVariableNewestStageEvent.create("test_variable", "test_dataelement", null, RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_data_element_type() {
        try {
            RuleVariableNewestStageEvent.create("test_variable", "test_dataelement", "test_programstage", null);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void propagate_properties_correctly_when_create_with_valid_values() {
        RuleVariableNewestStageEvent ruleVariablePreviousEvent = RuleVariableNewestStageEvent.create(
                "test_variable", "test_dataelement", "test_programstage", RuleValueType.NUMERIC);

        assertThat(ruleVariablePreviousEvent.name()).isEqualTo("test_variable");
        assertThat(ruleVariablePreviousEvent.dataElement()).isEqualTo("test_dataelement");
        assertThat(ruleVariablePreviousEvent.programStage()).isEqualTo("test_programstage");
        assertThat(ruleVariablePreviousEvent.dataElementType()).isEqualTo(RuleValueType.NUMERIC);
    }
}
