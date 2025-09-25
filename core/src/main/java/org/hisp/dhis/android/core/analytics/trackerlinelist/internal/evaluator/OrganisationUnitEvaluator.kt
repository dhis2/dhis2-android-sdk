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
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.EnrollmentAlias
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.OrgUnitAlias
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.SubOrgUnitAlias
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.TrackedEntityInstanceAlias
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitOrganisationUnitGroupLinkStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitOrganisationUnitGroupLinkTableInfo
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceTableInfo

internal class OrganisationUnitEvaluator(
    private val item: TrackerLineListItem.OrganisationUnitItem,
) : TrackerLineListEvaluator() {

    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore = koin.get()
    private val organisationUnitStore: OrganisationUnitStore = koin.get()
    private val orgunitLevelStore: OrganisationUnitLevelStore = koin.get()
    private val orgunitGroupLinkStore: OrganisationUnitOrganisationUnitGroupLinkStore = koin.get()

    override suspend fun getCommonSelectSQL(): String {
        return "$OrgUnitAlias.${OrganisationUnitTableInfo.Columns.DISPLAY_NAME}"
    }

    override suspend fun getCommonWhereSQL(): String {
        return if (item.filters.isEmpty()) {
            "1"
        } else {
            return item.filters.map { getFilterWhereClause(it) }.joinToString(" AND ")
        }
    }

    override suspend fun getSelectSQLForTrackedEntityInstance(): String {
        return if (item.programUid.isNullOrBlank()) {
            "$OrgUnitAlias.${OrganisationUnitTableInfo.Columns.DISPLAY_NAME}"
        } else {
            "SELECT $SubOrgUnitAlias.${OrganisationUnitTableInfo.Columns.DISPLAY_NAME} " +
                "FROM ${EnrollmentTableInfo.TABLE_INFO.name()} $EnrollmentAlias " +
                "JOIN ${OrganisationUnitTableInfo.TABLE_INFO.name()} $SubOrgUnitAlias " +
                "ON $EnrollmentAlias.${EnrollmentTableInfo.Columns.ORGANISATION_UNIT} = " +
                "$SubOrgUnitAlias.${OrganisationUnitTableInfo.Columns.UID} " +
                "WHERE $EnrollmentAlias.${EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE} = " +
                "$TrackedEntityInstanceAlias.${TrackedEntityInstanceTableInfo.Columns.UID} " +
                "AND $EnrollmentAlias.${EnrollmentTableInfo.Columns.PROGRAM} = '${item.programUid}' " +
                "ORDER BY $EnrollmentAlias.${EnrollmentTableInfo.Columns.ENROLLMENT_DATE} DESC LIMIT 1"
        }
    }

    private suspend fun getFilterWhereClause(filter: OrganisationUnitFilter): String {
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
        return "$OrgUnitAlias.${OrganisationUnitTableInfo.Columns.PATH} LIKE '%$orgunit%'"
    }

    private suspend fun getChildren(orgunits: List<String>): List<String> {
        return organisationUnitStore.selectUidsWhere(
            WhereClauseBuilder()
                .appendInKeyStringValues(OrganisationUnitTableInfo.Columns.PARENT, orgunits)
                .build(),
        )
    }
}
