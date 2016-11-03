package org.hisp.dhis.client.models.dataelement;

import org.hisp.dhis.client.models.BaseIdendifiableObjectTestMethods;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryComboTests implements BaseIdendifiableObjectTestMethods {

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
    
    @Override
    @Test(expected = IllegalStateException.class)
    public void build_shouldThrowOnNullUidField() {
        VALID_BUILDER.uid(null).build();
    }

    @Override
    @Test
    public void isValid_shouldReturnFalseOnMalformedUid() {
        CategoryCombo categoryComboWithEmptyUid = VALID_BUILDER.uid("").build();
        CategoryCombo categoryComboWithShortUid = VALID_BUILDER.uid("a1b2c3d4e5").build();
        CategoryCombo categoryComboWithLongUid =  VALID_BUILDER.uid("a1b2c3d4e5ff").build();

        assertThat (categoryComboWithEmptyUid.uid().length()) .isEqualTo(0);
        assertThat (categoryComboWithShortUid.uid().length()) .isEqualTo(10);
        assertThat (categoryComboWithLongUid.uid().length())  .isEqualTo(12);

        assertThat(categoryComboWithEmptyUid.isValid()).isFalse();  // corner case: empty string
        assertThat(categoryComboWithShortUid.isValid()).isFalse();  // uid of 10 chars long
        assertThat(categoryComboWithLongUid.isValid()).isFalse();   // uid of 12 chars long
    }

    @Override
    @Test
    public void isValid_shouldReturnFalseOnNullCreatedField() {
        CategoryCombo categoryCombo = VALID_BUILDER.created(null).build();
        assertThat(categoryCombo.created()).isNull();
        assertThat(categoryCombo.isValid()).isFalse();
    }

    @Override
    @Test
    public void isValid_shouldReturnFalseOnNullLastUpdatedField() {
        CategoryCombo categoryCombo = VALID_BUILDER.lastUpdated(null).build();
        assertThat(categoryCombo.lastUpdated()).isNull();
        assertThat(categoryCombo.isValid()).isFalse();
    }

    @Override
    @Test
    public void isValid_shouldReturnTrueOnValidObject() {
        CategoryCombo categoryCombo = VALID_BUILDER.build();
        assertThat(categoryCombo.isValid()).isTrue();
    }

    //**************************************************************************************
    //
    // EQUALS VERIFIER
    //
    //**************************************************************************************

    @Override
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