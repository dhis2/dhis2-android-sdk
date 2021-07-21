/*
 *  Copyright (c) 2004-2021, University of Oslo
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
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore

internal class AnalyticsOrganisationUnitHelper @Inject constructor(
    private val userOrganisationUnitStore: UserOrganisationUnitLinkStore,
    private val organisationUnitStore: IdentifiableObjectStore<OrganisationUnit>
) {

    fun getRelativeOrganisationUnits(relative: RelativeOrganisationUnit): List<OrganisationUnit> {
        val orgunitUids = getRelativeOrganisationUnitUids(relative)

        return organisationUnitStore.selectByUids(orgunitUids)
    }

    fun getRelativeOrganisationUnitUids(relative: RelativeOrganisationUnit): List<String> {
        val userOrganisationUnits = userOrganisationUnitStore.queryRootCaptureOrganisationUnitUids()

        return when (relative) {
            RelativeOrganisationUnit.USER_ORGUNIT ->
                userOrganisationUnits
            RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN ->
                queryChildrenOrganisationUnitUids(userOrganisationUnits)
            RelativeOrganisationUnit.USER_ORGUNIT_GRANDCHILDREN ->
                queryGrandChildrenOrganisationUnitUids(userOrganisationUnits)
        }
    }

    fun getOrganisationUnitUidsByLevel(level: Int): List<String> {
        val whereClause = WhereClauseBuilder()
            .appendKeyNumberValue(OrganisationUnitTableInfo.Columns.LEVEL, level)
            .build()

        return organisationUnitStore.selectUidsWhere(whereClause)
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
