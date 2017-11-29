package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleActionDisplayTextShould {


    @Test
    public void substitute_correct_location_when_create_for_feedback() {
        RuleActionDisplayText displayTextAction = RuleActionDisplayText
                .createForFeedback("test_content", "test_data");

        assertThat(displayTextAction.location())
                .isEqualTo(RuleActionDisplayText.LOCATION_FEEDBACK_WIDGET);
        assertThat(displayTextAction.content()).isEqualTo("test_content");
        assertThat(displayTextAction.data()).isEqualTo("test_data");
    }

    @Test
    public void substitute_correct_location_when_create_for_indicators() {
        RuleActionDisplayText displayTextAction = RuleActionDisplayText
                .createForIndicators("test_content", "test_data");

        assertThat(displayTextAction.location())
                .isEqualTo(RuleActionDisplayText.LOCATION_INDICATOR_WIDGET);
        assertThat(displayTextAction.content()).isEqualTo("test_content");
        assertThat(displayTextAction.data()).isEqualTo("test_data");
    }

    @Test
    public void throw_illegal_argument_exception_when_create_for_feedback_with_null_arguments() {
        try {
            RuleActionDisplayText.createForFeedback(null, null);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void throw_illegal_argument_exception_when_create_for_indicators_with_null_fields() {
        try {
            RuleActionDisplayText.createForIndicators(null, null);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void substitute_empty_strings_when_create_for_feedback_with_null_arguments() {
        RuleActionDisplayText ruleActionNoContent = RuleActionDisplayText
                .createForFeedback(null, "test_data");
        RuleActionDisplayText ruleActionNoData = RuleActionDisplayText
                .createForFeedback("test_content", null);

        assertThat(ruleActionNoContent.content()).isEqualTo("");
        assertThat(ruleActionNoContent.data()).isEqualTo("test_data");

        assertThat(ruleActionNoData.content()).isEqualTo("test_content");
        assertThat(ruleActionNoData.data()).isEqualTo("");
    }

    @Test
    public void substitute_empty_strings_when_create_for_indicators_with_null_arguments() {
        RuleActionDisplayText ruleActionNoContent = RuleActionDisplayText
                .createForIndicators(null, "test_data");
        RuleActionDisplayText ruleActionNoData = RuleActionDisplayText
                .createForIndicators("test_content", null);

        assertThat(ruleActionNoContent.content()).isEqualTo("");
        assertThat(ruleActionNoContent.data()).isEqualTo("test_data");

        assertThat(ruleActionNoData.content()).isEqualTo("test_content");
        assertThat(ruleActionNoData.data()).isEqualTo("");
    }

    @Test
    public void have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(RuleActionDisplayText.createForFeedback("", "").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
