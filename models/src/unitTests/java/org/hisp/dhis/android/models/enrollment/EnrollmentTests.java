package org.hisp.dhis.android.models.enrollment;

import org.hisp.dhis.android.models.trackedentity.TrackedEntityAttributeValue;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class EnrollmentTests {

    private static Enrollment.Builder VALID_BUILDER;

    @Before public void setUp() {
        VALID_BUILDER = createValidBuilder();
    }

    private Enrollment.Builder createValidBuilder() {
        return Enrollment.builder()
                .uid("a1b2c3d4e5f")
                .created(new java.util.Date())
                .lastUpdated(new java.util.Date());
    }

    //**************************************************************************************
    //
    // NULL UID TEST
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
    public void trackedEntityAttributeValues_shouldThrowOnCollectionMutations() {
        Enrollment enrollment = VALID_BUILDER
                .trackedEntityAttributeValues(Arrays.asList(
                        TrackedEntityAttributeValue.builder().trackedEntityAttribute("att1").value("value1").build(),
                        TrackedEntityAttributeValue.builder().trackedEntityAttribute("att2").value("value2").build()))
                .build();

        enrollment.trackedEntityAttributeValues().add(TrackedEntityAttributeValue.builder().trackedEntityAttribute("att3").value("value3").build());
    }
}
