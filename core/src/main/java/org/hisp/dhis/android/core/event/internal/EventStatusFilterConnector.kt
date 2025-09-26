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
package org.hisp.dhis.android.core.event.internal

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toJavaDate
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeComplexFilterItem
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory
import org.hisp.dhis.android.persistence.event.EventTableInfo

@Suppress("TooManyFunctions")
class EventStatusFilterConnector internal constructor(
    private val repositoryFactory: BaseRepositoryFactory<EventCollectionRepository>,
    private val scope: RepositoryScope,
    key: String,
) {
    private val standardConnector = EnumFilterConnector<EventCollectionRepository, EventStatus>(
        repositoryFactory,
        scope,
        key,
    )

    private val scheduleAndOverdueStatuses = listOf(EventStatus.SCHEDULE, EventStatus.OVERDUE)
    private val scheduleAndOverdueStatusNames = listOf(EventStatus.SCHEDULE.name, EventStatus.OVERDUE.name)
    private val dueDateExpression = "date(${EventTableInfo.Columns.DUE_DATE})"

    fun eq(value: EventStatus?): EventCollectionRepository {
        return when (value) {
            EventStatus.OVERDUE -> createOverdueFilterRepository()
            EventStatus.SCHEDULE -> createScheduleFilterRepository()
            else -> standardConnector.eq(value)
        }
    }

    fun neq(value: EventStatus?): EventCollectionRepository {
        return when (value) {
            EventStatus.OVERDUE -> createNotOverdueFilterRepository()
            EventStatus.SCHEDULE -> createNotScheduleFilterRepository()
            else -> standardConnector.neq(value)
        }
    }

    fun `in`(values: Collection<EventStatus>?): EventCollectionRepository {
        return handleStatusCollection(values, inclusion = true)
    }

    @SafeVarargs
    fun `in`(vararg values: EventStatus): EventCollectionRepository {
        return `in`(listOf(*values))
    }

    fun notIn(values: Collection<EventStatus>?): EventCollectionRepository {
        return handleStatusCollection(values, inclusion = false)
    }

    @SafeVarargs
    fun notIn(vararg values: EventStatus): EventCollectionRepository {
        return notIn(listOf(*values))
    }

    val isNull: EventCollectionRepository
        get() = standardConnector.isNull

    val isNotNull: EventCollectionRepository
        get() = standardConnector.isNotNull

    private fun createOverdueFilterRepository(): EventCollectionRepository {
        return createComplexFilterRepository(buildOverdueCondition(negated = false))
    }

    private fun createNotOverdueFilterRepository(): EventCollectionRepository {
        return createComplexFilterRepository(buildOverdueCondition(negated = true))
    }

    private fun createScheduleFilterRepository(): EventCollectionRepository {
        return createComplexFilterRepository(buildScheduleCondition(negated = false))
    }

    private fun createNotScheduleFilterRepository(): EventCollectionRepository {
        return createComplexFilterRepository(buildScheduleCondition(negated = true))
    }

    @Suppress("ComplexMethod")
    private fun handleStatusCollection(
        values: Collection<EventStatus>?,
        inclusion: Boolean,
    ): EventCollectionRepository {
        if (values.isNullOrEmpty()) {
            return if (inclusion) standardConnector.`in`(values) else standardConnector.notIn(values)
        }

        val containsOverdue = values.contains(EventStatus.OVERDUE)
        val containsSchedule = values.contains(EventStatus.SCHEDULE)
        val regularStatuses = values.filter { it != EventStatus.OVERDUE && it != EventStatus.SCHEDULE }

        return when {
            (containsOverdue || containsSchedule) && regularStatuses.isNotEmpty() -> {
                handleComplexMixedStatusCollection(containsOverdue, containsSchedule, regularStatuses, inclusion)
            }

            containsOverdue && containsSchedule -> {
                handleOverdueAndScheduleCollection(inclusion)
            }

            containsOverdue -> {
                if (inclusion) createOverdueFilterRepository() else createNotOverdueFilterRepository()
            }

            containsSchedule -> {
                if (inclusion) createScheduleFilterRepository() else createNotScheduleFilterRepository()
            }

            else -> {
                if (inclusion) {
                    standardConnector.`in`(regularStatuses)
                } else {
                    standardConnector.notIn(regularStatuses)
                }
            }
        }
    }

    private fun handleComplexMixedStatusCollection(
        containsOverdue: Boolean,
        containsSchedule: Boolean,
        regularStatuses: List<EventStatus>,
        inclusion: Boolean,
    ): EventCollectionRepository {
        val operator = if (inclusion) "IN" else "NOT IN"
        val logicalOperator = if (inclusion) "OR" else "AND"

        val clauses = mutableListOf<String>()

        if (regularStatuses.isNotEmpty()) {
            val regularClause =
                "${EventTableInfo.Columns.STATUS} $operator (${getCommaSeparatedValues(regularStatuses)})"
            clauses.add(regularClause)
        }

        if (containsOverdue) {
            val overdueClause = buildOverdueCondition(negated = !inclusion)
            clauses.add(overdueClause)
        }

        if (containsSchedule) {
            val scheduleClause = buildScheduleCondition(negated = !inclusion)
            clauses.add(scheduleClause)
        }

        return createComplexFilterRepository("(${clauses.joinToString(" $logicalOperator ")})")
    }

    private fun handleOverdueAndScheduleCollection(inclusion: Boolean): EventCollectionRepository {
        val logicalOperator = if (inclusion) "OR" else "AND"
        val overdueClause = buildOverdueCondition(negated = !inclusion)
        val scheduleClause = buildScheduleCondition(negated = !inclusion)

        return createComplexFilterRepository("($overdueClause $logicalOperator $scheduleClause)")
    }

    private fun createComplexFilterRepository(whereClause: String): EventCollectionRepository {
        val updatedScope = RepositoryScopeHelper.withComplexFilterItem(
            scope,
            RepositoryScopeComplexFilterItem.builder().whereQuery(whereClause).build(),
        )
        return repositoryFactory.updated(updatedScope)
    }

    private val currentDateString: String
        get() = DateUtils.SIMPLE_DATE_FORMAT.format(ClockProviderFactory.clockProvider.clock.now().toJavaDate())

    private fun buildNegatedConditionClauses(isOverdue: Boolean): String {
        val innerClause1 = WhereClauseBuilder()
            .appendIsNotNullValue(EventTableInfo.Columns.EVENT_DATE)
            .build()

        val innerClause2 = WhereClauseBuilder()
            .appendNotInKeyStringValues(EventTableInfo.Columns.STATUS, scheduleAndOverdueStatusNames)
            .build()

        val innerClause3 = if (isOverdue) {
            // For overdue negation: due_date >= current_date
            WhereClauseBuilder()
                .appendKeyGreaterOrEqStringValue(dueDateExpression, currentDateString)
                .build()
        } else {
            // For schedule negation: due_date <= current_date
            WhereClauseBuilder()
                .appendKeyLessThanOrEqStringValue(dueDateExpression, currentDateString)
                .build()
        }

        return "($innerClause1 OR $innerClause2 OR $innerClause3)"
    }

    private fun buildCondition(isOverdue: Boolean, negated: Boolean): String {
        return if (negated) {
            buildNegatedConditionClauses(isOverdue)
        } else {
            val builder = WhereClauseBuilder()
                .appendIsNullValue(EventTableInfo.Columns.EVENT_DATE)
                .appendInKeyEnumValues(EventTableInfo.Columns.STATUS, scheduleAndOverdueStatuses)

            if (isOverdue) {
                // overdue if due_date is past
                builder.appendKeyLessThanStringValue(dueDateExpression, currentDateString)
            } else {
                // schedule if due_date is future/today
                builder.appendKeyGreaterOrEqStringValue(dueDateExpression, currentDateString)
            }

            builder.build()
        }
    }

    private fun buildOverdueCondition(negated: Boolean): String {
        return buildCondition(isOverdue = true, negated = negated)
    }

    private fun buildScheduleCondition(negated: Boolean): String {
        return buildCondition(isOverdue = false, negated = negated)
    }

    private fun getCommaSeparatedValues(values: Collection<EventStatus>): String {
        return values.joinToString(", ") { "'${it.name}'" }
    }
}
