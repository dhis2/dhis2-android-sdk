package org.hisp.dhis.android.core.organisationunit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

@RunWith(JUnit4.class)
public class OrganisationUnitUnitTests {

    @Test
    public void equals_shouldConformToContract() {
        EqualsVerifier.forClass(OrganisationUnitModel.builder().build().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
