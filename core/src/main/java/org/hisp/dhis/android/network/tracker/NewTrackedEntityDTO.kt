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
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import org.hisp.dhis.android.network.common.dto.BaseDeletableDataObjectDTO
import org.hisp.dhis.android.network.common.dto.GeometryDTO
import org.hisp.dhis.android.network.common.dto.toDto

@Serializable
internal data class NewTrackedEntityDTO(
    override val deleted: Boolean?,
    val trackedEntity: String,
    val createdAt: String?,
    val updatedAt: String?,
    val createdAtClient: String?,
    val updatedAtClient: String?,
    val organisationUnit: String?,
    val trackedEntityType: String?,
    val geometry: GeometryDTO?,
    val aggregatedSyncState: String?,
    val attributes: List<NewTrackedEntityAttributeValueDTO>?,
    val enrollments: List<NewEnrollmentDTO>?,
    val programOwners: List<NewProgramOwnerDTO>?,
    val relationships: List<NewRelationshipDTO>? = null,
) : BaseDeletableDataObjectDTO

internal fun NewTrackerImporterTrackedEntity.toDto(): NewTrackedEntityDTO {
    return NewTrackedEntityDTO(
        deleted = this.deleted(),
        trackedEntity = this.uid(),
        createdAt = this.createdAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAt = this.updatedAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        createdAtClient = this.createdAtClient()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAtClient = this.updatedAtClient()?.let { DateUtils.DATE_FORMAT.format(it) },
        organisationUnit = this.organisationUnit(),
        trackedEntityType = this.trackedEntityType(),
        geometry = this.geometry()?.let { it.toDto() },
        aggregatedSyncState = this.aggregatedSyncState()?.name,
        attributes = this.trackedEntityAttributeValues()?.map { it.toDto() },
        enrollments = this.enrollments()?.map { it.toDto() },
        programOwners = this.programOwners()?.map { it.toDto() },
//        relationships = this.relationships()?.map { it.toDto() }
    )
}
