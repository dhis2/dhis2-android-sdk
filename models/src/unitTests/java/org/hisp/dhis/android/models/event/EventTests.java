package org.hisp.dhis.android.models.event;

import org.hisp.dhis.android.models.trackedentity.TrackedEntityDataValue;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class EventTests {

    private static Event.Builder VALID_BUILDER;

    @Before
    public void setValidBuilder() {
        VALID_BUILDER = Event.builder()
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
    public void trackedEntityDataValues_shouldThrowOnCollectionMutations() {
        Event event = VALID_BUILDER
                .trackedEntityDataValues(Arrays.asList(
                        TrackedEntityDataValue.builder().build(),
                        TrackedEntityDataValue.builder().build()
                )).build();

        event.trackedEntityDataValues().add(TrackedEntityDataValue.builder().build());
    }
}
