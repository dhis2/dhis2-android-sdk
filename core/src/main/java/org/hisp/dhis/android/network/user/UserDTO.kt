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

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.network.common.dto.BaseIdentifiableObjectDTO
import org.hisp.dhis.android.network.common.dto.applyBaseIdentifiableFields
import org.hisp.dhis.android.network.organisationunit.OrganisationUnitDTO

@Serializable
internal data class UserDTO(
    override val id: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    val username: String?,
    val birthday: String?,
    val education: String?,
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
    val organisationUnits: List<OrganisationUnitDTO>? = emptyList(),
    val teiSearchOrganisationUnits: List<OrganisationUnitDTO>? = emptyList(),
    val userRoles: List<UserRoleDTO>?,
    val userGroups: List<UserGroupDTO>? = emptyList(),
) : BaseIdentifiableObjectDTO {
    fun toDomain(): User {
        return User.builder()
            .applyBaseIdentifiableFields(this)
            .username(username ?: userCredentials?.username)
            .birthday(birthday)
            .education(education)
            .gender(gender)
            .jobTitle(jobTitle)
            .surname(surname)
            .firstName(firstName)
            .introduction(introduction)
            .employer(employer)
            .interests(interests)
            .languages(languages)
            .email(email)
            .phoneNumber(phoneNumber)
            .nationality(nationality)
            .organisationUnits(organisationUnits?.map { it.toDomain() })
            .teiSearchOrganisationUnits(teiSearchOrganisationUnits?.map { it.toDomain() })
            .userRoles((userRoles ?: userCredentials?.userRoles)?.map { it.toDomain() })
            .userGroups(userGroups?.map { it.toDomain() })
            .build()
    }
}
