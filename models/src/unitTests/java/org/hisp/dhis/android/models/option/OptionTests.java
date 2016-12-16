package org.hisp.dhis.android.models.option;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class OptionTests {

    private static Option.Builder VALID_BUILDER;

    @Before
    public void setValidBuilder() {
        VALID_BUILDER = Option.builder()
                .uid("a1b2c3d4e5f");
    }

    //**************************************************************************************
    //
    // UID NULL TEST
    //
    //**************************************************************************************

    @Test(expected = IllegalStateException.class)
    public void build_throwOnNullUidField() {
        VALID_BUILDER.uid(null).build();
    }

    //**************************************************************************************
    //
    // EQUALS VERIFIER
    //
    //**************************************************************************************

    @Test
    public void equals_shouldConformToContract() {
        EqualsVerifier.forClass(VALID_BUILDER.build().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
