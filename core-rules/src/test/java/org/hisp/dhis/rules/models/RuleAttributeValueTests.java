package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleAttributeValueTests {

    @Test
    public void shouldThrowOnNullTrackedEntityAttribute() {
        try {
            RuleAttributeValue.create(null, "test_value");
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void shouldThrowOnNullValue() {
        try {
            RuleAttributeValue.create("test_tracked_entity_attribute", null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void propertiesShouldBePropagatedCorrectly() {
        RuleAttributeValue ruleAttributeValue = RuleAttributeValue
                .create("test_tracked_entity_attribute", "test_value");

        assertThat(ruleAttributeValue.trackedEntityAttribute()).isEqualTo("test_tracked_entity_attribute");
        assertThat(ruleAttributeValue.value()).isEqualTo("test_value");
    }
}
