package org.hisp.dhis.android.rules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class TrackedEntityDataValueTests {

    @Mock
    private Event event;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldThrowOnNullEvent() {
        try {
            TrackedEntityDataValue.create(null, "test_field", "test_value");
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void shouldThrowOnNullDataElement() {
        try {
            TrackedEntityDataValue.create(event, null, "test_value");
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void shouldThrowOnNullValue() {
        try {
            TrackedEntityDataValue.create(event, "test_dataelement", null);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void shouldPropagateValuesCorrectly() {
        TrackedEntityDataValue dataValue = TrackedEntityDataValue.create(
                event, "test_dataelement", "test_value");

        assertThat(dataValue.event()).isEqualTo(event);
        assertThat(dataValue.dataElement()).isEqualTo("test_dataelement");
        assertThat(dataValue.value()).isEqualTo("test_value");
    }
}
