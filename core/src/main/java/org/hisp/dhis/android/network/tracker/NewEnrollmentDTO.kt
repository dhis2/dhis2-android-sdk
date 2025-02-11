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
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.network.common.dto.BaseDeletableDataObjectDTO
import org.hisp.dhis.android.network.common.dto.GeometryDTO
import org.hisp.dhis.android.network.common.dto.toDto

@Serializable
internal data class NewEnrollmentDTO(
    override val deleted: Boolean?,
    val enrollment: String,
    val createdAt: String?,
    val updatedAt: String?,
    val createdAtClient: String?,
    val updatedAtClient: String?,
    val orgUnit: String?,
    val program: String?,
    val enrolledAt: String?,
    val occurredAt: String?,
    val completedAt: String?,
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
            created(createdAt.toJavaDate())
            lastUpdated(updatedAt.toJavaDate())
            createdAtClient(createdAtClient.toJavaDate())
            lastUpdatedAtClient(updatedAtClient.toJavaDate())
            organisationUnit(orgUnit)
            program(program)
            enrollmentDate(enrolledAt.toJavaDate())
            incidentDate(occurredAt.toJavaDate())
            completedDate(completedAt.toJavaDate())
            followUp(followUp)
            status(status?.let { EnrollmentStatus.valueOf(it) })
            trackedEntityInstance(trackedEntity)
            geometry(geometry?.toDomain())
            notes(notes?.map { it.toDomain() })
            EnrollmentInternalAccessor.insertEvents(this, events?.map { it.toDomain() })
            relationships(relationships?.map { it.toDomain() })
        }.build()
    }
}


internal fun NewTrackerImporterEnrollment.toDto(): NewEnrollmentDTO {
    return NewEnrollmentDTO(
        enrollment = this.uid(),
        deleted = this.deleted(),
        createdAt = this.createdAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAt = this.updatedAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        createdAtClient = this.createdAtClient()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAtClient = this.updatedAtClient()?.let { DateUtils.DATE_FORMAT.format(it) },
        orgUnit = this.organisationUnit(),
        program = this.program(),
        enrolledAt = this.enrolledAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        occurredAt = this.occurredAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        completedAt = this.completedAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        followUp = this.followUp(),
        status = this.status()?.name,
        trackedEntity = this.trackedEntity(),
        geometry = this.geometry()?.let { it.toDto() },
        attributes = this.attributes()?.map { it.toDto() } ?: emptyList(),
        events = this.events()?.map { it.toDto() },
        notes = this.notes()?.map { it.toDto() },
    )
}
