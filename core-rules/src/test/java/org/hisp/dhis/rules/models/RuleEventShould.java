package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class RuleEventShould {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Test
    public void throw_null_pointer_exception_when_create_with_null_event() {
        try {
            RuleEvent.create(null, "test_programstage", RuleEvent.Status.ACTIVE,
                    new Date(), new Date(), Arrays.<RuleDataValue>asList());
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_program_stage() {
        try {
            RuleEvent.create("test_event", null, RuleEvent.Status.ACTIVE,
                    new Date(), new Date(), Arrays.<RuleDataValue>asList());
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_status() {
        try {
            RuleEvent.create("test_event", "test_programstage", null,
                    new Date(), new Date(), Arrays.<RuleDataValue>asList());
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_event_date() {
        try {
            RuleEvent.create("test_event", "test_programstage", RuleEvent.Status.ACTIVE,
                    null, new Date(), Arrays.<RuleDataValue>asList());
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_due_date() {
        try {
            RuleEvent.create("test_event", "test_programstage", RuleEvent.Status.ACTIVE,
                    new Date(), null, Arrays.<RuleDataValue>asList());
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_list_of_data_values() {
        try {
            RuleEvent.create("test_event", "test_programstage", RuleEvent.Status.ACTIVE,
                    new Date(), new Date(), null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void propagate_immutable_list_when_create_with_valid_values() {
        RuleDataValue ruleDataValue = mock(RuleDataValue.class);

        List<RuleDataValue> ruleDataValues = new ArrayList<>();
        ruleDataValues.add(ruleDataValue);

        RuleEvent ruleEvent = RuleEvent.create("test_event_uid", "test_stage_uid",
                RuleEvent.Status.ACTIVE, new Date(), new Date(), ruleDataValues);

        // add another data value
        ruleDataValues.add(ruleDataValue);

        assertThat(ruleEvent.dataValues().size()).isEqualTo(1);
        assertThat(ruleEvent.dataValues().get(0)).isEqualTo(ruleDataValue);

        try {
            ruleEvent.dataValues().add(ruleDataValue);
            fail("UnsupportedOperationException was expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void propagate_values_correctly_when_create_with_valid_values() {
        RuleDataValue ruleDataValue = mock(RuleDataValue.class);

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
    public void return_true_when_compare_event_with_equal_value() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        List<RuleEvent> ruleEvents = Arrays.asList(
                RuleEvent.create("test_event_one", "test_program_stage_one", RuleEvent.Status.ACTIVE,
                        dateFormat.parse("2014-02-11"), dateFormat.parse("2014-02-11"),
                        new ArrayList<RuleDataValue>()),
                RuleEvent.create("test_event_two", "test_program_stage_two", RuleEvent.Status.ACTIVE,
                        dateFormat.parse("2017-03-22"), dateFormat.parse("2017-03-22"),
                        new ArrayList<RuleDataValue>()));

        Collections.sort(ruleEvents, RuleEvent.EVENT_DATE_COMPARATOR);

        assertThat(ruleEvents.get(0).event()).isEqualTo("test_event_two");
        assertThat(ruleEvents.get(1).event()).isEqualTo("test_event_one");
    }
}
