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
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES
 *  LOSS OF USE, DATA, OR PROFITSOR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.network.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.network.common.BaseIdentifiableObjectDTO
import org.hisp.dhis.android.network.common.OrganisationUnitDTO

@Serializable
internal data class UserDTO(
    @SerialName("id") override val uid: String,
    override val code: String? = BaseIdentifiableObjectDTO.CODE,
    override val name: String? = BaseIdentifiableObjectDTO.NAME,
    override val displayName: String? = BaseIdentifiableObjectDTO.DISPLAY_NAME,
    override val created: String? = BaseIdentifiableObjectDTO.CREATED,
    override val lastUpdated: String? = BaseIdentifiableObjectDTO.LAST_UPDATED,
    override val deleted: Boolean? = BaseIdentifiableObjectDTO.DELETED,
    val username: String? = null,
    val birthday: String? = null,
    val education: String? = null,
    val gender: String? = null,
    val jobTitle: String? = null,
    val surname: String? = null,
    val firstName: String? = null,
    val introduction: String? = null,
    val employer: String? = null,
    val interests: String? = null,
    val languages: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val nationality: String? = null,
    val userCredentials: UserCredentialsDTO? = null,
    val organisationUnits: List<OrganisationUnitDTO>? = null,
    val teiSearchOrganisationUnits: List<OrganisationUnitDTO>? = null,
    val userRoles: List<UserRoleDTO>? = null,
    val userGroups: List<UserGroupDTO>? = null,
) : BaseIdentifiableObjectDTO
