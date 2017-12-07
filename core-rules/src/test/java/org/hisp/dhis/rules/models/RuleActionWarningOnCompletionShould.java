package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleActionWarningOnCompletionShould {

    @Test
    public void substitute_empty_strings_when_create_with_null_arguments() {
        RuleActionWarningOnCompletion ruleActionNoContent = RuleActionWarningOnCompletion
                .create(null, "test_data", "test_field");
        RuleActionWarningOnCompletion ruleActionNoData = RuleActionWarningOnCompletion
                .create("test_content", null, "test_field");
        RuleActionWarningOnCompletion ruleActionNoField = RuleActionWarningOnCompletion
                .create("test_content", "test_data", null);

        assertThat(ruleActionNoContent.content()).isEqualTo("");
        assertThat(ruleActionNoContent.data()).isEqualTo("test_data");
        assertThat(ruleActionNoContent.field()).isEqualTo("test_field");

        assertThat(ruleActionNoData.content()).isEqualTo("test_content");
        assertThat(ruleActionNoData.data()).isEqualTo("");
        assertThat(ruleActionNoData.field()).isEqualTo("test_field");

        assertThat(ruleActionNoField.content()).isEqualTo("test_content");
        assertThat(ruleActionNoField.data()).isEqualTo("test_data");
        assertThat(ruleActionNoField.field()).isEqualTo("");
    }

    @Test
    public void throw_illegal_argument_exception_when_create_with_null_content_data_field() {
        try {
            RuleActionWarningOnCompletion.create(null, null, null);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(RuleActionWarningOnCompletion
                .create("test_content", "test_data", "test_field").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
