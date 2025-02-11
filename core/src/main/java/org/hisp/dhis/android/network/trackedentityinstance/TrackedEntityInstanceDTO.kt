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

package org.hisp.dhis.android.network.trackedentityinstance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.BaseDeletableDataObjectDTO
import org.hisp.dhis.android.network.common.dto.GeometryDTO
import org.hisp.dhis.android.network.common.dto.PagerDTO
import org.hisp.dhis.android.network.common.dto.toDto
import org.hisp.dhis.android.network.enrollment.EnrollmentDTO
import org.hisp.dhis.android.network.enrollment.toDto
import org.hisp.dhis.android.network.relationship.RelationshipDTO
import org.hisp.dhis.android.network.relationship.toDto

@Serializable
internal data class TrackedEntityInstanceDTO(
    override val deleted: Boolean?,
    val trackedEntityInstance: String,
    val created: String?,
    val lastUpdated: String?,
    val createdAtClient: String?,
    val lastUpdatedAtClient: String?,
    val orgUnit: String?,
    val trackedEntityType: String?,
    val geometry: GeometryDTO?,
    val attributes: List<TrackedEntityAttributeValueDTO>?,
    val relationships: List<RelationshipDTO>?,
    val enrollments: List<EnrollmentDTO>?,
    val programOwners: List<ProgramOwnerDTO>?,
) : BaseDeletableDataObjectDTO {
    fun toDomain(): TrackedEntityInstance {
        return TrackedEntityInstance.builder().apply {
            deleted(deleted)
            uid(trackedEntityInstance)
            created(created.toJavaDate())
            lastUpdated(lastUpdated.toJavaDate())
            createdAtClient(createdAtClient.toJavaDate())
            lastUpdatedAtClient(lastUpdatedAtClient.toJavaDate())
            organisationUnit(orgUnit)
            trackedEntityType(trackedEntityType)
            geometry(geometry?.toDomain())
            trackedEntityAttributeValues(attributes?.map { it.toDomain(trackedEntityInstance) })
            TrackedEntityInstanceInternalAccessor.insertRelationships(this, relationships?.map { it.toDomain() })
            TrackedEntityInstanceInternalAccessor.insertEnrollments(this, enrollments?.map { it.toDomain() })
            programOwners(programOwners?.map { it.toDomain() })
        }.build()
    }
}

internal fun TrackedEntityInstance.toDto(): TrackedEntityInstanceDTO {
    return TrackedEntityInstanceDTO(
        deleted = deleted(),
        trackedEntityInstance = uid(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        createdAtClient = createdAtClient().dateFormat(),
        lastUpdatedAtClient = lastUpdatedAtClient().dateFormat(),
        orgUnit = organisationUnit(),
        trackedEntityType = trackedEntityType(),
        geometry = geometry()?.toDto(),
        attributes = trackedEntityAttributeValues()?.map { it.toDto() },
        relationships = TrackedEntityInstanceInternalAccessor.accessRelationships(this)?.map { it.toDto() },
        enrollments = TrackedEntityInstanceInternalAccessor.accessEnrollments(this)?.map { it.toDto() },
        programOwners = programOwners()?.map { it.toDto() },
    )
}

@Serializable
internal class TrackedEntityInstancePayload(
    override val pager: PagerDTO? = null,
    @SerialName("trackedEntityInstances") override val items: List<TrackedEntityInstanceDTO> = emptyList(),
) : PayloadJson<TrackedEntityInstanceDTO>(pager, items)
