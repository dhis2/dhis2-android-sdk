/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.relationship;

import org.junit.Test;

public class RelationshipItemShould {

    @Test(expected = IllegalArgumentException.class)
    public void fail_in_instantiation_with_no_elements() {
        RelationshipItem.builder().build();
    }

    @Test()
    public void succeed_in_instantiation_with_tei() {
        RelationshipItem
                .builder()
                .trackedEntityInstance(
                        RelationshipItemTrackedEntityInstance
                                .builder()
                                .trackedEntityInstance("uid")
                                .build()
                )
                .build();
    }

    @Test()
    public void succeed_in_instantiation_with_enrollment() {
        RelationshipItem
                .builder()
                .enrollment(
                        RelationshipItemEnrollment
                                .builder()
                                .enrollment("uid")
                                .build()
                )
                .build();
    }

    @Test()
    public void succeed_in_instantiation_with_event() {
        RelationshipItem
                .builder()
                .event(
                        RelationshipItemEvent
                                .builder()
                                .event("uid")
                                .build()
                )
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_in_instantiation_with_tei_and_enrollment() {
        RelationshipItem
                .builder()
                .trackedEntityInstance(
                        RelationshipItemTrackedEntityInstance
                                .builder()
                                .trackedEntityInstance("uid")
                                .build()
                )
                .enrollment(
                        RelationshipItemEnrollment
                                .builder()
                                .enrollment("uid")
                                .build()
                )
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_in_instantiation_with_tei_and_event() {
        RelationshipItem
                .builder()
                .trackedEntityInstance(
                        RelationshipItemTrackedEntityInstance
                                .builder()
                                .trackedEntityInstance("uid")
                                .build()
                )
                .event(
                        RelationshipItemEvent
                                .builder()
                                .event("uid")
                                .build()
                )
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_in_instantiation_with_enrollment_and_event() {
        RelationshipItem
                .builder()
                .enrollment(
                        RelationshipItemEnrollment
                                .builder()
                                .enrollment("uid")
                                .build()
                )
                .event(
                        RelationshipItemEvent
                                .builder()
                                .event("uid")
                                .build()
                )
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_in_instantiation_with_tei_enrollment_and_event() {
        RelationshipItem
                .builder()
                .trackedEntityInstance(
                        RelationshipItemTrackedEntityInstance
                                .builder()
                                .trackedEntityInstance("uid")
                                .build()
                )
                .enrollment(
                        RelationshipItemEnrollment
                                .builder()
                                .enrollment("uid")
                                .build()
                )
                .event(
                        RelationshipItemEvent
                                .builder()
                                .event("uid")
                                .build()
                )
                .build();
    }
}