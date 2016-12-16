package org.hisp.dhis.android.models.option;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class OptionSetTests {

    private static OptionSet.Builder VALID_BUILDER;

    @Before
    public void setValidBuilder() {
        VALID_BUILDER = OptionSet.builder()
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

    //**************************************************************************************
    //
    // COLLECTION MUTATION TESTS
    //
    //**************************************************************************************

    @Test(expected = UnsupportedOperationException.class)
    public void options_shouldThrowOnCollectionMutations() {
        OptionSet optionSet = VALID_BUILDER
                .options(Arrays.asList(
                        Option.builder().uid("optionUid01").build(),
                        Option.builder().uid("optionUid02").build()
                ))
                .build();

        optionSet.options().add(Option.builder().uid("optionUid03").build());
    }
}
