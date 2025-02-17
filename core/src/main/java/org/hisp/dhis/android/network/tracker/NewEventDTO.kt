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
import kotlinx.serialization.json.JsonNames
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventInternalAccessor
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.BaseDeletableDataObjectDTO
import org.hisp.dhis.android.network.common.dto.GeometryDTO
import org.hisp.dhis.android.network.common.dto.PagerDTO
import org.hisp.dhis.android.network.common.dto.toDto

@Serializable
internal data class NewEventDTO(
    override val deleted: Boolean?,
    val event: String?,
    val enrollment: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val createdAtClient: String?,
    val updatedAtClient: String?,
    val program: String?,
    val programStage: String?,
    val orgUnit: String?,
    val occurredAt: String?,
    val status: String?,
    val geometry: GeometryDTO?,
    val completedAt: String?,
    val completedBy: String?,
    val scheduledAt: String?,
    val attributeOptionCombo: String?,
    val assignedUser: NewUserInfoDTO?,
    val notes: List<NewNoteDTO>?,
    val dataValues: List<NewTrackedEntityDataValueDTO>?,
    val trackedEntity: String?,
    val relationships: List<NewRelationshipDTO>? = null,
) : BaseDeletableDataObjectDTO {
    fun toDomain(): Event {
        return Event.builder().apply {
            uid(event)
            deleted(deleted)
            enrollment(enrollment)
            created(createdAt.toJavaDate())
            lastUpdated(updatedAt.toJavaDate())
            createdAtClient(createdAtClient.toJavaDate())
            lastUpdatedAtClient(updatedAtClient.toJavaDate())
            program(program)
            programStage(programStage)
            organisationUnit(orgUnit)
            eventDate(occurredAt.toJavaDate())
            status(status?.let { EventStatus.valueOf(it) })
            geometry(geometry?.toDomain())
            completedDate(completedAt.toJavaDate())
            completedBy(completedBy)
            dueDate(scheduledAt.toJavaDate())
            attributeOptionCombo(attributeOptionCombo)
            assignedUser(assignedUser?.uid)
            trackedEntityDataValues(dataValues?.map { it.toDomain(event) })
            notes(notes?.map { it.toDomain(event = event) })
            relationships(relationships?.map { it.toDomain() })
            EventInternalAccessor.insertTrackedEntityInstance(this, trackedEntity)
        }.build()
    }
}

internal fun NewTrackerImporterEvent.toDto(): NewEventDTO {
    return NewEventDTO(
        event = this.uid(),
        deleted = this.deleted(),
        enrollment = this.enrollment(),
        createdAt = this.createdAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAt = this.updatedAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        createdAtClient = this.createdAtClient()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAtClient = this.updatedAtClient()?.let { DateUtils.DATE_FORMAT.format(it) },
        program = this.program(),
        programStage = this.programStage(),
        orgUnit = this.organisationUnit(),
        occurredAt = this.occurredAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        status = this.status()?.name,
        geometry = this.geometry()?.let { it.toDto() },
        completedAt = this.completedAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        completedBy = this.completedBy(),
        scheduledAt = this.scheduledAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        attributeOptionCombo = this.attributeOptionCombo(),
        assignedUser = this.assignedUser()?.let { it.toDto() },
        notes = this.notes()?.map { it.toDto() },
        dataValues = this.trackedEntityDataValues()?.map { it.toDto() },
        trackedEntity = this.trackedEntity(),
    )
}

@Serializable
internal class NewEventPayload(
    override val pager: PagerDTO?,
    val page: Int?,
    val pageCount: Int?,
    val pageSize: Int?,
    val total: Int?,
    @JsonNames("instances", "events") override val items: List<NewEventDTO> = emptyList(),
) : PayloadJson<NewEventDTO>(pager, items) {

    override fun pager(): PagerDTO {
        return pager ?: PagerDTO(page, pageCount, pageSize, total)
    }
}
