package org.hisp.dhis.android.rules.models;

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
public class RuleEventTests {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Mock
    private RuleDataValue ruleDataValue;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createShouldThrowExceptionIfEventIsNull() {
        try {
            RuleEvent.create(null, "test_stage_uid", RuleEvent.Status.ACTIVE,
                    new Date(), new Date(), Arrays.<RuleDataValue>asList());
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void dataValuesShouldBeImmutable() {
        List<RuleDataValue> ruleDataValues = new ArrayList<>();
        ruleDataValues.add(ruleDataValue);

        try {
            RuleEvent ruleEvent = RuleEvent.create("test_event_uid", "test_stage_uid",
                    RuleEvent.Status.ACTIVE, new Date(), new Date(), ruleDataValues);

            // add another data value
            ruleDataValues.add(ruleDataValue);

            assertThat(ruleEvent.dataValues().size()).isEqualTo(1);
            assertThat(ruleEvent.dataValues().contains(ruleDataValue)).isTrue();
            ruleEvent.dataValues().add(ruleDataValue);

            fail("UnsupportedOperationException was expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void allValuesShouldBePropagatedCorrectly() {
        List<RuleDataValue> ruleDataValues = new ArrayList<>();
        ruleDataValues.add(ruleDataValue);

        Date eventDate = new Date();
        Date dueDate = new Date();

        RuleEvent ruleEvent = RuleEvent.create("test_event_uid", "test_stage_uid",
                RuleEvent.Status.ACTIVE, eventDate, dueDate, ruleDataValues);

        assertThat(ruleEvent.event()).isEqualTo("test_event_uid");
        assertThat(ruleEvent.status()).isEqualTo(RuleEvent.Status.ACTIVE);
        assertThat(ruleEvent.programStage()).isEqualTo("test_stage_uid");
        assertThat(ruleEvent.eventDate()).isEqualTo(eventDate);
        assertThat(ruleEvent.dueDate()).isEqualTo(dueDate);

        assertThat(ruleEvent.dataValues().size()).isEqualTo(1);
        assertThat(ruleEvent.dataValues().get(0)).isEqualTo(ruleDataValue);
    }

    @Test
    public void eventDateComparatorTest() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        List<RuleEvent> ruleEvents = Arrays.asList(
                RuleEvent.create("test_event_one", "test_program_stage_one", RuleEvent.Status.ACTIVE,
                        dateFormat.parse("2014-02-11"), dateFormat.parse("2014-02-11"), new ArrayList<RuleDataValue>()),
                RuleEvent.create("test_event_two", "test_program_stage_two", RuleEvent.Status.ACTIVE,
                        dateFormat.parse("2017-03-22"), dateFormat.parse("2017-03-22"), new ArrayList<RuleDataValue>()));

        Collections.sort(ruleEvents, RuleEvent.EVENT_DATE_COMPARATOR);

        assertThat(ruleEvents.get(0).event()).isEqualTo("test_event_two");
        assertThat(ruleEvents.get(1).event()).isEqualTo("test_event_one");
    }
}
