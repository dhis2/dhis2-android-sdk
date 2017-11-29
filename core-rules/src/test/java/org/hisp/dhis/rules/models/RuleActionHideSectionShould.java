package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleActionHideSectionShould {

    @Test
    public void throw_null_pointer_exception_when_create_with_null_field() {
        try {
            RuleActionHideSection.create(null);
            fail("NullPointerException was expected, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(RuleActionHideSection.create("test_field").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
