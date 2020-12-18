/*
* Copyright (c) 2004-2019, University of Oslo
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright notice, this
* list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
* Neither the name of the HISP project nor the names of its contributors may
* be used to endorse or promote products derived from this software without
* specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
* ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.hisp.dhis.android.core.trackedentity.search

import java.util.*
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope.OrderByDirection
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo
import org.hisp.dhis.android.core.user.AuthenticatedUserTableInfo

@Suppress("TooManyFunctions")
internal object TrackedEntityInstanceLocalQueryHelper {
    private const val TEI_ALIAS = "tei"
    private const val ENROLLMENT_ALIAS = "en"
    private const val EVENT_ALIAS = "ev"
    private const val ORGUNIT_ALIAS = "ou"
    private const val TEAV_ALIAS = "teav"

    private val TEI_UID = dot(TEI_ALIAS, "uid")
    private val TEI_ALL = dot(TEI_ALIAS, "*")
    private val TEI_LAST_UPDATED = dot(TEI_ALIAS, "lastUpdated")
    private const val ENROLLMENT_DATE = EnrollmentTableInfo.Columns.ENROLLMENT_DATE
    private const val PROGRAM = EnrollmentTableInfo.Columns.PROGRAM
    private const val TRACKED_ENTITY_ATTRIBUTE = TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE
    private const val TRACKED_ENTITY_INSTANCE = TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE

    @JvmStatic
    fun getSqlQuery(scope: TrackedEntityInstanceQueryRepositoryScope, excludeList: Set<String>, limit: Int): String {
        return getSqlQuery(scope, excludeList, limit, TEI_ALL)
    }

    @JvmStatic
    fun getUidsWhereClause(
        scope: TrackedEntityInstanceQueryRepositoryScope,
        excludeList: Set<String>,
        limit: Int
    ): String {
        val selectSubQuery = getSqlQuery(scope, excludeList, limit, TEI_UID)
        return WhereClauseBuilder()
            .appendInSubQuery(IdentifiableColumns.UID, selectSubQuery)
            .build()
    }

    private fun getSqlQuery(
        scope: TrackedEntityInstanceQueryRepositoryScope,
        excludeList: Set<String>,
        limit: Int,
        columns: String
    ): String {
        var queryStr = "SELECT DISTINCT $columns FROM ${TrackedEntityInstanceTableInfo.TABLE_INFO.name()} $TEI_ALIAS"

        val where = WhereClauseBuilder()
        if (hasProgram(scope)) {
            queryStr += " JOIN ${EnrollmentTableInfo.TABLE_INFO.name()} $ENROLLMENT_ALIAS"
            queryStr += " ON ${dot(TEI_ALIAS, IdentifiableColumns.UID)} = " +
                dot(ENROLLMENT_ALIAS, EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE)

            appendProgramWhere(where, scope)
            if (hasEvent(scope)) {
                queryStr += String.format(
                    " JOIN %s %s ON %s = %s",
                    EventTableInfo.TABLE_INFO.name(), EVENT_ALIAS,
                    dot(ENROLLMENT_ALIAS, IdentifiableColumns.UID),
                    dot(EVENT_ALIAS, EventTableInfo.Columns.ENROLLMENT)
                )
                appendEventWhere(where, scope)
            }
        }

        if (hasOrgunits(scope)) {
            queryStr += String.format(
                " JOIN %s %s ON %s = %s",
                OrganisationUnitTableInfo.TABLE_INFO.name(), ORGUNIT_ALIAS,
                dot(TEI_ALIAS, TrackedEntityInstanceTableInfo.Columns.ORGANISATION_UNIT),
                dot(ORGUNIT_ALIAS, IdentifiableColumns.UID)
            )
            appendOrgunitWhere(where, scope)
        }

        if (scope.trackedEntityType() != null) {
            where.appendKeyStringValue(
                dot(TEI_ALIAS, TrackedEntityInstanceTableInfo.Columns.TRACKED_ENTITY_TYPE),
                escapeQuotes(scope.trackedEntityType())
            )
        }

        if (scope.states() == null) {
            where.appendNotKeyStringValue(dot(TEI_ALIAS, DataColumns.STATE), State.RELATIONSHIP.name)
        } else {
            where.appendInKeyEnumValues(dot(TEI_ALIAS, DataColumns.STATE), scope.states())
        }

        if (!scope.includeDeleted()) {
            where.appendKeyOperatorValue(dot(TEI_ALIAS, TrackedEntityInstanceTableInfo.Columns.DELETED), "!=", "1")
        }

        appendQueryWhere(where, scope)
        appendFiltersWhere(where, scope)
        appendExcludeList(where, excludeList)

        if (!where.isEmpty) {
            queryStr += " WHERE " + where.build()
        }
        queryStr += orderByClause(scope)
        if (limit > 0) {
            queryStr += " LIMIT $limit"
        }

        return queryStr
    }

    private fun hasProgram(scope: TrackedEntityInstanceQueryRepositoryScope): Boolean {
        return scope.program() != null
    }

    private fun hasEvent(scope: TrackedEntityInstanceQueryRepositoryScope): Boolean {
        return scope.eventFilters().isNotEmpty()
    }

    private fun appendProgramWhere(where: WhereClauseBuilder, scope: TrackedEntityInstanceQueryRepositoryScope) {
        if (scope.program() != null) {
            where.appendKeyStringValue(dot(ENROLLMENT_ALIAS, PROGRAM), escapeQuotes(scope.program()))
        }
        if (scope.programStartDate() != null) {
            where.appendKeyGreaterOrEqStringValue(
                dot(ENROLLMENT_ALIAS, ENROLLMENT_DATE),
                scope.formattedProgramStartDate()
            )
        }
        if (scope.programEndDate() != null) {
            where.appendKeyLessThanOrEqStringValue(
                dot(ENROLLMENT_ALIAS, ENROLLMENT_DATE),
                scope.formattedProgramEndDate()
            )
        }
        if (scope.enrollmentStatus() != null) {
            where.appendInKeyEnumValues(
                dot(ENROLLMENT_ALIAS, EnrollmentTableInfo.Columns.STATUS),
                scope.enrollmentStatus()
            )
        }
        if (!scope.includeDeleted()) {
            where.appendKeyOperatorValue(dot(ENROLLMENT_ALIAS, EnrollmentTableInfo.Columns.DELETED), "!=", "1")
        }
        if (scope.followUp() != null) {
            val value = if (scope.followUp() == true) 1 else 0
            where.appendKeyNumberValue(dot(ENROLLMENT_ALIAS, EnrollmentTableInfo.Columns.FOLLOW_UP), value)
        }
    }

    private fun hasOrgunits(scope: TrackedEntityInstanceQueryRepositoryScope): Boolean {
        return (
            (
                scope.orgUnits().isNotEmpty() &&
                    OrganisationUnitMode.ALL != scope.orgUnitMode() &&
                    OrganisationUnitMode.ACCESSIBLE != scope.orgUnitMode()
                ) ||
                hasOrgunitSortOrder(scope)
            )
    }

    private fun hasOrgunitSortOrder(scope: TrackedEntityInstanceQueryRepositoryScope): Boolean {
        return scope.order().any { it.column() == TrackedEntityInstanceQueryScopeOrderColumn.ORGUNIT_NAME }
    }

    private fun appendOrgunitWhere(where: WhereClauseBuilder, scope: TrackedEntityInstanceQueryRepositoryScope) {
        val ouMode = scope.orgUnitMode() ?: OrganisationUnitMode.SELECTED
        val inner = WhereClauseBuilder()
        when (ouMode) {
            OrganisationUnitMode.DESCENDANTS -> scope.orgUnits().forEach { orgUnit ->
                inner.appendOrKeyLikeStringValue(
                    dot(ORGUNIT_ALIAS, OrganisationUnitTableInfo.Columns.PATH),
                    "%" + escapeQuotes(orgUnit) + "%"
                )
            }
            OrganisationUnitMode.CHILDREN -> scope.orgUnits().forEach { orgUnit ->
                inner.appendOrKeyStringValue(
                    dot(ORGUNIT_ALIAS, OrganisationUnitTableInfo.Columns.PARENT), escapeQuotes(orgUnit)
                )

                // TODO Include orgunit?
                inner.appendOrKeyStringValue(dot(ORGUNIT_ALIAS, IdentifiableColumns.UID), escapeQuotes(orgUnit))
            }
            else -> scope.orgUnits().forEach { orgUnit ->
                inner.appendOrKeyStringValue(dot(ORGUNIT_ALIAS, IdentifiableColumns.UID), escapeQuotes(orgUnit))
            }
        }
        if (!inner.isEmpty) {
            where.appendComplexQuery(inner.build())
        }
    }

    private fun appendQueryWhere(where: WhereClauseBuilder, scope: TrackedEntityInstanceQueryRepositoryScope) {
        scope.query()?.let { query ->
            val tokens = query.value().split(" ".toRegex()).toTypedArray()
            for (token in tokens) {
                val valueStr =
                    if (query.operator() == FilterItemOperator.LIKE) "%${escapeQuotes(token)}%"
                    else escapeQuotes(token)

                val sub = String.format(
                    "SELECT 1 FROM %s %s WHERE %s = %s AND %s %s '%s'",
                    TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(), TEAV_ALIAS,
                    dot(TEAV_ALIAS, TRACKED_ENTITY_INSTANCE), dot(TEI_ALIAS, IdentifiableColumns.UID),
                    dot(TEAV_ALIAS, TrackedEntityAttributeValueTableInfo.Columns.VALUE),
                    query.operator().sqlOperator, valueStr
                )

                where.appendExistsSubQuery(sub)
            }
        }
    }

    private fun appendFiltersWhere(where: WhereClauseBuilder, scope: TrackedEntityInstanceQueryRepositoryScope) {
        // TODO Filter by program attributes in case program is provided
        appendFilterWhere(where, scope.filter())
        appendFilterWhere(where, scope.attribute())
    }

    private fun appendFilterWhere(where: WhereClauseBuilder, items: List<RepositoryScopeFilterItem>) {
        for (item in items) {
            val valueStr =
                if (item.operator() == FilterItemOperator.LIKE) "%${escapeQuotes(item.value())}%"
                else escapeQuotes(item.value())

            val sub = String.format(
                "SELECT 1 FROM %s %s WHERE %s = %s AND %s = '%s' AND %s %s '%s'",
                TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(), TEAV_ALIAS,
                dot(TEAV_ALIAS, TRACKED_ENTITY_INSTANCE), dot(TEI_ALIAS, IdentifiableColumns.UID),
                dot(TEAV_ALIAS, TRACKED_ENTITY_ATTRIBUTE), escapeQuotes(item.key()),
                dot(TEAV_ALIAS, TrackedEntityAttributeValueTableInfo.Columns.VALUE),
                item.operator().sqlOperator, valueStr
            )

            where.appendExistsSubQuery(sub)
        }
    }

    private fun appendExcludeList(where: WhereClauseBuilder, excludeList: Set<String>?) {
        if (!excludeList.isNullOrEmpty()) {
            where.appendNotInKeyStringValues(TEI_UID, excludeList.toList())
        }
    }

    private fun appendEventWhere(where: WhereClauseBuilder, scope: TrackedEntityInstanceQueryRepositoryScope) {
        val innerClause = WhereClauseBuilder()
        scope.eventFilters().forEach { eventFilter ->
            getEventFilterClause(eventFilter)?.let { eventFilterClause ->
                innerClause.appendOrComplexQuery(eventFilterClause)
            }
        }
        if (!innerClause.isEmpty) {
            where.appendComplexQuery(innerClause.build())
            where.appendKeyOperatorValue(dot(EVENT_ALIAS, EventTableInfo.Columns.DELETED), "!=", "1")
        }
    }

    private fun getEventFilterClause(eventFilter: TrackedEntityInstanceQueryEventFilter): String? {
        val innerClause = WhereClauseBuilder()

        eventFilter.assignedUserMode()?.let { mode -> appendAssignedUserMode(innerClause, mode) }

        val statusList = eventFilter.eventStatus()

        if (statusList == null) {
            appendEventDates(innerClause, eventFilter, EventTableInfo.Columns.EVENT_DATE)
        } else if (statusList.size > 0 && eventFilter.eventStartDate() != null && eventFilter.eventEndDate() != null) {
            val nowStr = DateUtils.SIMPLE_DATE_FORMAT.format(Date())
            val statusListWhere = WhereClauseBuilder()
            for (eventStatus in statusList) {
                val statusWhere = WhereClauseBuilder()
                when (eventStatus) {
                    EventStatus.ACTIVE, EventStatus.COMPLETED, EventStatus.VISITED -> {
                        statusWhere.appendKeyStringValue(dot(EVENT_ALIAS, EventTableInfo.Columns.STATUS), eventStatus)
                        appendEventDates(statusWhere, eventFilter, EventTableInfo.Columns.EVENT_DATE)
                    }
                    EventStatus.SCHEDULE -> {
                        appendEventDates(statusWhere, eventFilter, EventTableInfo.Columns.DUE_DATE)
                        statusWhere.appendIsNullValue(EventTableInfo.Columns.EVENT_DATE)
                        statusWhere.appendIsNotNullValue(dot(EVENT_ALIAS, EventTableInfo.Columns.STATUS))
                        statusWhere.appendKeyGreaterOrEqStringValue(
                            dot(EVENT_ALIAS, EventTableInfo.Columns.DUE_DATE), nowStr
                        )
                    }
                    EventStatus.OVERDUE -> {
                        appendEventDates(statusWhere, eventFilter, EventTableInfo.Columns.DUE_DATE)
                        statusWhere.appendIsNullValue(EventTableInfo.Columns.EVENT_DATE)
                        statusWhere.appendIsNotNullValue(dot(EVENT_ALIAS, EventTableInfo.Columns.STATUS))
                        statusWhere.appendKeyLessThanStringValue(
                            dot(EVENT_ALIAS, EventTableInfo.Columns.DUE_DATE), nowStr
                        )
                    }
                    EventStatus.SKIPPED -> {
                        statusWhere.appendKeyStringValue(dot(EVENT_ALIAS, EventTableInfo.Columns.STATUS), eventStatus)
                        appendEventDates(statusWhere, eventFilter, EventTableInfo.Columns.DUE_DATE)
                    }
                    else -> {
                    }
                }
                statusListWhere.appendOrComplexQuery(statusWhere.build())
            }
            innerClause.appendComplexQuery(statusListWhere.build())
        }
        return if (innerClause.isEmpty) null else innerClause.build()
    }

    private fun appendEventDates(
        where: WhereClauseBuilder,
        eventFilter: TrackedEntityInstanceQueryEventFilter,
        targetDate: String
    ) {
        if (eventFilter.eventStartDate() != null) {
            where.appendKeyGreaterOrEqStringValue(dot(EVENT_ALIAS, targetDate), eventFilter.formattedEventStartDate())
        }
        if (eventFilter.eventEndDate() != null) {
            where.appendKeyLessThanOrEqStringValue(dot(EVENT_ALIAS, targetDate), eventFilter.formattedEventEndDate())
        }
    }

    private fun appendAssignedUserMode(
        where: WhereClauseBuilder,
        mode: AssignedUserMode
    ) {
        val assignedUserColumn = dot(EVENT_ALIAS, EventTableInfo.Columns.ASSIGNED_USER)
        when (mode) {
            AssignedUserMode.CURRENT -> {
                val subquery = String.format(
                    "(SELECT %s FROM %s LIMIT 1)",
                    AuthenticatedUserTableInfo.Columns.USER,
                    AuthenticatedUserTableInfo.TABLE_INFO.name()
                )
                where.appendKeyOperatorValue(assignedUserColumn, "IN", subquery)
            }
            AssignedUserMode.ANY -> where.appendIsNotNullValue(assignedUserColumn)
            AssignedUserMode.NONE -> where.appendIsNullValue(assignedUserColumn)
            else -> {
            }
        }
    }

    private fun orderByClause(scope: TrackedEntityInstanceQueryRepositoryScope): String {
        val orderClauses = scope.order().mapNotNull { item ->
            when (item.column().type()) {
                TrackedEntityInstanceQueryScopeOrderColumn.Type.CREATED ->
                    if (hasProgram(scope)) {
                        orderByEnrollmentField(scope.program(), IdentifiableColumns.CREATED, item.direction())
                    } else {
                        dot(TEI_ALIAS, IdentifiableColumns.CREATED) + " " + item.direction().name
                    }

                TrackedEntityInstanceQueryScopeOrderColumn.Type.LAST_UPDATED ->
                    if (hasProgram(scope)) {
                        orderByEnrollmentField(scope.program(), IdentifiableColumns.LAST_UPDATED, item.direction())
                    } else {
                        dot(TEI_ALIAS, IdentifiableColumns.LAST_UPDATED) + " " + item.direction().name
                    }

                TrackedEntityInstanceQueryScopeOrderColumn.Type.ORGUNIT_NAME ->
                    dot(ORGUNIT_ALIAS, IdentifiableColumns.NAME) + " " + item.direction().name

                TrackedEntityInstanceQueryScopeOrderColumn.Type.ATTRIBUTE ->
                    orderByAttribute(item)

                TrackedEntityInstanceQueryScopeOrderColumn.Type.ENROLLMENT_DATE ->
                    orderByEnrollmentField(scope.program(), ENROLLMENT_DATE, item.direction())

                TrackedEntityInstanceQueryScopeOrderColumn.Type.INCIDENT_DATE ->
                    orderByEnrollmentField(
                        scope.program(), EnrollmentTableInfo.Columns.INCIDENT_DATE, item.direction()
                    )

                TrackedEntityInstanceQueryScopeOrderColumn.Type.ENROLLMENT_STATUS ->
                    orderByEnrollmentField(scope.program(), EnrollmentTableInfo.Columns.STATUS, item.direction())

                TrackedEntityInstanceQueryScopeOrderColumn.Type.EVENT_DATE -> {
                    val eventField = "IFNULL(${EventTableInfo.Columns.EVENT_DATE},${EventTableInfo.Columns.DUE_DATE})"
                    orderByEventField(scope.program(), eventField, item.direction())
                }

                TrackedEntityInstanceQueryScopeOrderColumn.Type.COMPLETION_DATE ->
                    orderByEventField(scope.program(), EventTableInfo.Columns.COMPLETE_DATE, item.direction())

                else -> null
            }
        } + orderByLastUpdated

        return " ORDER BY ${orderClauses.joinToString(", ")}"
    }

    // TODO In case a program uid is provided, the server orders by enrollmentStatus.
    private val orderByLastUpdated: String
        get() = "$TEI_LAST_UPDATED DESC "

    private fun orderByEnrollmentField(program: String?, field: String, dir: OrderByDirection): String {
        val programClause = if (program == null) "" else "AND ${EnrollmentTableInfo.Columns.PROGRAM} = '$program'"
        return String.format(
            "IFNULL((SELECT %s FROM %s WHERE %s = %s %s ORDER BY %s DESC LIMIT 1), 'zzzzz') %s",
            field,
            EnrollmentTableInfo.TABLE_INFO.name(),
            EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
            dot(TEI_ALIAS, IdentifiableColumns.UID),
            programClause,
            EnrollmentTableInfo.Columns.ENROLLMENT_DATE,
            dir.name
        )
    }

    private fun orderByEventField(program: String?, field: String, dir: OrderByDirection): String {
        val programClause = if (program == null) "" else "AND ${EnrollmentTableInfo.Columns.PROGRAM} = '$program'"
        return String.format(
            "(SELECT %s FROM %s WHERE %s IN (SELECT %s FROM %s WHERE %s = %s %s) " +
                "ORDER BY IFNULL(%s, %s) DESC LIMIT 1) %s",
            field,
            EventTableInfo.TABLE_INFO.name(),
            EventTableInfo.Columns.ENROLLMENT,
            IdentifiableColumns.UID,
            EnrollmentTableInfo.TABLE_INFO.name(),
            EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
            dot(TEI_ALIAS, IdentifiableColumns.UID),
            programClause,
            EventTableInfo.Columns.EVENT_DATE, EventTableInfo.Columns.DUE_DATE,
            dir.name
        )
    }

    private fun orderByAttribute(item: TrackedEntityInstanceQueryScopeOrderByItem): String {
        // Trick to put null values at the end of the list
        val attOrder = String.format(
            "IFNULL((SELECT %s FROM %s WHERE %s = %s AND %s = %s), 'zzzzzzzz')",
            TrackedEntityAttributeValueTableInfo.Columns.VALUE,
            TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(),
            TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
            CollectionsHelper.withSingleQuotationMarks(item.column().value()),
            TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
            dot(TEI_ALIAS, IdentifiableColumns.UID)
        )
        return "$attOrder ${item.direction().name}"
    }

    private fun dot(item1: String, item2: String): String {
        return "$item1.$item2"
    }

    private fun escapeQuotes(value: String?): String {
        return value!!.replace("'".toRegex(), "''")
    }
}
