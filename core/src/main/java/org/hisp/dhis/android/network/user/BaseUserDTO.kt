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

import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.network.common.dto.BaseIdentifiableObjectDTO
import org.hisp.dhis.android.network.common.dto.applyBaseIdentifiableFields

/**
 * Properties shared by the DTOs of the different `me` requests, which differ in how the username, the user roles
 * and the organisation units are requested.
 */
internal interface BaseUserDTO : BaseIdentifiableObjectDTO {
    val birthday: String?
    val education: String?
    val gender: String?
    val jobTitle: String?
    val surname: String?
    val firstName: String?
    val introduction: String?
    val employer: String?
    val interests: String?
    val languages: String?
    val email: String?
    val phoneNumber: String?
    val nationality: String?
    val userGroups: List<UserGroupDTO>?
}

internal fun User.Builder.applyBaseUserFields(item: BaseUserDTO): User.Builder {
    applyBaseIdentifiableFields(item)
    birthday(item.birthday)
    education(item.education)
    gender(item.gender)
    jobTitle(item.jobTitle)
    surname(item.surname)
    firstName(item.firstName)
    introduction(item.introduction)
    employer(item.employer)
    interests(item.interests)
    languages(item.languages)
    email(item.email)
    phoneNumber(item.phoneNumber)
    nationality(item.nationality)
    userGroups(item.userGroups?.map { it.toDomain() })
    return this
}
