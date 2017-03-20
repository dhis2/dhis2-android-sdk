package org.hisp.dhis.android.rules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class EventTests {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

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

    @Test
    public void eventDateComparatorTest() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        List<Event> events = Arrays.asList(
                Event.create("test_event_one", EventStatus.ACTIVE, "test_program_stage_one",
                        dateFormat.parse("2014-02-11"), dateFormat.parse("2014-02-11"), new ArrayList<DataValue>()),
                Event.create("test_event_two", EventStatus.ACTIVE, "test_program_stage_two",
                        dateFormat.parse("2017-03-22"), dateFormat.parse("2017-03-22"), new ArrayList<DataValue>()));

        Collections.sort(events, Event.EVENT_DATE_COMPARATOR);

        assertThat(events.get(0).event()).isEqualTo("test_event_two");
        assertThat(events.get(1).event()).isEqualTo("test_event_one");

    }

//    ToDo
//    @Test
//    public void equalsAndHashcodeShouldConformToContract() {
//        EqualsVerifier.forClass(Event.class).verify();
//    }
}
