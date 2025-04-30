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
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.network.common.dto.BaseDeletableDataObjectDTO
import org.hisp.dhis.android.network.common.dto.DateStringDTO
import org.hisp.dhis.android.network.common.dto.GeometryDTO
import org.hisp.dhis.android.network.common.dto.toDto

@Serializable
internal data class NewEnrollmentDTO(
    override val deleted: Boolean?,
    val enrollment: String,
    val createdAt: DateStringDTO?,
    val updatedAt: DateStringDTO?,
    val createdAtClient: DateStringDTO?,
    val updatedAtClient: DateStringDTO?,
    val orgUnit: String?,
    val program: String?,
    val enrolledAt: DateStringDTO?,
    val occurredAt: DateStringDTO?,
    val completedAt: DateStringDTO?,
    val followUp: Boolean?,
    val status: String?,
    val trackedEntity: String?,
    val geometry: GeometryDTO?,
    val attributes: List<NewTrackedEntityAttributeValueDTO> = emptyList(),
    val events: List<NewEventDTO>?,
    val notes: List<NewNoteDTO>?,
    val relationships: List<NewRelationshipDTO>? = null,
) : BaseDeletableDataObjectDTO {
    fun toDomain(): Enrollment {
        return Enrollment.builder().apply {
            uid(enrollment)
            deleted(deleted)
            created(createdAt?.toDomain())
            lastUpdated(updatedAt?.toDomain())
            createdAtClient(createdAtClient?.toDomain())
            lastUpdatedAtClient(updatedAtClient?.toDomain())
            organisationUnit(orgUnit)
            program(program)
            enrollmentDate(enrolledAt?.toDomain())
            incidentDate(occurredAt?.toDomain())
            completedDate(completedAt?.toDomain())
            followUp(followUp)
            status(status?.let { EnrollmentStatus.valueOf(it) })
            trackedEntityInstance(trackedEntity)
            geometry(geometry?.toDomain())
            notes(notes?.map { it.toDomain(enrollment = enrollment) })
            EnrollmentInternalAccessor.insertEvents(this, events?.map { it.toDomain() })
            relationships(relationships?.map { it.toDomain() })
        }.build()
    }
}

internal fun NewTrackerImporterEnrollment.toDto(): NewEnrollmentDTO {
    return NewEnrollmentDTO(
        enrollment = uid,
        deleted = deleted,
        createdAt = createdAt?.toDto(),
        updatedAt = updatedAt?.toDto(),
        createdAtClient = createdAtClient?.toDto(),
        updatedAtClient = updatedAtClient?.toDto(),
        orgUnit = organisationUnit,
        program = program,
        enrolledAt = enrolledAt?.toDto(),
        occurredAt = occurredAt?.toDto(),
        completedAt = completedAt?.toDto(),
        followUp = followUp,
        status = status?.name,
        trackedEntity = trackedEntity,
        geometry = geometry?.let { it.toDto() },
        attributes = attributes?.map { it.toDto() } ?: emptyList(),
        events = events?.map { it.toDto() },
        notes = notes?.map { it.toDto() },
    )
}
