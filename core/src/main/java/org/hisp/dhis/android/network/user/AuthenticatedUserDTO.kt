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

/**
 * User returned when authenticating. The server version is not known yet, so the username and the user roles are
 * requested both in the user and in `userCredentials`, and the organisation units are not requested.
 */
@Serializable
internal data class AuthenticatedUserDTO(
    override val id: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    val username: String?,
    override val birthday: String?,
    override val education: String?,
    override val gender: String? = null,
    override val jobTitle: String? = null,
    override val surname: String? = null,
    override val firstName: String? = null,
    override val introduction: String? = null,
    override val employer: String? = null,
    override val interests: String? = null,
    override val languages: String? = null,
    override val email: String? = null,
    override val phoneNumber: String? = null,
    override val nationality: String? = null,
    // Sent by servers before 2.38. Remove when 2.37 is no longer supported.
    val userCredentials: UserCredentialsDTO? = null,
    val userRoles: List<UserRoleDTO>?,
    override val userGroups: List<UserGroupDTO>? = emptyList(),
) : BaseUserDTO {
    fun toDomain(): User {
        return User.builder()
            .applyBaseUserFields(this)
            .username(username ?: userCredentials?.username)
            .userRoles((userRoles ?: userCredentials?.userRoles)?.map { it.toDomain() })
            .build()
    }
}
