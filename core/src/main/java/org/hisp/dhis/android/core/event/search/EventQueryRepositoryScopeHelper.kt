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
package org.hisp.dhis.android.core.event.search

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.event.EventFilter

internal object EventQueryRepositoryScopeHelper {

    @JvmStatic
    @Suppress("ComplexMethod")
    fun addEventFilter(
        scope: EventQueryRepositoryScope,
        filter: EventFilter
    ): EventQueryRepositoryScope {
        val builder = scope.toBuilder()

        filter.program()?.let { builder.program(it) }
        filter.programStage()?.let { builder.programStage(it) }
        filter.eventQueryCriteria()?.let { criteria ->
            criteria.followUp()?.let { builder.followUp(it) }
            criteria.organisationUnit()?.let { builder.orgUnits(listOf(it)) }
            criteria.ouMode()?.let { builder.orgUnitMode(it) }
            criteria.assignedUserMode()?.let { builder.assignedUserMode(it) }
            criteria.order()?.let { builder.order(parseOrderString(it)) }
            criteria.dataFilters()?.let { builder.dataFilters(it) }
            criteria.events()?.let { builder.events(it) }
            criteria.eventStatus()?.let { builder.eventStatus(listOf(it)) }
            criteria.eventDate()?.let { builder.eventDate(it) }
            criteria.dueDate()?.let { builder.dueDate(it) }
            criteria.lastUpdatedDate()?.let { builder.lastUpdatedDate(it) }
            criteria.completedDate()?.let { builder.completedDate(it) }
        }

        return builder.build()
    }

    private fun parseOrderString(orderStr: String): List<EventQueryScopeOrderByItem> {
        return orderStr.split(",").mapNotNull { token ->
            val tokens = token.split(":")

            when (tokens.size) {
                2 -> {
                    val column = parseOrderColumn(tokens[0])
                    val direction = RepositoryScope.OrderByDirection.values().find { it.api == tokens[1] }

                    if (column != null && direction != null) {
                        EventQueryScopeOrderByItem.builder()
                            .column(column)
                            .direction(direction)
                            .build()
                    } else {
                        null
                    }
                }
                else -> null
            }
        }
    }

    private fun parseOrderColumn(orderColumn: String): EventQueryScopeOrderColumn? {
        val fixedColumn = EventQueryScopeOrderColumn.fixedOrderColumns.find { it.apiName() == orderColumn }

        return when {
            fixedColumn != null -> fixedColumn
            Regex("\\w{11}").matches(orderColumn) -> EventQueryScopeOrderColumn.dataElement(orderColumn)
            else -> null
        }
    }
}
