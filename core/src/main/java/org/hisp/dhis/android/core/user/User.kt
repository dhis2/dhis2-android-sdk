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

package org.hisp.dhis.android.core.user

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import java.util.Date

@ModelBuilder
@Suppress("TooManyFunctions")
data class User(
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: Date?,
    override val lastUpdated: Date?,
    override val deleted: Boolean?,
    val username: String?,
    val birthday: String?,
    val education: String?,
    val gender: String?,
    val jobTitle: String?,
    val surname: String?,
    val firstName: String?,
    val introduction: String?,
    val employer: String?,
    val interests: String?,
    val languages: String?,
    val email: String?,
    val phoneNumber: String?,
    val nationality: String?,
    val twoFactorAuthEnabled: Boolean?,
    internal val organisationUnits: List<OrganisationUnit>?,
    internal val teiSearchOrganisationUnits: List<OrganisationUnit>?,
    val userRoles: List<UserRole>?,
    val userGroups: List<UserGroup>?,
) : BaseIdentifiableObject, CoreObject {

    fun username(): String? = username
    fun birthday(): String? = birthday
    fun education(): String? = education
    fun gender(): String? = gender
    fun jobTitle(): String? = jobTitle
    fun surname(): String? = surname
    fun firstName(): String? = firstName
    fun introduction(): String? = introduction
    fun employer(): String? = employer
    fun interests(): String? = interests
    fun languages(): String? = languages
    fun email(): String? = email
    fun phoneNumber(): String? = phoneNumber
    fun nationality(): String? = nationality
    fun twoFactorAuthEnabled(): Boolean? = twoFactorAuthEnabled
    internal fun organisationUnits(): List<OrganisationUnit>? = organisationUnits
    internal fun teiSearchOrganisationUnits(): List<OrganisationUnit>? = teiSearchOrganisationUnits
    fun userRoles(): List<UserRole>? = userRoles
    fun userGroups(): List<UserGroup>? = userGroups

    fun toBuilder(): Builder = UserBuilder.from(this)

    class Builder : UserBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
