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

package org.hisp.dhis.android.network.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OrganisationUnitDTO(
    @SerialName("id") override val uid: String,
    override val code: String? = null,
    override val name: String? = null,
    override val displayName: String? = null,
    override val created: String? = null,
    override val lastUpdated: String? = null,
    override val deleted: Boolean? = null,
    override val shortName: String? = null,
    override val displayShortName: String? = null,
    override val description: String? = null,
    override val displayDescription: String? = null,
    val parent: ObjectWithUidDTO? = null,
    val path: String? = null,
    val openingDate: String? = null,
    val closedDate: String? = null,
    val level: Int? = null,
    val coordinates: String? = null,
    val featureType: String? = null,
    val geometry: GeometryDTO? = null,
    val programs: List<ObjectWithUidDTO>? = emptyList(),
    val dataSets: List<ObjectWithUidDTO>? = emptyList(),
    val ancestors: List<OrganisationUnitDTO>? = emptyList(),
    val organisationUnitGroups: List<OrganisationUnitGroupDTO>? = emptyList(),
    val displayNamePath: List<String>? = emptyList(),
) : BaseNameableObjectDTO
