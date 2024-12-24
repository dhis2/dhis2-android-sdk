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
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.network.organisationunit.OrganisationUnitDTO
import org.hisp.dhis.android.network.common.dto.BaseIdentifiableObjectDTO
import org.hisp.dhis.android.network.common.dto.applyBaseIdentifiableFields

@Serializable
internal data class UserDTO(
    @SerialName("id") override val uid: String,
    override val code: String? = null,
    override val name: String? = null,
    override val displayName: String? = null,
    override val created: String? = null,
    override val lastUpdated: String? = null,
    override val deleted: Boolean? = null,
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
    val organisationUnits: List<OrganisationUnitDTO>? = emptyList(),
    val teiSearchOrganisationUnits: List<OrganisationUnitDTO>? = emptyList(),
    val userRoles: List<UserRoleDTO>? = emptyList(),
    val userGroups: List<UserGroupDTO>? = emptyList(),
) : BaseIdentifiableObjectDTO {
    fun toDomain(): User {
        return User.builder()
            .applyBaseIdentifiableFields(this@UserDTO)
            .username(username)
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
            .userCredentials(userCredentials?.toDomain())
            .organisationUnits(organisationUnits?.map { it.toDomain() })
            .teiSearchOrganisationUnits(teiSearchOrganisationUnits?.map { it.toDomain() })
            .userRoles(userRoles?.map { it.toDomain() })
            .userGroups(userGroups?.map { it.toDomain() })
            .build()
    }
}
