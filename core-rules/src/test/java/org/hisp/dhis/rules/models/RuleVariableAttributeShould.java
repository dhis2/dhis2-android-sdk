package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleVariableAttributeShould {

    @Test
    public void throw_null_pointer_exception_when_create_with_null_name() {
        try {
            RuleVariableAttribute.create(null, "test_attribute", RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_tracked_entity_attribute() {
        try {
            RuleVariableAttribute.create("test_variable", null, RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_tracked_entity_attribute_type() {
        try {
            RuleVariableAttribute.create("test_variable", "test_attribute", null);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void propagate_properties_correctly_when_create_with_valid_valuescreateShouldPropagatePropertiesCorrectly() {
        RuleVariableAttribute ruleVariableAttribute = RuleVariableAttribute.create(
                "test_variable", "test_attribute", RuleValueType.NUMERIC);

        assertThat(ruleVariableAttribute.name()).isEqualTo("test_variable");
        assertThat(ruleVariableAttribute.trackedEntityAttribute()).isEqualTo("test_attribute");
        assertThat(ruleVariableAttribute.trackedEntityAttributeType()).isEqualTo(RuleValueType.NUMERIC);
    }
}
