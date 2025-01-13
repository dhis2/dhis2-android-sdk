/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.network.organisationunit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.network.common.GeometryDTO
import org.hisp.dhis.android.network.common.dto.BaseNameableObjectDTO
import org.hisp.dhis.android.network.common.dto.ObjectWithUidDTO
import org.hisp.dhis.android.network.common.dto.applyBaseNameableFields

@Serializable
internal data class OrganisationUnitDTO(
    @SerialName("id") override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
    val parent: ObjectWithUidDTO?,
    val path: String?,
    val openingDate: String?,
    val closedDate: String?,
    val level: Int?,
    val coordinates: String?,
    val featureType: String?,
    val geometry: GeometryDTO?,
    val programs: List<ObjectWithUidDTO>? = emptyList(),
    val dataSets: List<ObjectWithUidDTO>? = emptyList(),
    val ancestors: List<OrganisationUnitDTO>? = emptyList(),
    val organisationUnitGroups: List<OrganisationUnitGroupDTO>? = emptyList(),
    val displayNamePath: List<String>? = emptyList(),
) : BaseNameableObjectDTO {
    fun toDomain(): OrganisationUnit {
        return OrganisationUnit.builder()
            .applyBaseNameableFields(this)
            .parent(parent?.uid?.let { ObjectWithUid.create(it) })
            .path(path)
            .apply {
                openingDate?.let { openingDate(it) }
                closedDate?.let { closedDate(it) }
            }
            .level(level)
            .geometry(geometry?.toDomain())
            .programs(programs?.map { ObjectWithUid.create(it.uid) })
            .programs(programs?.map { ObjectWithUid.create(it.uid) })
            .dataSets(dataSets?.map { ObjectWithUid.create(it.uid) })
            .organisationUnitGroups(organisationUnitGroups?.map { it.toDomain() })
            .displayNamePath(displayNamePath)
            .build()
    }
}
