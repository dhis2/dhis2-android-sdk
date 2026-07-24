/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core.relationship

import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl

@Suppress("TooManyFunctions")
object RelationshipHelper {

    @JvmStatic
    fun getTeiUid(item: RelationshipItem?): String? =
        item?.trackedEntityInstance()?.trackedEntityInstance()

    @JvmStatic
    fun teiItem(uid: String): RelationshipItem = RelationshipItem.builder()
        .trackedEntityInstance(
            RelationshipItemTrackedEntityInstance.builder()
                .trackedEntityInstance(uid)
                .build(),
        )
        .build()

    @JvmStatic
    fun enrollmentItem(uid: String): RelationshipItem = RelationshipItem.builder()
        .enrollment(
            RelationshipItemEnrollment.builder()
                .enrollment(uid)
                .build(),
        )
        .build()

    @JvmStatic
    fun eventItem(uid: String): RelationshipItem = RelationshipItem.builder()
        .event(
            RelationshipItemEvent.builder()
                .event(uid)
                .build(),
        )
        .build()

    @JvmStatic
    fun teiToTeiRelationship(fromUid: String, toUid: String, relationshipTypeUid: String): Relationship =
        relationship(teiItem(fromUid), teiItem(toUid), relationshipTypeUid)

    @JvmStatic
    fun teiToEnrollmentRelationship(fromUid: String, toUid: String, relationshipTypeUid: String): Relationship =
        relationship(teiItem(fromUid), enrollmentItem(toUid), relationshipTypeUid)

    @JvmStatic
    fun teiToEventRelationship(fromUid: String, toUid: String, relationshipTypeUid: String): Relationship =
        relationship(teiItem(fromUid), eventItem(toUid), relationshipTypeUid)

    @JvmStatic
    fun enrollmentToTeiRelationship(fromUid: String, toUid: String, relationshipTypeUid: String): Relationship =
        relationship(enrollmentItem(fromUid), teiItem(toUid), relationshipTypeUid)

    @JvmStatic
    fun enrollmentToEnrollmentRelationship(fromUid: String, toUid: String, relationshipTypeUid: String): Relationship =
        relationship(enrollmentItem(fromUid), enrollmentItem(toUid), relationshipTypeUid)

    @JvmStatic
    fun enrollmentToEventRelationship(fromUid: String, toUid: String, relationshipTypeUid: String): Relationship =
        relationship(enrollmentItem(fromUid), eventItem(toUid), relationshipTypeUid)

    @JvmStatic
    fun eventToTeiRelationship(fromUid: String, toUid: String, relationshipTypeUid: String): Relationship =
        relationship(eventItem(fromUid), teiItem(toUid), relationshipTypeUid)

    @JvmStatic
    fun eventToEnrollmentRelationship(fromUid: String, toUid: String, relationshipTypeUid: String): Relationship =
        relationship(eventItem(fromUid), enrollmentItem(toUid), relationshipTypeUid)

    @JvmStatic
    fun eventToEventRelationship(fromUid: String, toUid: String, relationshipTypeUid: String): Relationship =
        relationship(eventItem(fromUid), eventItem(toUid), relationshipTypeUid)

    @JvmStatic
    fun relationship(from: RelationshipItem, to: RelationshipItem, type: String): Relationship =
        Relationship.builder()
            .uid(UidGeneratorImpl().generate())
            .from(from)
            .to(to)
            .relationshipType(type)
            .build()

    @JvmStatic
    fun areItemsEqual(a: RelationshipItem?, b: RelationshipItem?): Boolean =
        a?.event() == b?.event() &&
            a?.enrollment() == b?.enrollment() &&
            a?.trackedEntityInstance() == b?.trackedEntityInstance()
}
