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
package org.hisp.dhis.android.core.trackedentity.search

import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter

internal object TrackedEntityInstanceQueryRepositoryScopeHelper {

    @JvmStatic
    fun setEventDateFilter(
        scope: TrackedEntityInstanceQueryRepositoryScope,
        dateFilter: DateFilterPeriod
    ): TrackedEntityInstanceQueryRepositoryScope {
        val eventFilters = getScopeEventFiltersOrInitial(scope).map { eventFilter ->
            val eventDateFilter = DateFilterPeriodHelper.mergeDateFilterPeriods(eventFilter.eventDate(), dateFilter)
            eventFilter.toBuilder().eventDate(eventDateFilter).build()
        }
        return scope.toBuilder().eventFilters(eventFilters).build()
    }

    @JvmStatic
    fun setAssignedUserMode(
        scope: TrackedEntityInstanceQueryRepositoryScope,
        assignedUserMode: AssignedUserMode
    ): TrackedEntityInstanceQueryRepositoryScope {

        val eventFilters = getScopeEventFiltersOrInitial(scope).map {
            it.toBuilder().assignedUserMode(assignedUserMode).build()
        }
        return scope.toBuilder().eventFilters(eventFilters).build()
    }

    @JvmStatic
    fun setEventStatus(
        scope: TrackedEntityInstanceQueryRepositoryScope,
        eventStatus: List<EventStatus>
    ): TrackedEntityInstanceQueryRepositoryScope {

        val eventFilters = getScopeEventFiltersOrInitial(scope).map {
            it.toBuilder().eventStatus(eventStatus).build()
        }
        return scope.toBuilder().eventFilters(eventFilters).build()
    }

    @JvmStatic
    @Suppress("ComplexMethod")
    fun addTrackedEntityInstanceFilter(
        scope: TrackedEntityInstanceQueryRepositoryScope,
        filter: TrackedEntityInstanceFilter
    ): TrackedEntityInstanceQueryRepositoryScope {
        val builder = scope.toBuilder()

        filter.program()?.let { builder.program(it.uid()) }
        filter.enrollmentStatus()?.let { builder.enrollmentStatus(listOf(it)) }
        filter.enrollmentCreatedPeriod()?.let { createPeriod ->
            createPeriod.periodFrom()?.let { periodFrom ->
                val fromFilter = DateFilterPeriod.builder().startBuffer(periodFrom).build()
                val newFilter = DateFilterPeriodHelper.mergeDateFilterPeriods(builder.build().programDate(), fromFilter)
                builder.programDate(newFilter)
            }
            createPeriod.periodTo()?. let { periodTo ->
                val toFilter = DateFilterPeriod.builder().endBuffer(periodTo).build()
                val newFilter = DateFilterPeriodHelper.mergeDateFilterPeriods(builder.build().programDate(), toFilter)
                builder.programDate(newFilter)
            }
        }
        filter.followUp()?.let {
            // TODO
        }
        filter.eventFilters()?.let { eventFilters ->
            val filters = eventFilters.map { eventFilter ->
                val eventBuilder = TrackedEntityInstanceQueryEventFilter.builder()

                eventFilter.programStage()?.let { eventBuilder.programStage(it) }
                eventFilter.eventStatus()?.let { eventBuilder.eventStatus(listOf(it)) }
                eventFilter.eventCreatedPeriod()?.let { createPeriod ->
                    createPeriod.periodFrom()?.let { periodFrom ->
                        val fromFilter = DateFilterPeriod.builder().startBuffer(periodFrom).build()
                        val newFilter =
                            DateFilterPeriodHelper.mergeDateFilterPeriods(eventBuilder.build().eventDate(), fromFilter)
                        eventBuilder.eventDate(newFilter)
                    }
                    createPeriod.periodTo()?.let { periodTo ->
                        val toFilter = DateFilterPeriod.builder().endBuffer(periodTo).build()
                        val newFilter =
                            DateFilterPeriodHelper.mergeDateFilterPeriods(eventBuilder.build().eventDate(), toFilter)
                        eventBuilder.eventDate(newFilter)
                    }
                }
                eventFilter.assignedUserMode()?.let { eventBuilder.assignedUserMode(it) }

                eventBuilder.build()
            }
            if (filters.isNotEmpty()) {
                builder.eventFilters(filters)
            }
        }

        return builder.build()
    }

    private fun getScopeEventFiltersOrInitial(
        scope: TrackedEntityInstanceQueryRepositoryScope
    ): List<TrackedEntityInstanceQueryEventFilter> {
        return if (scope.eventFilters().isEmpty()) {
            listOf(TrackedEntityInstanceQueryEventFilter.builder().build())
        } else {
            scope.eventFilters()
        }
    }
}
