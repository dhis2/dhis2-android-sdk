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

import org.hisp.dhis.android.core.arch.api.fields.internal.BaseFields
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.fields.internal.Property
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitFields
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserCredentials
import org.hisp.dhis.android.core.user.UserGroup
import org.hisp.dhis.android.core.user.UserRole

internal object UserFields : BaseFields<User>() {
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

    private val username = fh.field(USERNAME)
    private val userCredentials = fh.nestedField<UserCredentials>(USER_CREDENTIALS)
    private val userRoles = fh.nestedField<UserRole>(USER_ROLES)
    private val organisationUnits = fh.nestedField<OrganisationUnit>(ORGANISATION_UNITS)
    private val teiSearchOrganisationUnits = fh.nestedField<OrganisationUnit>(TEI_SEARCH_ORGANISATION_UNITS)

    private fun commonFields() = listOf(
        fh.field(BaseIdentifiableObject.UID),
        fh.field(BaseIdentifiableObject.CODE),
        fh.field(BaseIdentifiableObject.NAME),
        fh.field(BaseIdentifiableObject.DISPLAY_NAME),
        fh.field(BaseIdentifiableObject.CREATED),
        fh.field(BaseIdentifiableObject.LAST_UPDATED),
        fh.field(BIRTHDAY),
        fh.field(EDUCATION),
        fh.field(GENDER),
        fh.field(JOB_TITLE),
        fh.field(SURNAME),
        fh.field(FIRST_NAME),
        fh.field(INTRODUCTION),
        fh.field(EMPLOYER),
        fh.field(INTERESTS),
        fh.field(LANGUAGES),
        fh.field(EMAIL),
        fh.field(PHONE_NUMBER),
        fh.field(NATIONALITY),
        fh.field(BaseIdentifiableObject.DELETED),
        fh.nestedField<UserGroup>(USER_GROUPS).with(UserGroupFields.allFields),
    )

    private fun baseFields37() = commonFields() + userCredentials.with(UserCredentialsFields.allFields)

    private fun baseFields38() = commonFields() + username + userRoles.with(UserRoleFields.allFields)

    private fun allBaseFields() = (baseFields37() + baseFields38()).distinct()

    private fun getBaseFields(version: DHISVersion? = null): List<Property<User>> {
        return when {
            version == null -> allBaseFields()
            version >= DHISVersion.V2_38 -> baseFields38()
            else -> baseFields37()
        }
    }

    val allFieldsWithoutOrgUnit: Fields<User> = Fields.from(getBaseFields())

    fun allFieldsWithOrgUnit(version: DHISVersion?) = Fields.from(
        getBaseFields(version),
        organisationUnits.with(OrganisationUnitFields.fieldsInUserCall),
        teiSearchOrganisationUnits.with(OrganisationUnitFields.fieldsInUserCall),
    )
}
