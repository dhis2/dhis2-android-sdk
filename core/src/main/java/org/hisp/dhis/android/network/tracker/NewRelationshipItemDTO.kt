/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.network.tracker

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemEnrollment
import org.hisp.dhis.android.core.relationship.RelationshipItemEvent
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance

@Serializable
internal data class NewRelationshipItemDTO(
    val relationship: String?,
    val relationshipItemType: String?,
    val trackedEntity: NewRelationshipItemTrackedEntityDTO?,
    val enrollment: NewRelationshipItemEnrollmentDTO?,
    val event: NewRelationshipItemEventDTO?,
) {
    fun toDomain(): RelationshipItem? {
        val builder = RelationshipItem.builder()
            .relationship(relationship?.let { ObjectWithUid.create(it) })
            .relationshipItemType(relationshipItemType?.let { RelationshipConstraintType.valueOf(it) })

        return when {
            trackedEntity != null ->
                builder.trackedEntityInstance(
                    RelationshipItemTrackedEntityInstance.builder()
                        .trackedEntityInstance(trackedEntity.trackedEntity)
                        .build(),
                ).build()

            enrollment != null ->
                builder.enrollment(
                    RelationshipItemEnrollment.builder()
                        .enrollment(enrollment.enrollment)
                        .build(),
                ).build()

            event != null ->
                builder.event(
                    RelationshipItemEvent.builder()
                        .event(event.event)
                        .build(),
                ).build()

            else -> null
        }
    }
}

internal fun NewTrackerImporterRelationshipItem.toDto(): NewRelationshipItemDTO {
    return NewRelationshipItemDTO(
        relationship = this.relationship(),
        relationshipItemType = this.relationshipItemType()?.name,
        trackedEntity = this.trackedEntity()?.toDto(),
        enrollment = this.enrollment()?.toDto(),
        event = this.event()?.toDto(),
    )
}
