package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleActionAssignShould {


    @Test
    public void substitute_empty_strings_for_null_arguments() {
        RuleActionAssign ruleActionAssignNoContent = RuleActionAssign
                .create(null, "test_data", "test_field");
        RuleActionAssign ruleActionAssignNoField = RuleActionAssign
                .create("test_content", "test_data", null);

        assertThat(ruleActionAssignNoContent.content()).isEqualTo("");
        assertThat(ruleActionAssignNoContent.data()).isEqualTo("test_data");
        assertThat(ruleActionAssignNoContent.field()).isEqualTo("test_field");

        assertThat(ruleActionAssignNoField.content()).isEqualTo("test_content");
        assertThat(ruleActionAssignNoField.data()).isEqualTo("test_data");
        assertThat(ruleActionAssignNoField.field()).isEqualTo("");
    }

    @Test
    public void throw_illegal_argument_exception_when_content_and_date_are_null() {
        try {
            RuleActionAssign.create(null, "test_data", null);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void throw_null_pointer_exception_when_data_is_null() {
        try {
            RuleActionAssign.create("test_content", null, "test_field");
            fail("NullPointerException was expected, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(RuleActionAssign
                .create("test_content", "test_data", "test_field").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
