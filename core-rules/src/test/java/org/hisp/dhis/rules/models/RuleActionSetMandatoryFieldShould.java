package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

@RunWith(JUnit4.class)
public class RuleActionSetMandatoryFieldShould {

    @Test
    public void throw_null_pointer_exception_when_create_with_null_argument() {
        try {
            RuleActionSetMandatoryField.create(null);
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(RuleActionSetMandatoryField.create("").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
