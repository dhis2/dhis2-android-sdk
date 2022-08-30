/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.core.analytics.aggregated.internal

import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.organisationunit.*
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore

internal class AnalyticsOrganisationUnitHelper @Inject constructor(
    private val userOrganisationUnitStore: UserOrganisationUnitLinkStore,
    private val organisationUnitStore: IdentifiableObjectStore<OrganisationUnit>,
    private val organisationUnitLevelStore: IdentifiableObjectStore<OrganisationUnitLevel>,
    private val organisationUnitOrganisationUnitGroupLinkStore: LinkStore<OrganisationUnitOrganisationUnitGroupLink>
) {

    fun getRelativeOrganisationUnitUids(relative: RelativeOrganisationUnit): List<String> {
        val userOrganisationUnits = userOrganisationUnitStore
            .queryAssignedOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)

        val relativeOrganisationUnitsUids = when (relative) {
            RelativeOrganisationUnit.USER_ORGUNIT ->
                userOrganisationUnits
            RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN ->
                queryChildrenOrganisationUnitUids(userOrganisationUnits)
            RelativeOrganisationUnit.USER_ORGUNIT_GRANDCHILDREN ->
                queryGrandChildrenOrganisationUnitUids(userOrganisationUnits)
        }

        val whereClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                OrganisationUnitTableInfo.Columns.UID,
                relativeOrganisationUnitsUids
            ).build()

        return organisationUnitStore.selectUidsWhere(
            whereClause,
            "${OrganisationUnitTableInfo.Columns.NAME} ${RepositoryScope.OrderByDirection.ASC}"
        )
    }

    fun getOrganisationUnitUidsByLevel(level: Int): List<String> {
        val whereClause = WhereClauseBuilder()
            .appendKeyNumberValue(OrganisationUnitTableInfo.Columns.LEVEL, level)
            .build()

        return organisationUnitStore.selectUidsWhere(whereClause)
    }

    fun getOrganisationUnitUidsByLevelUid(levelUid: String): List<String> {
        val level = organisationUnitLevelStore.selectByUid(levelUid)

        return getOrganisationUnitUidsByLevel(level?.level()!!)
    }

    fun getOrganisationUnitUidsByGroup(groupUid: String): List<String> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(
                OrganisationUnitOrganisationUnitGroupLinkTableInfo.Columns.ORGANISATION_UNIT_GROUP,
                groupUid
            ).build()

        return organisationUnitOrganisationUnitGroupLinkStore.selectStringColumnsWhereClause(
            OrganisationUnitOrganisationUnitGroupLinkTableInfo.Columns.ORGANISATION_UNIT,
            whereClause
        ).distinct()
    }

    private fun queryChildrenOrganisationUnitUids(parentUids: List<String>): List<String> {
        return getChildrenOrganisationUnitUids(parentUids)
    }

    private fun queryGrandChildrenOrganisationUnitUids(parentUids: List<String>): List<String> {
        return getChildrenOrganisationUnitUids(queryChildrenOrganisationUnitUids(parentUids))
    }

    private fun getChildrenOrganisationUnitUids(parentUids: List<String>): List<String> {
        val childrenClause = WhereClauseBuilder()
            .appendInKeyStringValues(OrganisationUnitTableInfo.Columns.PARENT, parentUids)
            .build()

        return organisationUnitStore.selectUidsWhere(childrenClause)
    }
}
