package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

@RunWith(JUnit4.class)
public class RuleActionSetMandatoryFieldTests {

    @Test
    public void createMustThrowOnNullArgument() {
        try {
            RuleActionSetMandatoryField.create(null);
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void equalsAndHashcodeMustConformToContract() {
        EqualsVerifier.forClass(RuleActionSetMandatoryField.create("").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
