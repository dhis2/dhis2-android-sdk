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

package org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.trackerlinelist.OrganisationUnitFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.TrackerLineListContext
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.OrgunitAlias
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLinkTableInfo
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStoreImpl
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitOrganisationUnitGroupLinkStoreImpl
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStoreImpl
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStoreImpl

internal class OrganisationUnitEvaluator(
    private val item: TrackerLineListItem.OrganisationUnitItem,
    context: TrackerLineListContext,
) : TrackerLineListEvaluator() {

    private val userOrganisationUnitLinkStore = UserOrganisationUnitLinkStoreImpl(context.databaseAdapter)
    private val organisationUnitStore = OrganisationUnitStoreImpl(context.databaseAdapter)
    private val orgunitLevelStore = OrganisationUnitLevelStoreImpl(context.databaseAdapter)
    private val orgunitGroupLinkStore = OrganisationUnitOrganisationUnitGroupLinkStoreImpl(context.databaseAdapter)

    override fun getCommonSelectSQL(): String {
        return "$OrgunitAlias.${OrganisationUnitTableInfo.Columns.DISPLAY_NAME}"
    }

    override fun getCommonWhereSQL(): String {
        return if (item.filters.isEmpty()) {
            "1"
        } else {
            return item.filters.joinToString(" AND ") { getFilterWhereClause(it) }
        }
    }

    private fun getFilterWhereClause(filter: OrganisationUnitFilter): String {
        val filterHelper = FilterHelper(item.id)
        return when (filter) {
            is OrganisationUnitFilter.Absolute -> inPathOf(filter.uid)

            is OrganisationUnitFilter.Relative -> {
                val userAssignedOrgunits = userOrganisationUnitLinkStore
                    .queryAssignedOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)

                when (filter.relative) {
                    RelativeOrganisationUnit.USER_ORGUNIT -> {
                        inPathOfAny(userAssignedOrgunits)
                    }

                    RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN -> {
                        val children = getChildren(userAssignedOrgunits)

                        inPathOfAny(children)
                    }

                    RelativeOrganisationUnit.USER_ORGUNIT_GRANDCHILDREN -> {
                        val children = getChildren(userAssignedOrgunits)
                        val grandChildren = getChildren(children)

                        inPathOfAny(grandChildren)
                    }
                }
            }

            is OrganisationUnitFilter.Level -> {
                val level = orgunitLevelStore.selectByUid(filter.uid)
                    ?: throw AnalyticsException.InvalidOrganisationUnitLevel(filter.uid)
                val orgunits = orgunitLevelStore.selectUidsWhere(
                    WhereClauseBuilder()
                        .appendKeyStringValue(OrganisationUnitTableInfo.Columns.LEVEL, level.level()!!.toString())
                        .build(),
                )

                inPathOfAny(orgunits)
            }

            is OrganisationUnitFilter.Group -> {
                val orgunits = orgunitGroupLinkStore.selectWhere(
                    WhereClauseBuilder()
                        .appendKeyStringValue(
                            OrganisationUnitOrganisationUnitGroupLinkTableInfo.Columns.ORGANISATION_UNIT,
                            filter.uid,
                        )
                        .build(),
                )

                inPathOfAny(orgunits.mapNotNull { it.organisationUnit() })
            }

            is OrganisationUnitFilter.EqualTo -> filterHelper.equalTo(filter.orgunitName, filter.ignoreCase)
            is OrganisationUnitFilter.NotEqualTo -> filterHelper.notEqualTo(filter.orgunitName, filter.ignoreCase)
            is OrganisationUnitFilter.Like -> filterHelper.like(filter.orgunitName, filter.ignoreCase)
            is OrganisationUnitFilter.NotLike -> filterHelper.notLike(filter.orgunitName, filter.ignoreCase)
        }
    }

    private fun inPathOfAny(orgunits: List<String>): String {
        val orClauses = orgunits.joinToString(" OR ") { ou -> inPathOf(ou) }
        return "($orClauses)"
    }

    private fun inPathOf(orgunit: String): String {
        return "$OrgunitAlias.${OrganisationUnitTableInfo.Columns.PATH} LIKE '%$orgunit%'"
    }

    private fun getChildren(orgunits: List<String>): List<String> {
        return organisationUnitStore.selectUidsWhere(
            WhereClauseBuilder()
                .appendInKeyStringValues(OrganisationUnitTableInfo.Columns.PARENT, orgunits)
                .build(),
        )
    }
}
