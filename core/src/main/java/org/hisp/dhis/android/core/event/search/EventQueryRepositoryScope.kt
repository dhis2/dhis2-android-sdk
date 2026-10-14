/*
 *  Copyright (c) 2004-2025, University of Oslo
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

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.arch.repositories.scope.BaseScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.EventDataFilter
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode

@ModelBuilder
@Suppress("TooManyFunctions")
data class EventQueryRepositoryScope(
    val mode: RepositoryMode,
    val program: String?,
    val programStage: String?,
    val followUp: Boolean?,
    val trackedEntityInstance: String?,
    val orgUnits: List<String>?,
    val orgUnitMode: OrganisationUnitMode,
    val assignedUserMode: AssignedUserMode?,
    val order: List<EventQueryScopeOrderByItem>,
    val dataFilters: List<EventDataFilter>,
    val events: List<String>?,
    val eventStatus: List<EventStatus>?,
    val eventDate: DateFilterPeriod?,
    val dueDate: DateFilterPeriod?,
    val lastUpdatedDate: DateFilterPeriod?,
    val completedDate: DateFilterPeriod?,
    val includeDeleted: Boolean,
    val states: List<State>?,
    val attributeOptionCombos: List<String>?,
) : BaseScope {
    fun mode(): RepositoryMode = mode
    fun program(): String? = program
    fun programStage(): String? = programStage
    fun followUp(): Boolean? = followUp
    fun trackedEntityInstance(): String? = trackedEntityInstance
    fun orgUnits(): List<String>? = orgUnits
    fun orgUnitMode(): OrganisationUnitMode = orgUnitMode
    fun assignedUserMode(): AssignedUserMode? = assignedUserMode
    fun order(): List<EventQueryScopeOrderByItem> = order
    fun dataFilters(): List<EventDataFilter> = dataFilters
    fun events(): List<String>? = events
    fun eventStatus(): List<EventStatus>? = eventStatus
    fun eventDate(): DateFilterPeriod? = eventDate
    fun dueDate(): DateFilterPeriod? = dueDate
    fun lastUpdatedDate(): DateFilterPeriod? = lastUpdatedDate
    fun completedDate(): DateFilterPeriod? = completedDate
    fun includeDeleted(): Boolean = includeDeleted
    fun states(): List<State>? = states
    fun attributeOptionCombos(): List<String>? = attributeOptionCombos

    internal fun toBuilder(): Builder = EventQueryRepositoryScopeBuilder.from(this)

    internal class Builder : EventQueryRepositoryScopeBuilder() {
        override fun build(): EventQueryRepositoryScope {
            if (states != null) mode(RepositoryMode.OFFLINE_ONLY)
            return super.build()
        }
    }

    companion object {
        @JvmStatic
        internal fun builder(): Builder = Builder()
            .order(emptyList())
            .dataFilters(emptyList())
            .mode(RepositoryMode.OFFLINE_ONLY)
            .orgUnitMode(OrganisationUnitMode.SELECTED)
            .includeDeleted(false)

        internal fun empty(): EventQueryRepositoryScope = builder().build()
    }
}
