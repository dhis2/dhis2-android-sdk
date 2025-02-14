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
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor.insertEnrollments
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor.insertRelationships
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.BaseDeletableDataObjectDTO
import org.hisp.dhis.android.network.common.dto.GeometryDTO
import org.hisp.dhis.android.network.common.dto.PagerDTO
import org.hisp.dhis.android.network.common.dto.toDto

@Serializable
internal data class NewTrackedEntityDTO(
    override val deleted: Boolean?,
    val trackedEntity: String,
    val createdAt: String?,
    val updatedAt: String?,
    val createdAtClient: String?,
    val updatedAtClient: String?,
    val orgUnit: String?,
    val trackedEntityType: String?,
    val geometry: GeometryDTO?,
    val attributes: List<NewTrackedEntityAttributeValueDTO> = emptyList(),
    val enrollments: List<NewEnrollmentDTO> = emptyList(),
    val programOwners: List<NewProgramOwnerDTO>?,
    val relationships: List<NewRelationshipDTO>? = null,
) : BaseDeletableDataObjectDTO {
    fun toDomain(): TrackedEntityInstance {
        val teiAttributeValues = attributes.map { it.toDomain(trackedEntity) }
        val enrollmentAttributeValues =
            enrollments.flatMap { it.attributes.orEmpty().map { it.toDomain(trackedEntity) } }.orEmpty()
        val attributes = (teiAttributeValues + enrollmentAttributeValues).distinctBy { it.trackedEntityAttribute() }

        return TrackedEntityInstance.builder().apply {
            uid(trackedEntity)
            deleted(deleted)
            created(createdAt.toJavaDate())
            lastUpdated(updatedAt.toJavaDate())
            createdAtClient(createdAtClient.toJavaDate())
            lastUpdatedAtClient(updatedAtClient.toJavaDate())
            organisationUnit(orgUnit)
            trackedEntityType(trackedEntityType)
            geometry(geometry?.toDomain())
            insertEnrollments(this, enrollments.map { it.toDomain() })
            trackedEntityAttributeValues(attributes)
            programOwners(programOwners?.map { it.toDomain() })
            insertRelationships(this, relationships?.map { it.toDomain() })
        }.build()
    }
}

internal fun NewTrackerImporterTrackedEntity.toDto(): NewTrackedEntityDTO {
    return NewTrackedEntityDTO(
        deleted = this.deleted(),
        trackedEntity = this.uid(),
        createdAt = this.createdAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAt = this.updatedAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        createdAtClient = this.createdAtClient()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAtClient = this.updatedAtClient()?.let { DateUtils.DATE_FORMAT.format(it) },
        orgUnit = this.organisationUnit(),
        trackedEntityType = this.trackedEntityType(),
        geometry = this.geometry()?.let { it.toDto() },
        attributes = this.trackedEntityAttributeValues()?.map { it.toDto() } ?: emptyList(),
        enrollments = this.enrollments()?.map { it.toDto() } ?: emptyList(),
        programOwners = this.programOwners()?.map { it.toDto() },
    )
}

@Serializable
internal class NewTrackedEntityPayload(
    override val pager: PagerDTO?,
    val page: Int?,
    val pageCount: Int?,
    val pageSize: Int?,
    val total: Int?,
    @JsonNames("instances", "trackedEntities") override val items: List<NewTrackedEntityDTO> = emptyList(),
) : PayloadJson<NewTrackedEntityDTO>(pager, items) {

    override fun pager(): PagerDTO {
        return pager ?: PagerDTO(page ?: 0, pageCount ?: 0, pageSize ?: 0, total ?: 0)
    }
}
