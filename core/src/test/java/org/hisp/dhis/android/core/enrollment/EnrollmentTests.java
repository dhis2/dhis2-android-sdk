package org.hisp.dhis.android.core.enrollment;

public class EnrollmentTests {

    /*TODO: fix this: Use new Create method instead of Builder

    private static Enrollment.Builder VALID_BUILDER;

    @Before public void setUp() {
        VALID_BUILDER = createValidBuilder();
    }

    private Enrollment.Builder createValidBuilder() {
        return Enrollment.builder()
                .uid("a1b2c3d4e5f");
    }

    //**************************************************************************************
    //
    // UID NULL TEST
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
                        TrackedEntityAttributeValue.builder().build(),
                        TrackedEntityAttributeValue.builder().build()))
                .build();

        enrollment.trackedEntityAttributeValues().add(TrackedEntityAttributeValue.builder().build());
    }*/
}
