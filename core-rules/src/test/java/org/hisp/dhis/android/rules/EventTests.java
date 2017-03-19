package org.hisp.dhis.android.rules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class EventTests {

    @Mock
    private DataValue dataValue;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createShouldThrowExceptionIfEventIsNull() {
        try {
            Event.create(null, EventStatus.ACTIVE, "test_stage_uid",
                    new Date(), new Date(), Arrays.<DataValue>asList());
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void dataValuesShouldBeImmutable() {
        List<DataValue> dataValues = new ArrayList<>();
        dataValues.add(dataValue);

        try {
            Event event = Event.create("test_event_uid", EventStatus.ACTIVE, "test_stage_uid",
                    new Date(), new Date(), dataValues);

            // add another data value
            dataValues.add(dataValue);

            assertThat(event.dataValues().size()).isEqualTo(1);
            assertThat(event.dataValues().contains(dataValue)).isTrue();
            event.dataValues().add(dataValue);

            fail("UnsupportedOperationException was expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void allValuesShouldBePropagatedCorrectly() {
        List<DataValue> dataValues = new ArrayList<>();
        dataValues.add(dataValue);

        Date eventDate = new Date();
        Date dueDate = new Date();

        Event event = Event.create("test_event_uid", EventStatus.ACTIVE,
                "test_stage_uid", eventDate, dueDate, dataValues);

        assertThat(event.event()).isEqualTo("test_event_uid");
        assertThat(event.status()).isEqualTo(EventStatus.ACTIVE);
        assertThat(event.programStage()).isEqualTo("test_stage_uid");
        assertThat(event.eventDate()).isEqualTo(eventDate);
        assertThat(event.dueDate()).isEqualTo(dueDate);

        assertThat(event.dataValues().size()).isEqualTo(1);
        assertThat(event.dataValues().get(0)).isEqualTo(dataValue);
    }

//    ToDo
//    @Test
//    public void equalsAndHashcodeShouldConformToContract() {
//        EqualsVerifier.forClass(Event.class).verify();
//    }
}
