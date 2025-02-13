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

package org.hisp.dhis.android.network.enrollment

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.network.common.dto.BaseDeletableDataObjectDTO
import org.hisp.dhis.android.network.common.dto.GeometryDTO
import org.hisp.dhis.android.network.common.dto.toDto
import org.hisp.dhis.android.network.event.EventDTO
import org.hisp.dhis.android.network.event.toDto
import org.hisp.dhis.android.network.note.NoteDTO
import org.hisp.dhis.android.network.note.toDto
import org.hisp.dhis.android.network.relationship.RelationshipDTO
import org.hisp.dhis.android.network.relationship.toDto

@Serializable
internal data class EnrollmentDTO(
    override val deleted: Boolean?,
    val enrollment: String,
    val created: String?,
    val lastUpdated: String?,
    val createdAtClient: String?,
    val lastUpdatedAtClient: String?,
    val orgUnit: String?,
    val program: String?,
    val enrollmentDate: String?,
    val incidentDate: String?,
    val completedDate: String?,
    val followup: Boolean?,
    val status: String?,
    val trackedEntityInstance: String?,
    val geometry: GeometryDTO?,
    val events: List<EventDTO>?,
    val notes: List<NoteDTO>?,
    val relationships: List<RelationshipDTO>?,
) : BaseDeletableDataObjectDTO {
    fun toDomain(): Enrollment {
        return Enrollment.builder().apply {
            deleted(deleted)
            uid(enrollment)
            created(created.toJavaDate())
            lastUpdated(lastUpdated.toJavaDate())
            createdAtClient(createdAtClient.toJavaDate())
            lastUpdatedAtClient(lastUpdatedAtClient.toJavaDate())
            organisationUnit(orgUnit)
            program(program)
            enrollmentDate(enrollmentDate.toJavaDate())
            incidentDate(incidentDate.toJavaDate())
            completedDate(completedDate.toJavaDate())
            followUp(followup)
            status(status?.let { EnrollmentStatus.valueOf(it) })
            trackedEntityInstance(trackedEntityInstance)
            geometry(geometry?.toDomain())
            EnrollmentInternalAccessor.insertEvents(this, events?.map { it.toDomain() })
            notes(notes?.map { it.toDomain(enrollment = enrollment) })
            relationships(relationships?.map { it.toDomain() })
        }.build()
    }
}

internal fun Enrollment.toDto(): EnrollmentDTO {
    return EnrollmentDTO(
        deleted = this.deleted(),
        enrollment = this.uid(),
        created = this.created().dateFormat(),
        lastUpdated = this.lastUpdated().dateFormat(),
        createdAtClient = this.createdAtClient().dateFormat(),
        lastUpdatedAtClient = this.lastUpdatedAtClient().dateFormat(),
        orgUnit = this.organisationUnit(),
        program = this.program(),
        enrollmentDate = this.enrollmentDate().dateFormat(),
        incidentDate = this.incidentDate().dateFormat(),
        completedDate = this.completedDate().dateFormat(),
        followup = this.followUp(),
        status = this.status()?.name,
        trackedEntityInstance = this.trackedEntityInstance(),
        geometry = this.geometry()?.toDto(),
        events = EnrollmentInternalAccessor.accessEvents(this)?.map { it.toDto() },
        notes = this.notes()?.map { it.toDto() },
        relationships = EnrollmentInternalAccessor.accessRelationships(this)?.map { it.toDto() },
    )
}
