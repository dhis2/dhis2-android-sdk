package org.hisp.dhis.android.core.dataelement;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class CategoryTests {

    private static Category.Builder VALID_BUILDER;

    @Before
    public void setUp() {
        VALID_BUILDER = createValidBuilder();
    }

    //**************************************************************************************
    //
    // BASE IDENTIFIABLE OBJECT TESTS
    //
    //**************************************************************************************

    @Test(expected = IllegalStateException.class)
    public void build_shouldThrowOnNullUidField() {
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
    public void categoryOptions_shouldThrowOnCollectionMutations() {
        Category category = VALID_BUILDER
                .categoryOptions(Arrays.asList(
                        CategoryOption.builder().uid("catOptUid01").build(),
                        CategoryOption.builder().uid("catOptUid02").build()))
                .build();

        category.categoryOptions().add(CategoryOption.builder().uid("catOptUid03").build());
    }

    //**************************************************************************************
    //
    // CREATE VALID BUILDER OBJECT
    //
    //**************************************************************************************

    private Category.Builder createValidBuilder() {
        return Category.builder()
                .uid("a1b2c3d4e5f")
                .created(new java.util.Date())
                .lastUpdated(new java.util.Date());
    }
}