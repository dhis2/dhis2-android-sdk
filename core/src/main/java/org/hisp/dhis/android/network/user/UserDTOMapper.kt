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

package org.hisp.dhis.android.network.user

import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserCredentials
import org.hisp.dhis.android.core.user.UserGroup
import org.hisp.dhis.android.core.user.UserRole
import org.hisp.dhis.android.network.common.GeometryDTO
import org.hisp.dhis.android.network.common.OrganisationUnitDTO
import org.hisp.dhis.android.network.common.OrganisationUnitGroupDTO
import org.hisp.dhis.android.network.common.applyBaseIdentifiableFields
import org.hisp.dhis.android.network.common.applyBaseNameableFields

internal fun userDTOtoDomainMapper(item: UserDTO): User {
    return User.builder()
        .applyBaseIdentifiableFields(item)
        .username(item.username)
        .birthday(item.birthday)
        .education(item.education)
        .gender(item.gender)
        .jobTitle(item.jobTitle)
        .surname(item.surname)
        .firstName(item.firstName)
        .introduction(item.introduction)
        .employer(item.employer)
        .interests(item.interests)
        .languages(item.languages)
        .email(item.email)
        .phoneNumber(item.phoneNumber)
        .nationality(item.nationality)
        .userCredentials(item.userCredentials?.let { userCredentialsDTOtoDomainMapper(it) })
        .organisationUnits(item.organisationUnits?.map { it -> organisationUnitsDTOtoDomainMapper(it) })
        .teiSearchOrganisationUnits(
            item.teiSearchOrganisationUnits?.map { it ->
                organisationUnitsDTOtoDomainMapper(it)
            },
        )
        .userRoles(item.userRoles?.map { it -> userRolesDTOtoDomainMapper(it) })
        .userGroups(item.userGroups?.map { it -> userGroupsDTOtoDomainMapper(it) })
        .build()
}

internal fun userCredentialsDTOtoDomainMapper(item: UserCredentialsDTO): UserCredentials {
    return UserCredentials.builder()
        .username(item.username)
        .name(item.name)
        .displayName(item.displayName)
        .userRoles(item.userRoles?.map { it -> userRolesDTOtoDomainMapper(it) })
        .build()
}

internal fun organisationUnitsDTOtoDomainMapper(item: OrganisationUnitDTO): OrganisationUnit {
    return OrganisationUnit.builder()
        .applyBaseNameableFields(item)
        .parent(item.parent?.uid?.let { ObjectWithUid.create(it) })
        .path(item.path)
        .apply {
            item.openingDate?.let { openingDate(it) }
            item.closedDate?.let { closedDate(it) }
        }
        .level(item.level)
        .geometry(item.geometry?.let { geometryDTOtoDomainMapper(it) })
        .programs(item.programs?.map { ObjectWithUid.create(it.uid) })
        .dataSets(item.dataSets?.map { ObjectWithUid.create(it.uid) })
        .organisationUnitGroups(item.organisationUnitGroups?.map { organisationUnitGroupDTOtoDomainMapper(it) })
        .displayNamePath(item.displayNamePath)
        .build()
}

internal fun organisationUnitGroupDTOtoDomainMapper(item: OrganisationUnitGroupDTO): OrganisationUnitGroup {
    return OrganisationUnitGroup.builder()
        .applyBaseIdentifiableFields(item)
        .shortName(item.shortname)
        .displayShortName(item.displayShortname)
        .build()
}

internal fun geometryDTOtoDomainMapper(item: GeometryDTO): Geometry {
    return Geometry.builder()
        .type(item.type)
        .coordinates(item.coordinates)
        .build()
}

internal fun userRolesDTOtoDomainMapper(item: UserRoleDTO): UserRole {
    return UserRole.builder()
        .applyBaseIdentifiableFields(item)
        .build()
}

internal fun userGroupsDTOtoDomainMapper(item: UserGroupDTO): UserGroup {
    return UserGroup.builder()
        .applyBaseIdentifiableFields(item)
        .build()
}
