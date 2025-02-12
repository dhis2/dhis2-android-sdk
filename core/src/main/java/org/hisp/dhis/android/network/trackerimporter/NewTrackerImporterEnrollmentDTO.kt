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

package org.hisp.dhis.android.network.trackerimporter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.network.common.dto.GeometryDTO
import org.hisp.dhis.android.network.common.dto.toDto
import org.hisp.dhis.android.network.enrollment.EnrollmentFields.ORGANISATION_UNIT

@Serializable
internal data class NewTrackerImporterEnrollmentDTO(
    @SerialName("enrollment") val uid: String,
    val createdAt: String?,
    val updatedAt: String?,
    val createdAtClient: String?,
    val updatedAtClient: String?,
    @SerialName(ORGANISATION_UNIT) val organisationUnit: String?,
    val program: String?,
    val enrolledAt: String?,
    val occurredAt: String?,
    val completedAt: String?,
    val followUp: Boolean?,
    val status: String?,
    val trackedEntity: String?,
    val geometry: GeometryDTO?,
    val aggregatedSyncState: String?,
    val attributes: List<NewTrackerImporterTrackedEntityAttributeValueDTO>?,
    val events: List<NewTrackerImporterEventDTO>?,
    val notes: List<NewTrackerImporterNoteDTO>?,
    val relationships: List<NewTrackerImporterRelationshipDTO>? = null,
)

internal fun NewTrackerImporterEnrollment.toDto(): NewTrackerImporterEnrollmentDTO {
    return NewTrackerImporterEnrollmentDTO(
        uid = this.uid(),
        createdAt = this.createdAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAt = this.updatedAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        createdAtClient = this.createdAtClient()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAtClient = this.updatedAtClient()?.let { DateUtils.DATE_FORMAT.format(it) },
        organisationUnit = this.organisationUnit(),
        program = this.program(),
        enrolledAt = this.enrolledAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        occurredAt = this.occurredAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        completedAt = this.completedAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        followUp = this.followUp(),
        status = this.status()?.name,
        trackedEntity = this.trackedEntity(),
        geometry = this.geometry()?.let { it.toDto() },
        aggregatedSyncState = this.aggregatedSyncState()?.name,
        attributes = this.attributes()?.map { it.toDto() },
        events = this.events()?.map { it.toDto() },
        notes = this.notes()?.map { it.toDto() },
//        relationships = this.relationships()?.map { it.toDto() }
    )
}
