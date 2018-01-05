package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleVariableCurrentEventShould {

    @Test
    public void throw_null_pointer_exception_when_create_with_null_name() {
        try {
            RuleVariableCurrentEvent.create(null, "test_dataelement", RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_data_element() {
        try {
            RuleVariableCurrentEvent.create("test_variable", null, RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_data_element_type() {
        try {
            RuleVariableCurrentEvent.create("test_variable", "test_dataelement", null);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void propagate_properties_correctly_when_create_with_valid_values() {
        RuleVariableCurrentEvent ruleVariableCurrentEvent = RuleVariableCurrentEvent.create(
                "test_variable", "test_dataelement", RuleValueType.NUMERIC);

        assertThat(ruleVariableCurrentEvent.name()).isEqualTo("test_variable");
        assertThat(ruleVariableCurrentEvent.dataElement()).isEqualTo("test_dataelement");
        assertThat(ruleVariableCurrentEvent.dataElementType()).isEqualTo(RuleValueType.NUMERIC);
    }
}
