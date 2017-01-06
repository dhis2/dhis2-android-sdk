package org.hisp.dhis.android.core.option;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class OptionTests {

    private static OptionModel.Builder VALID_BUILDER;

    @Before
    public void setValidBuilder() {
        VALID_BUILDER = OptionModel.builder()
                .uid("a1b2c3d4e5f");
    }

    //**************************************************************************************
    //
    // UID NULL TEST
    // THIS SHOULD BE LEGAL SINCE PROJECTION CAN BE SPECIFIED WITHOUT UID
    //
    //**************************************************************************************

    @Test
    public void build_buildWithNullUidField() {
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
