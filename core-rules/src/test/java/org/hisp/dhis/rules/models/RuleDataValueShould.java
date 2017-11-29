package org.hisp.dhis.rules.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleDataValueShould {

    @Mock
    private RuleEvent ruleEvent;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_date() {
        try {
            RuleDataValue.create(null, "test_program_stage_uid", "test_field", "test_value");
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_event() {
        try {
            RuleDataValue.create(new Date(), null, "test_field", "test_value");
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_data_element() {
        try {
            RuleDataValue.create(new Date(), "test_program_stage_uid", null, "test_value");
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_create_with_null_value() {
        try {
            RuleDataValue.create(new Date(), "test_program_stage_uid", "test_dataelement", null);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void propagate_values_correctly_when_create_with_valid_values() {
        Date eventDate = new Date();
        RuleDataValue ruleDataValue = RuleDataValue.create(eventDate,
                "test_program_stage_uid", "test_dataelement", "test_value");

        assertThat(ruleDataValue.eventDate()).isEqualTo(eventDate);
        assertThat(ruleDataValue.programStage()).isEqualTo("test_program_stage_uid");
        assertThat(ruleDataValue.dataElement()).isEqualTo("test_dataelement");
        assertThat(ruleDataValue.value()).isEqualTo("test_value");
    }
}
