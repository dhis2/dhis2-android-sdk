package org.hisp.dhis.android.core.program;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ProgramStageSectionUnitTests {
    @Test
    public void equals_shouldConformToContract() {
        EqualsVerifier.forClass(ProgramStageSectionModel.builder().build().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
