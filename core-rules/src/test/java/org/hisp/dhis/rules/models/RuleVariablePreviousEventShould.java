package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleVariablePreviousEventShould {

    @Test
    public void throw_null_pointer_exception_when_create_with_null_name() {
        try {
            RuleVariablePreviousEvent.create(null, "test_dataelement", RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_date_element() {
        try {
            RuleVariablePreviousEvent.create("test_variable", null, RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_date_element_type() {
        try {
            RuleVariablePreviousEvent.create("test_variable", "test_dataelement", null);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void propagate_properties_correctly_when_create_with_valid_values() {
        RuleVariablePreviousEvent ruleVariablePreviousEvent = RuleVariablePreviousEvent.create(
                "test_variable", "test_dataelement", RuleValueType.NUMERIC);

        assertThat(ruleVariablePreviousEvent.name()).isEqualTo("test_variable");
        assertThat(ruleVariablePreviousEvent.dataElement()).isEqualTo("test_dataelement");
        assertThat(ruleVariablePreviousEvent.dataElementType()).isEqualTo(RuleValueType.NUMERIC);
    }
}
