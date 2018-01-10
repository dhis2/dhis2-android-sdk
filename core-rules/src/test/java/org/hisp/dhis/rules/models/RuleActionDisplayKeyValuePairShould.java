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
    public void substitute_correct_location_when_create_for_feedback() {
        RuleActionDisplayKeyValuePair displayTextAction = RuleActionDisplayKeyValuePair
                .createForFeedback("test_content", "test_data");

        assertThat(displayTextAction.location())
                .isEqualTo(RuleActionDisplayKeyValuePair.LOCATION_FEEDBACK_WIDGET);
        assertThat(displayTextAction.content()).isEqualTo("test_content");
        assertThat(displayTextAction.data()).isEqualTo("test_data");
    }

    @Test
    public void substitute_correct_location_when_create_for_indicators() {
        RuleActionDisplayKeyValuePair displayTextAction = RuleActionDisplayKeyValuePair
                .createForIndicators("test_content", "test_data");

        assertThat(displayTextAction.location())
                .isEqualTo(RuleActionDisplayKeyValuePair.LOCATION_INDICATOR_WIDGET);
        assertThat(displayTextAction.content()).isEqualTo("test_content");
        assertThat(displayTextAction.data()).isEqualTo("test_data");
    }

    @Test
    public void throw_illegal_argument_exception_when_create_for_feedback_with_null_arguments() {
        try {
            RuleActionDisplayKeyValuePair.createForFeedback(null, null);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void throw_illegal_argument_exception_when_create_for_indicators_with_null_fields() {
        try {
            RuleActionDisplayKeyValuePair.createForIndicators(null, null);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void substitute_empty_strings_when_create_for_feedback_with_null_arguments() {
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
    public void substitute_empty_strings_when_create_for_indicators_with_null_arguments() {
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
    public void have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(RuleActionDisplayKeyValuePair.createForFeedback("", "").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
