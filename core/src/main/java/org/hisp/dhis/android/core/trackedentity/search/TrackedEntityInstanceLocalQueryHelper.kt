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
package org.hisp.dhis.android.core.trackedentity.search

import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope.OrderByDirection
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.common.FilterOperatorsHelper.strToList
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.program.AccessLevel
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.persistence.program.ProgramTableInfo
import org.hisp.dhis.android.persistence.trackedentity.ProgramOwnerTableInfo
import org.hisp.dhis.android.persistence.trackedentity.ProgramTempOwnerTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceTableInfo
import org.hisp.dhis.android.persistence.user.AuthenticatedUserTableInfo
import org.hisp.dhis.android.persistence.user.UserOrganisationUnitTableInfo
import org.koin.core.annotation.Singleton
import java.util.Date

@Singleton
@Suppress("TooManyFunctions")
internal class TrackedEntityInstanceLocalQueryHelper(
    private val dateFilterPeriodHelper: DateFilterPeriodHelper,
) {
    private val teiAlias = "tei"
    private val enrollmentAlias = "en"
    private val eventAlias = "ev"
    private val orgunitAlias = "ou"
    private val userOrgunitAlias = "uou"
    private val teavAlias = "teav"
    private val tedvAlias = "tedv"
    private val programAlias = "pr"
    private val ownerAlias = "po"
    private val tempOwnerAlias = "tpo"

    private val teiUid = dot(teiAlias, "uid")
    private val teiAll = dot(teiAlias, "*")
    private val teiLastUpdated = dot(teiAlias, "lastUpdated")
    private val enrollmentDate = EnrollmentTableInfo.Columns.ENROLLMENT_DATE
    private val incidentDate = EnrollmentTableInfo.Columns.INCIDENT_DATE
    private val program = EnrollmentTableInfo.Columns.PROGRAM
    private val trackedEntityAttribute = TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE
    private val trackedEntityInstance = TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE

    fun getSqlQuery(scope: TrackedEntityInstanceQueryRepositoryScope, excludeList: Set<String>?, limit: Int): String {
        return getSqlQuery(scope, excludeList, limit, teiAll)
    }

    fun getUidsWhereClause(
        scope: TrackedEntityInstanceQueryRepositoryScope,
        excludeList: Set<String>?,
        limit: Int,
    ): String {
        val selectSubQuery = getSqlQuery(scope, excludeList, limit, teiUid)
        return WhereClauseBuilder()
            .appendInSubQuery(IdentifiableColumns.UID, selectSubQuery)
            .build()
    }

    @Suppress("LongMethod")
    private fun getSqlQuery(
        scope: TrackedEntityInstanceQueryRepositoryScope,
        excludeList: Set<String>?,
        limit: Int,
        columns: String,
    ): String {
        var queryStr = "SELECT DISTINCT $columns FROM ${TrackedEntityInstanceTableInfo.TABLE_INFO.name()} $teiAlias"

        val where = WhereClauseBuilder()
        if (hasProgram(scope)) {
            queryStr += " JOIN ${EnrollmentTableInfo.TABLE_INFO.name()} $enrollmentAlias"
            queryStr += " ON ${dot(teiAlias, IdentifiableColumns.UID)} = " +
                dot(enrollmentAlias, EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE)

            queryStr += " JOIN ${ProgramTableInfo.TABLE_INFO.name()} $programAlias"
            queryStr += " ON ${dot(enrollmentAlias, EnrollmentTableInfo.Columns.PROGRAM)} = " +
                dot(programAlias, IdentifiableColumns.UID)

            queryStr += " JOIN ${ProgramOwnerTableInfo.TABLE_INFO.name()} $ownerAlias"
            queryStr += " ON ${dot(enrollmentAlias, EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE)} = " +
                dot(ownerAlias, ProgramOwnerTableInfo.Columns.TRACKED_ENTITY_INSTANCE) +
                " AND " +
                "${dot(enrollmentAlias, EnrollmentTableInfo.Columns.PROGRAM)} = " +
                dot(ownerAlias, ProgramOwnerTableInfo.Columns.PROGRAM)

            appendProgramWhere(where, scope)
            if (hasEvent(scope)) {
                queryStr += String.format(
                    " JOIN %s %s ON %s = %s",
                    EventTableInfo.TABLE_INFO.name(),
                    eventAlias,
                    dot(enrollmentAlias, IdentifiableColumns.UID),
                    dot(eventAlias, EventTableInfo.Columns.ENROLLMENT),
                )
                appendEventWhere(where, scope)
            }
        }

        if (hasOrgunits(scope)) {
            val joinOrgunitColum =
                if (hasProgram(scope)) {
                    dot(ownerAlias, ProgramOwnerTableInfo.Columns.OWNER_ORG_UNIT)
                } else {
                    dot(teiAlias, TrackedEntityInstanceTableInfo.Columns.ORGANISATION_UNIT)
                }

            queryStr += String.format(
                " JOIN %s %s ON %s = %s",
                OrganisationUnitTableInfo.TABLE_INFO.name(),
                orgunitAlias,
                joinOrgunitColum,
                dot(orgunitAlias, IdentifiableColumns.UID),
            )
            appendOrgunitWhere(where, scope)
        }

        if (scope.trackedEntityType() != null) {
            where.appendKeyStringValue(
                dot(teiAlias, TrackedEntityInstanceTableInfo.Columns.TRACKED_ENTITY_TYPE),
                escapeQuotes(scope.trackedEntityType()),
            )
        }

        if (!scope.uids().isNullOrEmpty()) {
            where.appendInKeyStringValues(
                dot(teiAlias, TrackedEntityInstanceTableInfo.Columns.UID),
                scope.uids(),
            )
        }

        scope.states()?.let {
            where.appendInKeyEnumValues(dot(teiAlias, DataColumns.AGGREGATED_SYNC_STATE), it)
        } ?: run {
            where.appendNotKeyStringValue(dot(teiAlias, DataColumns.AGGREGATED_SYNC_STATE), State.RELATIONSHIP.name)
        }

        if (!scope.includeDeleted()) {
            where.appendKeyOperatorValue(dot(teiAlias, TrackedEntityInstanceTableInfo.Columns.DELETED), "!=", "1")
        }

        scope.lastUpdatedDate()?.let {
            appendDateFilter(where, dot(teiAlias, TrackedEntityInstanceTableInfo.Columns.LAST_UPDATED), it)
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
        return scope.eventFilters().isNotEmpty() || scope.dataValue().isNotEmpty() || scope.programStage() != null ||
            scope.eventDate() != null || scope.dueDate() != null || scope.eventCreatedDate() != null ||
            scope.assignedUserMode() != null || !scope.eventStatus().isNullOrEmpty()
    }

    @Suppress("LongMethod")
    private fun appendProgramWhere(where: WhereClauseBuilder, scope: TrackedEntityInstanceQueryRepositoryScope) {
        if (scope.program() != null) {
            where.appendKeyStringValue(dot(enrollmentAlias, program), escapeQuotes(scope.program()))
        }
        if (scope.programDate() != null) {
            appendDateFilter(
                where = where,
                column = dot(enrollmentAlias, enrollmentDate),
                dateFilterPeriod = scope.programDate()!!,
            )
        }
        if (scope.incidentDate() != null) {
            appendDateFilter(
                where = where,
                column = dot(enrollmentAlias, incidentDate),
                dateFilterPeriod = scope.incidentDate()!!,
            )
        }
        scope.enrollmentStatus()?.let {
            where.appendInKeyEnumValues(dot(enrollmentAlias, EnrollmentTableInfo.Columns.STATUS), it)
        }
        if (!scope.includeDeleted()) {
            where.appendKeyOperatorValue(dot(enrollmentAlias, EnrollmentTableInfo.Columns.DELETED), "!=", "1")
        }
        if (scope.followUp() != null) {
            val value = if (scope.followUp() == true) 1 else 0
            where.appendKeyNumberValue(dot(enrollmentAlias, EnrollmentTableInfo.Columns.FOLLOWUP), value)
        }

        val hasAnyOwnershipRecord = "SELECT 1 FROM ${ProgramTempOwnerTableInfo.TABLE_INFO.name()} $tempOwnerAlias " +
            "WHERE ${dot(tempOwnerAlias, ProgramTempOwnerTableInfo.Columns.TRACKED_ENTITY_INSTANCE)} = " +
            dot(teiAlias, IdentifiableColumns.UID) +
            " AND " +
            "${dot(tempOwnerAlias, ProgramTempOwnerTableInfo.Columns.PROGRAM)} = " +
            dot(programAlias, IdentifiableColumns.UID)

        val hasAnyNotExpiredOwnershipRecord = hasAnyOwnershipRecord +
            " AND " +
            "${dot(tempOwnerAlias, ProgramTempOwnerTableInfo.Columns.VALID_UNTIL)} " +
            ">= '${DateUtils.DATE_FORMAT.format(Date())}'"

        val ownerOrguitIsInCaptureScope =
            "SELECT 1 FROM ${UserOrganisationUnitTableInfo.TABLE_INFO.name()} $userOrgunitAlias " +
                "WHERE ${dot(userOrgunitAlias, UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT)} = " +
                dot(ownerAlias, ProgramOwnerTableInfo.Columns.OWNER_ORG_UNIT) +
                " AND " +
                "${dot(userOrgunitAlias, UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT_SCOPE)} = " +
                "'${OrganisationUnit.Scope.SCOPE_DATA_CAPTURE.name}'"

        /* Break the glass query. The condition is either of:
         * - Not to have a record: this applies to CAPTURE teis and SEARCH teis that didn't need a ownership request.
         * - Owner orgunit in CAPTURE scope. It could happen that there is record and the ownership is transferred.
         * - To have a record that is not expired.
         */
        where.appendComplexQuery(
            "CASE " +
                "WHEN ${dot(programAlias, ProgramTableInfo.Columns.ACCESS_LEVEL)} = '${AccessLevel.PROTECTED.name}' " +
                "THEN (" +
                "NOT EXISTS($hasAnyOwnershipRecord) " +
                "OR " +
                "EXISTS($ownerOrguitIsInCaptureScope) " +
                "OR " +
                "EXISTS($hasAnyNotExpiredOwnershipRecord)" +
                ") ELSE 1 END ",
        )
    }

    private fun hasOrgunits(scope: TrackedEntityInstanceQueryRepositoryScope): Boolean {
        return (
            (
                scope.orgUnits().isNotEmpty() &&
                    OrganisationUnitMode.ALL != scope.orgUnitMode() &&
                    OrganisationUnitMode.ACCESSIBLE != scope.orgUnitMode()
                ) ||
                OrganisationUnitMode.CAPTURE == scope.orgUnitMode() ||
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
                    dot(orgunitAlias, OrganisationUnitTableInfo.Columns.PATH),
                    "%" + escapeQuotes(orgUnit) + "%",
                )
            }
            OrganisationUnitMode.CHILDREN -> scope.orgUnits().forEach { orgUnit ->
                inner.appendOrKeyStringValue(
                    dot(orgunitAlias, OrganisationUnitTableInfo.Columns.PARENT),
                    escapeQuotes(orgUnit),
                )

                // TODO Include orgunit?
                inner.appendOrKeyStringValue(dot(orgunitAlias, IdentifiableColumns.UID), escapeQuotes(orgUnit))
            }
            OrganisationUnitMode.CAPTURE ->
                inner.appendComplexQuery(
                    String.format(
                        "%s IN (SELECT %s FROM %s WHERE %s = '%s')",
                        dot(orgunitAlias, IdentifiableColumns.UID),
                        UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT,
                        UserOrganisationUnitTableInfo.TABLE_INFO.name(),
                        UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
                        OrganisationUnit.Scope.SCOPE_DATA_CAPTURE.name,
                    ),
                )
            OrganisationUnitMode.SELECTED -> scope.orgUnits().forEach { orgUnit ->
                inner.appendOrKeyStringValue(dot(orgunitAlias, IdentifiableColumns.UID), escapeQuotes(orgUnit))
            }
            OrganisationUnitMode.ACCESSIBLE, OrganisationUnitMode.ALL -> {
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
                    if (query.operator() == FilterItemOperator.LIKE) {
                        "%${escapeQuotes(token)}%"
                    } else {
                        escapeQuotes(token)
                    }

                val sub = String.format(
                    "SELECT 1 FROM %s %s WHERE %s = %s AND %s %s '%s'",
                    TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(),
                    teavAlias,
                    dot(teavAlias, trackedEntityInstance),
                    dot(teiAlias, IdentifiableColumns.UID),
                    dot(teavAlias, TrackedEntityAttributeValueTableInfo.Columns.VALUE),
                    query.operator().sqlOperator,
                    valueStr,
                )

                where.appendExistsSubQuery(sub)
            }
        }
    }

    private fun appendFiltersWhere(where: WhereClauseBuilder, scope: TrackedEntityInstanceQueryRepositoryScope) {
        for (item in scope.filter()) {
            val sub = String.format(
                "SELECT 1 FROM %s %s WHERE %s = %s AND %s = '%s' AND %s %s %s",
                TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(), teavAlias,
                dot(teavAlias, trackedEntityInstance), dot(teiAlias, IdentifiableColumns.UID),
                dot(teavAlias, trackedEntityAttribute), escapeQuotes(item.key()),
                dot(teavAlias, TrackedEntityAttributeValueTableInfo.Columns.VALUE),
                item.operator().sqlOperator,
                getFilterItemValueStr(item),
            )

            where.appendExistsSubQuery(sub)
        }
    }

    private fun appendExcludeList(where: WhereClauseBuilder, excludeList: Set<String>?) {
        if (!excludeList.isNullOrEmpty()) {
            where.appendNotInKeyStringValues(teiUid, excludeList.toList())
        }
    }

    private fun appendEventWhere(where: WhereClauseBuilder, scope: TrackedEntityInstanceQueryRepositoryScope) {
        scope.assignedUserMode()?.let { appendAssignedUserMode(where, it) }
        scope.programStage()?.let { programStage ->
            where.appendKeyStringValue(dot(eventAlias, EventTableInfo.Columns.PROGRAM_STAGE), programStage)
        }
        appendEventStatusAndDates(
            where = where,
            eventCreatedDate = scope.eventCreatedDate(),
            eventStatusList = scope.eventStatus(),
            eventDate = scope.eventDate(),
            dueDate = scope.dueDate(),
        )

        val innerClause = WhereClauseBuilder()
        scope.eventFilters().forEach { eventFilter ->
            getEventFilterClause(eventFilter)?.let { eventFilterClause ->
                innerClause.appendOrComplexQuery(eventFilterClause)
            }
        }
        if (!innerClause.isEmpty) {
            where.appendComplexQuery(innerClause.build())
        }

        appendDataValues(where, scope.dataValue())

        where.appendKeyOperatorValue(dot(eventAlias, EventTableInfo.Columns.DELETED), "!=", "1")
    }

    private fun getEventFilterClause(eventFilter: TrackedEntityInstanceQueryEventFilter): String? {
        val innerClause = WhereClauseBuilder()

        eventFilter.assignedUserMode()?.let { mode -> appendAssignedUserMode(innerClause, mode) }
        eventFilter.programStage()?.let { programStage ->
            innerClause.appendKeyStringValue(dot(eventAlias, EventTableInfo.Columns.PROGRAM_STAGE), programStage)
        }
        appendEventStatusAndDates(
            where = innerClause,
            eventCreatedDate = null,
            eventStatusList = eventFilter.eventStatus(),
            eventDate = eventFilter.eventDate(),
            dueDate = null,
        )

        return if (innerClause.isEmpty) null else innerClause.build()
    }

    private fun appendEventStatusAndDates(
        where: WhereClauseBuilder,
        eventCreatedDate: DateFilterPeriod?,
        eventStatusList: List<EventStatus>?,
        eventDate: DateFilterPeriod?,
        dueDate: DateFilterPeriod?,
    ) {
        if (eventStatusList == null) {
            appendEventDates(where, eventDate, EventTableInfo.Columns.EVENT_DATE)
            appendEventDates(where, dueDate, EventTableInfo.Columns.DUE_DATE)
            appendEventDates(where, eventCreatedDate, EventTableInfo.Columns.CREATED)
        } else if (eventStatusList.isNotEmpty()) {
            val nowStr = DateUtils.SIMPLE_DATE_FORMAT.format(Date())
            val statusListWhere = WhereClauseBuilder()
            for (eventStatus in eventStatusList) {
                val statusWhere = WhereClauseBuilder()
                when (eventStatus) {
                    EventStatus.ACTIVE -> {
                        statusWhere.appendIsNotNullValue(EventTableInfo.Columns.EVENT_DATE)
                        statusWhere.appendInKeyEnumValues(
                            dot(eventAlias, EventTableInfo.Columns.STATUS),
                            listOf(EventStatus.ACTIVE, EventStatus.SCHEDULE, EventStatus.OVERDUE),
                        )
                    }
                    EventStatus.COMPLETED, EventStatus.VISITED -> {
                        statusWhere.appendKeyStringValue(dot(eventAlias, EventTableInfo.Columns.STATUS), eventStatus)
                    }
                    EventStatus.SCHEDULE -> {
                        statusWhere.appendIsNullValue(EventTableInfo.Columns.EVENT_DATE)
                        statusWhere.appendInKeyEnumValues(
                            dot(eventAlias, EventTableInfo.Columns.STATUS),
                            listOf(EventStatus.SCHEDULE, EventStatus.OVERDUE),
                        )
                        statusWhere.appendKeyGreaterOrEqStringValue(
                            "date(${dot(eventAlias, EventTableInfo.Columns.DUE_DATE)})",
                            nowStr,
                        )
                    }
                    EventStatus.OVERDUE -> {
                        statusWhere.appendIsNullValue(EventTableInfo.Columns.EVENT_DATE)
                        statusWhere.appendInKeyEnumValues(
                            dot(eventAlias, EventTableInfo.Columns.STATUS),
                            listOf(EventStatus.SCHEDULE, EventStatus.OVERDUE),
                        )
                        statusWhere.appendKeyLessThanStringValue(
                            "date(${dot(eventAlias, EventTableInfo.Columns.DUE_DATE)})",
                            nowStr,
                        )
                    }
                    EventStatus.SKIPPED -> {
                        statusWhere.appendKeyStringValue(dot(eventAlias, EventTableInfo.Columns.STATUS), eventStatus)
                    }
                }
                val eventDateComparisonColum = when (eventStatus) {
                    EventStatus.ACTIVE, EventStatus.COMPLETED -> EventTableInfo.Columns.EVENT_DATE
                    else -> EventTableInfo.Columns.DUE_DATE
                }
                appendEventDates(statusWhere, eventDate, eventDateComparisonColum)
                appendEventDates(statusWhere, dueDate, EventTableInfo.Columns.DUE_DATE)
                appendEventDates(statusWhere, eventCreatedDate, EventTableInfo.Columns.CREATED)
                statusListWhere.appendOrComplexQuery(statusWhere.build())
            }
            where.appendComplexQuery(statusListWhere.build())
        }
    }

    private fun appendEventDates(
        where: WhereClauseBuilder,
        date: DateFilterPeriod?,
        refDate: String,
    ) {
        if (date != null) {
            appendDateFilter(
                where = where,
                column = dot(eventAlias, refDate),
                dateFilterPeriod = date,
            )
        }
    }

    private fun appendDateFilter(
        where: WhereClauseBuilder,
        column: String,
        dateFilterPeriod: DateFilterPeriod,
    ) {
        val dateColumnStr = "date($column)"

        dateFilterPeriodHelper.getStartDate(dateFilterPeriod)?.let { startDate ->
            val startDateStr = DateUtils.SIMPLE_DATE_FORMAT.format(startDate)
            where.appendKeyGreaterOrEqStringValue(dateColumnStr, startDateStr)
        }

        dateFilterPeriodHelper.getEndDate(dateFilterPeriod)?.let { endDate ->
            val endDateStr = DateUtils.SIMPLE_DATE_FORMAT.format(endDate)
            where.appendKeyLessThanOrEqStringValue(dateColumnStr, endDateStr)
        }
    }

    private fun appendAssignedUserMode(
        where: WhereClauseBuilder,
        mode: AssignedUserMode,
    ) {
        val assignedUserColumn = dot(eventAlias, EventTableInfo.Columns.ASSIGNED_USER)
        when (mode) {
            AssignedUserMode.CURRENT -> {
                val subquery = String.format(
                    "(SELECT %s FROM %s LIMIT 1)",
                    AuthenticatedUserTableInfo.Columns.USER,
                    AuthenticatedUserTableInfo.TABLE_INFO.name(),
                )
                where.appendKeyOperatorValue(assignedUserColumn, "IN", subquery)
            }
            AssignedUserMode.ANY -> where.appendIsNotNullValue(assignedUserColumn)
            AssignedUserMode.NONE -> where.appendIsNullValue(assignedUserColumn)
            else -> {
            }
        }
    }

    private fun appendDataValues(
        where: WhereClauseBuilder,
        dataValues: List<RepositoryScopeFilterItem>,
    ) {
        dataValues
            .groupBy { it.key() }
            .forEach { (key, items) ->
                val sub = "SELECT 1 FROM ${TrackedEntityDataValueTableInfo.TABLE_INFO.name()} $tedvAlias " +
                    "WHERE ${dot(tedvAlias, TrackedEntityDataValueTableInfo.Columns.EVENT)} = " +
                    "${dot(eventAlias, IdentifiableColumns.UID)} " +
                    "AND ${dot(tedvAlias, TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT)} = " +
                    "'${escapeQuotes(key)}' " +

                    items.joinToString("") { item ->
                        "AND ${dot(tedvAlias, TrackedEntityDataValueTableInfo.Columns.VALUE)} " +
                            "${item.operator().sqlOperator} " +
                            "${getFilterItemValueStr(item)} "
                    }

                where.appendExistsSubQuery(sub)
            }
    }

    private fun getFilterItemValueStr(item: RepositoryScopeFilterItem): String {
        return when (item.operator()) {
            FilterItemOperator.LIKE -> "'%${escapeQuotes(item.value())}%'"
            FilterItemOperator.SW -> "'${escapeQuotes(item.value())}%'"
            FilterItemOperator.EW -> "'%${escapeQuotes(item.value())}'"
            FilterItemOperator.IN -> {
                val value = strToList(item.value()).joinToString(separator = ",") { "'${escapeQuotes(it)}'" }
                "($value)"
            }
            else -> "'${escapeQuotes(item.value())}'"
        }
    }

    private fun orderByClause(scope: TrackedEntityInstanceQueryRepositoryScope): String {
        val orderClauses = scope.order().mapNotNull { item ->
            when (item.column().type()) {
                TrackedEntityInstanceQueryScopeOrderColumn.Type.CREATED ->
                    if (hasProgram(scope)) {
                        orderByEnrollmentField(scope.program(), IdentifiableColumns.CREATED, item.direction())
                    } else {
                        dot(teiAlias, IdentifiableColumns.CREATED) + " " + item.direction().name
                    }

                TrackedEntityInstanceQueryScopeOrderColumn.Type.LAST_UPDATED ->
                    if (hasProgram(scope)) {
                        orderByEnrollmentField(scope.program(), IdentifiableColumns.LAST_UPDATED, item.direction())
                    } else {
                        dot(teiAlias, IdentifiableColumns.LAST_UPDATED) + " " + item.direction().name
                    }

                TrackedEntityInstanceQueryScopeOrderColumn.Type.ORGUNIT_NAME ->
                    dot(orgunitAlias, IdentifiableColumns.NAME) + " " + item.direction().name

                TrackedEntityInstanceQueryScopeOrderColumn.Type.ATTRIBUTE ->
                    orderByAttribute(item)

                TrackedEntityInstanceQueryScopeOrderColumn.Type.ENROLLMENT_DATE ->
                    orderByEnrollmentField(scope.program(), enrollmentDate, item.direction())

                TrackedEntityInstanceQueryScopeOrderColumn.Type.INCIDENT_DATE ->
                    orderByEnrollmentField(
                        scope.program(),
                        EnrollmentTableInfo.Columns.INCIDENT_DATE,
                        item.direction(),
                    )

                TrackedEntityInstanceQueryScopeOrderColumn.Type.ENROLLMENT_STATUS ->
                    orderByEnrollmentField(scope.program(), EnrollmentTableInfo.Columns.STATUS, item.direction())

                TrackedEntityInstanceQueryScopeOrderColumn.Type.EVENT_DATE -> {
                    val eventField = "IFNULL(${EventTableInfo.Columns.EVENT_DATE},${EventTableInfo.Columns.DUE_DATE})"
                    orderByEventField(scope.program(), eventField, item.direction())
                }

                TrackedEntityInstanceQueryScopeOrderColumn.Type.COMPLETION_DATE ->
                    orderByEventField(scope.program(), EventTableInfo.Columns.COMPLETED_DATE, item.direction())

                else -> null
            }
        } + orderByLastUpdated

        return " ORDER BY ${orderClauses.joinToString(", ")}"
    }

    // TODO In case a program uid is provided, the server orders by enrollmentStatus.
    private val orderByLastUpdated: String
        get() = "$teiLastUpdated DESC "

    private fun orderByEnrollmentField(program: String?, field: String, dir: OrderByDirection): String {
        val programClause = if (program == null) "" else "AND ${EnrollmentTableInfo.Columns.PROGRAM} = '$program'"
        return String.format(
            "IFNULL((SELECT %s FROM %s WHERE %s = %s %s ORDER BY %s DESC LIMIT 1), 'zzzzz') %s",
            field,
            EnrollmentTableInfo.TABLE_INFO.name(),
            EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
            dot(teiAlias, IdentifiableColumns.UID),
            programClause,
            EnrollmentTableInfo.Columns.ENROLLMENT_DATE,
            dir.name,
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
            dot(teiAlias, IdentifiableColumns.UID),
            programClause,
            EventTableInfo.Columns.EVENT_DATE, EventTableInfo.Columns.DUE_DATE,
            dir.name,
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
            dot(teiAlias, IdentifiableColumns.UID),
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
