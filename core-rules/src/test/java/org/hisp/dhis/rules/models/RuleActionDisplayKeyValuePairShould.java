package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleActionDisplayKeyValuePairShould {
    
    @Test
    public void createForFeedbackMustSubstituteCorrectLocation() {
        RuleActionDisplayKeyValuePair displayTextAction = RuleActionDisplayKeyValuePair
                .createForFeedback("test_content", "test_data");

        assertThat(displayTextAction.location())
                .isEqualTo(RuleActionDisplayKeyValuePair.LOCATION_FEEDBACK_WIDGET);
        assertThat(displayTextAction.content()).isEqualTo("test_content");
        assertThat(displayTextAction.data()).isEqualTo("test_data");
    }

    @Test
    public void createForIndicatorsMustSubstituteCorrectLocation() {
        RuleActionDisplayKeyValuePair displayTextAction = RuleActionDisplayKeyValuePair
                .createForIndicators("test_content", "test_data");

        assertThat(displayTextAction.location())
                .isEqualTo(RuleActionDisplayKeyValuePair.LOCATION_INDICATOR_WIDGET);
        assertThat(displayTextAction.content()).isEqualTo("test_content");
        assertThat(displayTextAction.data()).isEqualTo("test_data");
    }

    @Test
    public void createForFeedbackMustThrowWhenBothArgumentsNull() {
        try {
            RuleActionDisplayKeyValuePair.createForFeedback(null, null);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void createForIndicatorsMustThrowWhenBothArgumentsNull() {
        try {
            RuleActionDisplayKeyValuePair.createForIndicators(null, null);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void createForFeedbackMustSubstituteEmptyStringsForNullArguments() {
        RuleActionDisplayKeyValuePair ruleActionNoContent = RuleActionDisplayKeyValuePair
                .createForFeedback(null, "test_data");
        RuleActionDisplayKeyValuePair ruleActionNoData = RuleActionDisplayKeyValuePair
                .createForFeedback("test_content", null);

        assertThat(ruleActionNoContent.content()).isEqualTo("");
        assertThat(ruleActionNoContent.data()).isEqualTo("test_data");

        assertThat(ruleActionNoData.content()).isEqualTo("test_content");
        assertThat(ruleActionNoData.data()).isEqualTo("");
    }

    @Test
    public void createForIndicatorsMustSubstituteEmptyStringsForNullArguments() {
        RuleActionDisplayKeyValuePair ruleActionNoContent = RuleActionDisplayKeyValuePair
                .createForIndicators(null, "test_data");
        RuleActionDisplayKeyValuePair ruleActionNoData = RuleActionDisplayKeyValuePair
                .createForIndicators("test_content", null);

        assertThat(ruleActionNoContent.content()).isEqualTo("");
        assertThat(ruleActionNoContent.data()).isEqualTo("test_data");

        assertThat(ruleActionNoData.content()).isEqualTo("test_content");
        assertThat(ruleActionNoData.data()).isEqualTo("");
    }

    @Test
    public void equalsAndHashcodeFunctionsMustConformToContract() {
        EqualsVerifier.forClass(RuleActionDisplayKeyValuePair.createForFeedback("", "").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
