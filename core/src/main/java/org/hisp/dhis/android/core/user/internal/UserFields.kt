/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.user.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.Field
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.fields.internal.NestedField
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitFields
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserCredentials
import org.hisp.dhis.android.core.user.UserGroup
import org.hisp.dhis.android.core.user.UserRole

internal object UserFields {
    const val USERNAME = "username"
    const val BIRTHDAY = "birthday"
    const val EDUCATION = "education"
    const val GENDER = "gender"
    const val JOB_TITLE = "jobTitle"
    const val SURNAME = "surname"
    const val FIRST_NAME = "firstName"
    const val INTRODUCTION = "introduction"
    const val EMPLOYER = "employer"
    const val INTERESTS = "interests"
    const val LANGUAGES = "languages"
    const val EMAIL = "email"
    const val PHONE_NUMBER = "phoneNumber"
    const val NATIONALITY = "nationality"
    private const val USER_CREDENTIALS = "userCredentials"
    const val USER_ROLES = "userRoles"
    private const val USER_GROUPS = "userGroups"
    private const val ORGANISATION_UNITS = "organisationUnits"
    private const val TEI_SEARCH_ORGANISATION_UNITS = "teiSearchOrganisationUnits"

    private val uid = Field.create<User>(BaseIdentifiableObject.UID)
    private val code = Field.create<User>(BaseIdentifiableObject.CODE)
    private val name = Field.create<User>(BaseIdentifiableObject.NAME)
    private val displayName = Field.create<User>(BaseIdentifiableObject.DISPLAY_NAME)
    private val created = Field.create<User>(BaseIdentifiableObject.CREATED)
    private val lastUpdated = Field.create<User>(BaseIdentifiableObject.LAST_UPDATED)
    private val username = Field.create<User>(USERNAME)
    private val birthday = Field.create<User>(BIRTHDAY)
    private val education = Field.create<User>(EDUCATION)
    private val gender = Field.create<User>(GENDER)
    private val jobTitle = Field.create<User>(JOB_TITLE)
    private val surname = Field.create<User>(SURNAME)
    private val firstName = Field.create<User>(FIRST_NAME)
    private val introduction = Field.create<User>(INTRODUCTION)
    private val employer = Field.create<User>(EMPLOYER)
    private val interests = Field.create<User>(INTERESTS)
    private val languages = Field.create<User>(LANGUAGES)
    private val email = Field.create<User>(EMAIL)
    private val phoneNumber = Field.create<User>(PHONE_NUMBER)
    private val nationality = Field.create<User>(NATIONALITY)
    private val deleted = Field.create<User>(BaseIdentifiableObject.DELETED)
    private val userCredentials = NestedField.create<User, UserCredentials>(USER_CREDENTIALS)
    private val organisationUnits = NestedField.create<User, OrganisationUnit>(ORGANISATION_UNITS)
    private val teiSearchOrganisationUnits = NestedField.create<User, OrganisationUnit>(TEI_SEARCH_ORGANISATION_UNITS)
    private val userRoles = NestedField.create<User, UserRole>(USER_ROLES)
    private val userGroups = NestedField.create<User, UserGroup>(USER_GROUPS)

    private fun commonFields(): Fields.Builder<User> {
        return Fields.builder<User>().fields(
            uid, code, name, displayName, created, lastUpdated, birthday, education, gender, jobTitle, surname,
            firstName, introduction, employer, interests, languages, email, phoneNumber, nationality, deleted,
            userGroups.with(UserGroupFields.allFields),
        )
    }

    private fun baseFields37(): Fields.Builder<User> {
        return commonFields()
            .fields(userCredentials.with(UserCredentialsFields.allFields))
    }

    private fun baseFields38(): Fields.Builder<User> {
        return commonFields()
            .fields(username, userRoles.with(UserRoleFields.allFields))
    }

    private fun allBaseFields(): Fields.Builder<User> {
        return commonFields()
            .fields(userCredentials.with(UserCredentialsFields.allFields))
            .fields(username, userRoles.with(UserRoleFields.allFields))
    }

    private fun getBaseFields(version: DHISVersion?): Fields.Builder<User> {
        return when {
            version == null -> allBaseFields()
            version >= DHISVersion.V2_38 -> baseFields38()
            else -> baseFields37()
        }
    }

    val allFieldsWithoutOrgUnit: Fields<User> = getBaseFields(null).build()

    fun allFieldsWithOrgUnit(version: DHISVersion?): Fields<User> {
        return getBaseFields(version)
            .fields(
                organisationUnits.with(OrganisationUnitFields.fieldsInUserCall),
                teiSearchOrganisationUnits.with(OrganisationUnitFields.fieldsInUserCall),
            )
            .build()
    }
}
