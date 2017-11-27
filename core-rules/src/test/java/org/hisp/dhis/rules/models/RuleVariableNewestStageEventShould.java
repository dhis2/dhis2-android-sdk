package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleVariableNewestStageEventShould {

    @Test
    public void createShouldThrowOnNullName() {
        try {
            RuleVariableNewestStageEvent.create(null, "test_dataelement", "test_programstage", RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullDataElement() {
        try {
            RuleVariableNewestStageEvent.create("test_variable", null, "test_programstage", RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullProgramStage() {
        try {
            RuleVariableNewestStageEvent.create("test_variable", "test_dataelement", null, RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullDataElementType() {
        try {
            RuleVariableNewestStageEvent.create("test_variable", "test_dataelement", "test_programstage", null);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldPropagatePropertiesCorrectly() {
        RuleVariableNewestStageEvent ruleVariablePreviousEvent = RuleVariableNewestStageEvent.create(
                "test_variable", "test_dataelement", "test_programstage", RuleValueType.NUMERIC);

        assertThat(ruleVariablePreviousEvent.name()).isEqualTo("test_variable");
        assertThat(ruleVariablePreviousEvent.dataElement()).isEqualTo("test_dataelement");
        assertThat(ruleVariablePreviousEvent.programStage()).isEqualTo("test_programstage");
        assertThat(ruleVariablePreviousEvent.dataElementType()).isEqualTo(RuleValueType.NUMERIC);
    }
}
