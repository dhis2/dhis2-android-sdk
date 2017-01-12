package org.hisp.dhis.android.core.event;

import org.junit.Test;

public class EventModelTests {

    @Test(expected = IllegalStateException.class)
    public void build_throwOnNullUidField() {
        EventModel.builder().uid(null).build();
    }

    // ToDo: Consider re-evaluating usage of EqualsVerifier for store models
//    @Test
//    public void equals_shouldConformToContract() {
//        EqualsVerifier.forClass(EventModel.builder().uid("a1b2c3d4e5f").build().getClass())
//                .suppress(Warning.NULL_FIELDS)
//                .verify();
//    }
}
