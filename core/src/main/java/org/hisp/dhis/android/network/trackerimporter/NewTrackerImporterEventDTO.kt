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
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.event.internal.EventFields
import org.hisp.dhis.android.network.common.dto.GeometryDTO
import org.hisp.dhis.android.network.common.dto.toDto

@Serializable
internal data class NewTrackerImporterEventDTO(
    @SerialName(EventFields.UID) val uid: String?,
    val enrollment: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val createdAtClient: String?,
    val updatedAtClient: String?,
    val program: String?,
    val programStage: String?,
    @SerialName(EventFields.ORGANISATION_UNIT) val organisationUnit: String?,
    val occurredAt: String?,
    val status: String?,
    val geometry: GeometryDTO?,
    val completedAt: String?,
    val completedBy: String?,
    val scheduledAt: String?,
    val attributeOptionCombo: String?,
    val assignedUser: NewTrackerImporterUserInfoDTO?,
    val notes: List<NewTrackerImporterNoteDTO>?,
    @SerialName(EventFields.TRACKED_ENTITY_DATA_VALUES) val trackedEntityDataValues: List<NewTrackerImporterTrackedEntityDataValueDTO>?,
    val aggregatedSyncState: String?, //no json property
    val trackedEntity: String?,
    val relationships: List<NewTrackerImporterRelationshipDTO>? = null,

    )

internal fun NewTrackerImporterEvent.toDto(): NewTrackerImporterEventDTO {
    return NewTrackerImporterEventDTO(
        uid = this.uid(),
        enrollment = this.enrollment(),
        createdAt = this.createdAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAt = this.updatedAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        createdAtClient = this.createdAtClient()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAtClient = this.updatedAtClient()?.let { DateUtils.DATE_FORMAT.format(it) },
        program = this.program(),
        programStage = this.programStage(),
        organisationUnit = this.organisationUnit(),
        occurredAt = this.occurredAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        status = this.status()?.name,
        geometry = this.geometry()?.let { it.toDto() },
        completedAt = this.completedAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        completedBy = this.completedBy(),
        scheduledAt = this.scheduledAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        attributeOptionCombo = this.attributeOptionCombo(),
        assignedUser = this.assignedUser()?.let { it.toDto() },
        notes = this.notes()?.map { it.toDto() },
        trackedEntityDataValues = this.trackedEntityDataValues()?.map { it.toDto() },
        aggregatedSyncState = this.aggregatedSyncState()?.name,
        trackedEntity = this.trackedEntity(),
//        relationships = this.relationships()?.map { it.toDto() }
    )
}
