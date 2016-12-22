package org.hisp.dhis.android.core.option;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

@RunWith(JUnit4.class)
public class OptionUnitTests {

    @Test
    public void equals_shouldConformToContract() {
        EqualsVerifier.forClass(OptionModel.builder().build().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
