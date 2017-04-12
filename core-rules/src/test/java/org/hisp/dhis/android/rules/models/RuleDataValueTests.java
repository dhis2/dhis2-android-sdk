package org.hisp.dhis.android.rules.models;

import org.hisp.dhis.android.rules.models.RuleDataValue;
import org.hisp.dhis.android.rules.models.RuleEvent;
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
public class RuleDataValueTests {

    @Mock
    private RuleEvent ruleEvent;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldThrowOnNullDate() {
        try {
            RuleDataValue.create(null, "test_program_stage_uid", "test_field", "test_value");
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void shouldThrowOnNullEvent() {
        try {
            RuleDataValue.create(new Date(), null, "test_field", "test_value");
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void shouldThrowOnNullDataElement() {
        try {
            RuleDataValue.create(new Date(), "test_program_stage_uid", null, "test_value");
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void shouldThrowOnNullValue() {
        try {
            RuleDataValue.create(new Date(), "test_program_stage_uid", "test_dataelement", null);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void shouldPropagateValuesCorrectly() {
        RuleDataValue ruleDataValue = RuleDataValue.create(new Date(),
                "test_program_stage_uid", "test_dataelement", "test_value");

        assertThat(ruleDataValue.programStage()).isEqualTo("test_program_stage_uid");
        assertThat(ruleDataValue.dataElement()).isEqualTo("test_dataelement");
        assertThat(ruleDataValue.value()).isEqualTo("test_value");
    }
}
