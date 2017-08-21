package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleVariablePreviousEventTests {

    @Test
    public void createShouldThrowOnNullName() {
        try {
            RuleVariablePreviousEvent.create(null, "test_dataelement", RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullDataElement() {
        try {
            RuleVariablePreviousEvent.create("test_variable", null, RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullDataElementType() {
        try {
            RuleVariablePreviousEvent.create("test_variable", "test_dataelement", null);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldPropagatePropertiesCorrectly() {
        RuleVariablePreviousEvent ruleVariablePreviousEvent = RuleVariablePreviousEvent.create(
                "test_variable", "test_dataelement", RuleValueType.NUMERIC);

        assertThat(ruleVariablePreviousEvent.name()).isEqualTo("test_variable");
        assertThat(ruleVariablePreviousEvent.dataElement()).isEqualTo("test_dataelement");
        assertThat(ruleVariablePreviousEvent.dataElementType()).isEqualTo(RuleValueType.NUMERIC);
    }
}
