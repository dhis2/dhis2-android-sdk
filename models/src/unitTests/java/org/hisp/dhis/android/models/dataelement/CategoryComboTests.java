package org.hisp.dhis.android.models.dataelement;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class CategoryComboTests {

    private static CategoryCombo.Builder VALID_BUILDER;

    @Before
    public void setUp() {
        VALID_BUILDER = createValidBuilder();
    }

    private CategoryCombo.Builder createValidBuilder() {
        return CategoryCombo.builder()
                .uid("a1b2c3d4e5f")
                .created(new java.util.Date())
                .lastUpdated(new java.util.Date());
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
    public void categories_shouldThrowOnCollectionMutations() {
        CategoryCombo categoryCombo = VALID_BUILDER
                .categories(Arrays.asList(
                        Category.builder().uid("categoryId1").build(),
                        Category.builder().uid("categoryId2").build()))
                .build();

        categoryCombo.categories().add(Category.builder().uid("categoryUid3").build());
    }
}