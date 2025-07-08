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

package org.hisp.dhis.android.network.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventInternalAccessor
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.BaseDeletableDataObjectDTO
import org.hisp.dhis.android.network.common.dto.DateStringDTO
import org.hisp.dhis.android.network.common.dto.GeometryDTO
import org.hisp.dhis.android.network.common.dto.PagerDTO
import org.hisp.dhis.android.network.common.dto.toDto
import org.hisp.dhis.android.network.note.NoteDTO
import org.hisp.dhis.android.network.note.toDto
import org.hisp.dhis.android.network.relationship.RelationshipDTO
import org.hisp.dhis.android.network.relationship.toDto

@Serializable
internal data class EventDTO(
    override val deleted: Boolean?,
    val event: String,
    val enrollment: String?,
    val trackedEntityInstance: String?,
    val created: DateStringDTO?,
    val lastUpdated: DateStringDTO?,
    val createdAtClient: DateStringDTO?,
    val lastUpdatedAtClient: DateStringDTO?,
    val program: String?,
    val programStage: String?,
    val orgUnit: String?,
    val eventDate: DateStringDTO?,
    val status: String?,
    val geometry: GeometryDTO?,
    val completedDate: DateStringDTO?,
    val completedBy: String?,
    val dueDate: DateStringDTO?,
    val attributeOptionCombo: String?,
    val assignedUser: String?,
    val notes: List<NoteDTO>?,
    val relationships: List<RelationshipDTO>?,
    val dataValues: List<TrackedEntityDataValueDTO>?,
) : BaseDeletableDataObjectDTO {
    fun toDomain(): Event {
        return Event.builder().apply {
            deleted(deleted)
            uid(event)
            enrollment(enrollment)
            EventInternalAccessor.insertTrackedEntityInstance(this, trackedEntityInstance)
            created(created?.toDomain())
            lastUpdated(lastUpdated?.toDomain())
            createdAtClient(createdAtClient?.toDomain())
            lastUpdatedAtClient(lastUpdatedAtClient?.toDomain())
            program(program)
            programStage(programStage)
            organisationUnit(orgUnit)
            eventDate(eventDate?.toDomain())
            status(status?.let { EventStatus.valueOf(it) })
            geometry(geometry?.toDomain())
            completedDate(completedDate?.toDomain())
            completedBy(completedBy)
            dueDate(dueDate?.toDomain())
            attributeOptionCombo(attributeOptionCombo)
            assignedUser(assignedUser)
            notes(notes?.map { it.toDomain(event = event) })
            relationships(relationships?.map { it.toDomain() })
            trackedEntityDataValues(dataValues?.map { it.toDomain(event) })
        }.build()
    }
}

internal fun Event.toDto(): EventDTO {
    return EventDTO(
        deleted = this.deleted(),
        event = this.uid(),
        enrollment = this.enrollment(),
        trackedEntityInstance = EventInternalAccessor.accessTrackedEntityInstance(this),
        created = this.created()?.toDto(),
        lastUpdated = this.lastUpdated()?.toDto(),
        createdAtClient = this.createdAtClient()?.toDto(),
        lastUpdatedAtClient = this.lastUpdatedAtClient()?.toDto(),
        program = this.program(),
        programStage = this.programStage(),
        orgUnit = this.organisationUnit(),
        eventDate = this.eventDate()?.toDto(),
        status = this.status()?.name,
        geometry = this.geometry()?.toDto(),
        completedDate = this.completedDate()?.toDto(),
        completedBy = this.completedBy(),
        dueDate = this.dueDate()?.toDto(),
        attributeOptionCombo = this.attributeOptionCombo(),
        assignedUser = this.assignedUser(),
        notes = this.notes()?.map { it.toDto() },
        relationships = EventInternalAccessor.accessRelationships(this)?.map { it.toDto() },
        dataValues = this.trackedEntityDataValues()?.map { it.toDto() },
    )
}

@Serializable
internal class EventPayload(
    override val pager: PagerDTO? = null,
    @SerialName("events") override val items: List<EventDTO> = emptyList(),
) : PayloadJson<EventDTO>(pager, items)
