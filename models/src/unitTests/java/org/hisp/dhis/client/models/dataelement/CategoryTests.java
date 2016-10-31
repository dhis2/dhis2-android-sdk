package org.hisp.dhis.client.models.dataelement;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void isValid_shouldReturnFalseOnMalformedUid() {
        Category categoryWithEmptyUid = VALID_BUILDER.uid("").build();
        Category categoryWithShortUid = VALID_BUILDER.uid("a1b2c3d4e5").build();
        Category categoryWithLongUid =  VALID_BUILDER.uid("a1b2c3d4e5ff").build();

        assertThat(categoryWithEmptyUid.isValid()).isFalse();   // corner case: empty string
        assertThat(categoryWithShortUid.isValid()).isFalse();   // uid of 10 chars long
        assertThat(categoryWithLongUid.isValid()).isFalse();    // uid of 12 chars long
    }

    @Test
    public void isValid_shouldReturnFalseOnNullCreatedField() {
        Category category = VALID_BUILDER
                .created(null).build();
        assertThat(category.isValid()).isFalse();
    }

    @Test
    public void isValid_shouldReturnFalseOnNullLastUpdatedField() {
        Category category = VALID_BUILDER
                .lastUpdated(null).build();
        assertThat(category.isValid()).isFalse();
    }

    @Test
    public void isValid_shouldReturnTrueOnValidObject() {
        Category category = VALID_BUILDER.build();
        assertThat(category.isValid()).isTrue();
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