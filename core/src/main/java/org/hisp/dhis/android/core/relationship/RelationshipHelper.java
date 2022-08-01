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

import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;

public final class RelationshipHelper {

    public static String getTeiUid(RelationshipItem item) {
        if (item != null && item.trackedEntityInstance() != null) {
            return item.trackedEntityInstance().trackedEntityInstance();
        }
        return null;
    }

    public static RelationshipItem teiItem(String uid) {
        return RelationshipItem.builder().trackedEntityInstance(
                RelationshipItemTrackedEntityInstance
                        .builder()
                        .trackedEntityInstance(uid)
                        .build()
        ).build();
    }

    public static RelationshipItem enrollmentItem(String uid) {
        return RelationshipItem.builder().enrollment(
                RelationshipItemEnrollment
                        .builder()
                        .enrollment(uid)
                        .build()
        ).build();
    }

    public static RelationshipItem eventItem(String uid) {
        return RelationshipItem.builder().event(
                RelationshipItemEvent
                        .builder()
                        .event(uid)
                        .build()
        ).build();
    }

    public static Relationship teiToTeiRelationship(String fromUid, String toUid, String relationshipTypeUid) {
        return relationship(
                RelationshipHelper.teiItem(fromUid),
                RelationshipHelper.teiItem(toUid),
                relationshipTypeUid);
    }

    public static Relationship teiToEnrollmentRelationship(String fromUid, String toUid, String relationshipTypeUid) {
        return relationship(
                RelationshipHelper.teiItem(fromUid),
                RelationshipHelper.enrollmentItem(toUid),
                relationshipTypeUid);
    }

    public static Relationship teiToEventRelationship(String fromUid, String toUid, String relationshipTypeUid) {
        return relationship(
                RelationshipHelper.teiItem(fromUid),
                RelationshipHelper.eventItem(toUid),
                relationshipTypeUid);
    }

    public static Relationship enrollmentToTeiRelationship(String fromUid, String toUid, String relationshipTypeUid) {
        return relationship(
                RelationshipHelper.enrollmentItem(fromUid),
                RelationshipHelper.teiItem(toUid),
                relationshipTypeUid);
    }

    public static Relationship enrollmentToEnrollmentRelationship(String fromUid,
                                                                  String toUid,
                                                                  String relationshipTypeUid) {
        return relationship(
                RelationshipHelper.enrollmentItem(fromUid),
                RelationshipHelper.enrollmentItem(toUid),
                relationshipTypeUid);
    }

    public static Relationship enrollmentToEventRelationship(String fromUid, String toUid, String relationshipTypeUid) {
        return relationship(
                RelationshipHelper.enrollmentItem(fromUid),
                RelationshipHelper.eventItem(toUid),
                relationshipTypeUid);
    }

    public static Relationship eventToTeiRelationship(String fromUid, String toUid, String relationshipTypeUid) {
        return relationship(
                RelationshipHelper.eventItem(fromUid),
                RelationshipHelper.teiItem(toUid),
                relationshipTypeUid);
    }

    public static Relationship eventToEnrollmentRelationship(String fromUid, String toUid, String relationshipTypeUid) {
        return relationship(
                RelationshipHelper.eventItem(fromUid),
                RelationshipHelper.enrollmentItem(toUid),
                relationshipTypeUid);
    }

    public static Relationship eventToEventRelationship(String fromUid, String toUid, String relationshipTypeUid) {
        return relationship(
                RelationshipHelper.eventItem(fromUid),
                RelationshipHelper.eventItem(toUid),
                relationshipTypeUid);
    }

    public static Relationship relationship(RelationshipItem from, RelationshipItem to, String type) {
        return Relationship.builder()
                .uid(new UidGeneratorImpl().generate())
                .from(from)
                .to(to)
                .relationshipType(type)
                .build();
    }

    public static boolean areItemsEqual(RelationshipItem a, RelationshipItem b) {
        return equalsConsideringNull(a.event(), b.event())
                && equalsConsideringNull(a.enrollment(), b.enrollment())
                && equalsConsideringNull(a.trackedEntityInstance(), b.trackedEntityInstance());
    }

    private static <O> boolean equalsConsideringNull(@Nullable O a, @Nullable O b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
    }

    private RelationshipHelper() {
    }
}