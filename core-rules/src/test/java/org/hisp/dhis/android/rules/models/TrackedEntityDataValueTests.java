package org.hisp.dhis.android.rules.models;

import org.hisp.dhis.android.rules.models.Event;
import org.hisp.dhis.android.rules.models.TrackedEntityDataValue;
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
            TrackedEntityDataValue.create("test_program_stage_uid", null, "test_value");
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void shouldThrowOnNullValue() {
        try {
            TrackedEntityDataValue.create("test_program_stage_uid", "test_dataelement", null);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void shouldPropagateValuesCorrectly() {
        TrackedEntityDataValue dataValue = TrackedEntityDataValue.create(
                "test_program_stage_uid", "test_dataelement", "test_value");

        assertThat(dataValue.programStage()).isEqualTo("test_program_stage_uid");
        assertThat(dataValue.dataElement()).isEqualTo("test_dataelement");
        assertThat(dataValue.value()).isEqualTo("test_value");
    }
}
