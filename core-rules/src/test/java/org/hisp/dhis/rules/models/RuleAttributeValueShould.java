package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleAttributeValueShould {

    @Test
    public void throw_null_pointer_exception_when_tracked_entity_attribute_is_null() {
        try {
            RuleAttributeValue.create(null, "test_value");
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_value() {
        try {
            RuleAttributeValue.create("test_tracked_entity_attribute", null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void propagate_correctly_the_properties() {
        RuleAttributeValue ruleAttributeValue = RuleAttributeValue
                .create("test_tracked_entity_attribute", "test_value");

        assertThat(ruleAttributeValue.trackedEntityAttribute()).isEqualTo("test_tracked_entity_attribute");
        assertThat(ruleAttributeValue.value()).isEqualTo("test_value");
    }
}
