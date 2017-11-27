package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleActionHideFieldShould {

    @Test
    public void createMustSubstituteEmptyStringIfArgumentsNull() {
        RuleActionHideField ruleActionHideField =
                RuleActionHideField.create(null, "test_field");

        assertThat(ruleActionHideField.content()).isEqualTo("");
    }

    @Test
    public void createMustThrowOnNullField() {
        try {
            RuleActionHideField.create("test_content", null);
            fail("NullPointerException was expected, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void equalsAndHashCodeFunctionsMustConformContract() {
        EqualsVerifier.forClass(RuleActionHideField.create("test_content", "test_field").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
