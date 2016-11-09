package org.hisp.dhis.client.models.dataelement;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;

public class DataElementTests {

    private static DataElement.Builder VALID_BUILDER;

    @Before
    public void setUp() {
        VALID_BUILDER = createValidBuilder();
    }

    private DataElement.Builder createValidBuilder() {
        return DataElement.builder()
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

    @Test
    public void isValid_shouldReturnFalseOnMalformedUid() {
        DataElement dataElementWithEmptyUid = VALID_BUILDER.uid("").build();
        DataElement dataElementWithShortUid = VALID_BUILDER.uid("a1b2c3d4e5").build();
        DataElement dataElementWithLongUid = VALID_BUILDER.uid("a1b2c3d4e5ff").build();

        assertThat(dataElementWithEmptyUid.uid().length()).isEqualTo(0);
        assertThat(dataElementWithShortUid.uid().length()).isEqualTo(10);
        assertThat(dataElementWithLongUid.uid().length()).isEqualTo(12);

        assertThat(dataElementWithEmptyUid.isValid()).isFalse();  // corner case: empty string
        assertThat(dataElementWithShortUid.isValid()).isFalse();  // uid of 10 chars long
        assertThat(dataElementWithLongUid.isValid()).isFalse();   // uid of 12 chars long
    }

    @Test
    public void isValid_shouldReturnFalseOnNullCreatedField() {
        DataElement dataElement = VALID_BUILDER.created(null).build();
        assertThat(dataElement.created()).isNull();
        assertThat(dataElement.isValid()).isFalse();
    }

    @Test
    public void isValid_shouldReturnFalseOnNullLastUpdatedField() {
        DataElement dataElement = VALID_BUILDER.lastUpdated(null).build();
        assertThat(dataElement.lastUpdated()).isNull();
        assertThat(dataElement.isValid()).isFalse();
    }

    @Test
    public void isValid_shouldReturnTrueOnValidObject() {
        DataElement dataElement = VALID_BUILDER.build();
        assertThat(dataElement.isValid()).isTrue();
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
