package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleActionCreateEventShould {

    @Test
    public void substitute_empty_strings_for_null_arguments() {
        RuleActionCreateEvent ruleActionAssignNoContent = RuleActionCreateEvent
                .create(null, "test_data", "test_program_stage");
        RuleActionCreateEvent ruleActionAssignNoField = RuleActionCreateEvent
                .create("test_content", null, "test_program_stage");

        assertThat(ruleActionAssignNoContent.content()).isEqualTo("");
        assertThat(ruleActionAssignNoContent.data()).isEqualTo("test_data");
        assertThat(ruleActionAssignNoContent.programStage()).isEqualTo("test_program_stage");

        assertThat(ruleActionAssignNoField.content()).isEqualTo("test_content");
        assertThat(ruleActionAssignNoField.data()).isEqualTo("");
        assertThat(ruleActionAssignNoField.programStage()).isEqualTo("test_program_stage");
    }

    @Test
    public void throw_null_pointer_exception_when_field_is_null() {
        try {
            RuleActionCreateEvent.create("test_content", "test_data", null);
            fail("NullPointerException was expected, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(RuleActionCreateEvent
                .create("test_content", "test_data", "test_field").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
