package org.hisp.dhis.android.rules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class TrackedEntityAttributeValueTests {

    @Test
    public void shouldThrowOnNullTrackedEntityAttribute() {
        try {
            TrackedEntityAttributeValue.create(null, "test_value");
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void shouldThrowOnNullValue() {
        try {
            TrackedEntityAttributeValue.create("test_tracked_entity_attribute", null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void propertiesShouldBePropagatedCorrectly() {
        TrackedEntityAttributeValue attributeValue = TrackedEntityAttributeValue
                .create("test_tracked_entity_attribute", "test_value");

        assertThat(attributeValue.trackedEntityAttribute()).isEqualTo("test_tracked_entity_attribute");
        assertThat(attributeValue.value()).isEqualTo("test_value");
    }
}
