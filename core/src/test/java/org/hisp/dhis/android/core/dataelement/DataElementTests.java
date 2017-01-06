package org.hisp.dhis.android.core.dataelement;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class DataElementTests {

    private static DataElementModel.Builder VALID_BUILDER;

    @Before
    public void setUp() {
        VALID_BUILDER = createValidBuilder();
    }

    private DataElementModel.Builder createValidBuilder() {
        return DataElementModel.builder()
                .uid("a1b2c3d4e5f")
                .created(new java.util.Date())
                .lastUpdated(new java.util.Date());
    }

    //**************************************************************************************
    //
    // BASE IDENTIFIABLE OBJECT TESTS
    // THIS SHOULD BE LEGAL SINCE PROJECTION CAN BE SPECIFIED WITHOUT UID
    //
    //**************************************************************************************

    public void build_withNullUidField() {
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
