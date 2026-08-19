/*
 *  Copyright (c) 2004-2026, University of Oslo
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

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.arch.repositories.scope.BaseScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode

@ModelBuilder
@Suppress("TooManyFunctions")
data class TrackedEntityInstanceQueryRepositoryScope(
    val mode: RepositoryMode,
    val orgUnits: List<String>,
    val orgUnitMode: OrganisationUnitMode?,
    val program: String?,
    val programStage: String?,
    val query: RepositoryScopeFilterItem?,
    val filter: List<RepositoryScopeFilterItem>,
    val dataValue: List<RepositoryScopeFilterItem>,
    val programDate: DateFilterPeriod?,
    val incidentDate: DateFilterPeriod?,
    val enrollmentStatus: List<EnrollmentStatus>?,
    val eventCreatedDate: DateFilterPeriod?,
    val eventDate: DateFilterPeriod?,
    val dueDate: DateFilterPeriod?,
    val eventStatus: List<EventStatus>?,
    val assignedUserMode: AssignedUserMode?,
    val trackedEntityType: String?,
    val includeDeleted: Boolean,
    val states: List<State>?,
    val followUp: Boolean?,
    val eventFilters: List<TrackedEntityInstanceQueryEventFilter>,
    val lastUpdatedDate: DateFilterPeriod?,
    val order: List<TrackedEntityInstanceQueryScopeOrderByItem>,
    val allowOnlineCache: Boolean,
    val excludedUids: Set<String>?,
    val uids: List<String>?,
    /**
     * A restriction that survives every `by*()` call, or null for an unrestricted scope.
     *
     * Set only by [ScopedD2][org.hisp.dhis.android.core.scopedaccess.ScopedD2]. The generated
     * builder setter is `internal`, and [applyGrant] re-applies it on every repository
     * construction, so restricted code can neither install nor drop one.
     */
    internal val mandatory: TrackedEntityQueryGrant? = null,
) : BaseScope {

    fun mode(): RepositoryMode = mode
    fun orgUnits(): List<String> = orgUnits
    fun orgUnitMode(): OrganisationUnitMode? = orgUnitMode
    fun program(): String? = program
    fun programStage(): String? = programStage
    fun query(): RepositoryScopeFilterItem? = query

    @Deprecated("Use filter()")
    fun attribute(): List<RepositoryScopeFilterItem> = filter

    fun filter(): List<RepositoryScopeFilterItem> = filter
    fun dataValue(): List<RepositoryScopeFilterItem> = dataValue
    fun programDate(): DateFilterPeriod? = programDate
    fun incidentDate(): DateFilterPeriod? = incidentDate
    fun enrollmentStatus(): List<EnrollmentStatus>? = enrollmentStatus
    fun eventCreatedDate(): DateFilterPeriod? = eventCreatedDate
    fun eventDate(): DateFilterPeriod? = eventDate
    fun dueDate(): DateFilterPeriod? = dueDate
    fun eventStatus(): List<EventStatus>? = eventStatus
    fun assignedUserMode(): AssignedUserMode? = assignedUserMode
    fun trackedEntityType(): String? = trackedEntityType
    fun includeDeleted(): Boolean = includeDeleted
    fun states(): List<State>? = states
    fun followUp(): Boolean? = followUp
    fun eventFilters(): List<TrackedEntityInstanceQueryEventFilter> = eventFilters
    fun lastUpdatedDate(): DateFilterPeriod? = lastUpdatedDate
    fun order(): List<TrackedEntityInstanceQueryScopeOrderByItem> = order
    fun allowOnlineCache(): Boolean = allowOnlineCache
    fun excludedUids(): Set<String>? = excludedUids
    fun uids(): List<String>? = uids

    fun toBuilder(): Builder = TrackedEntityInstanceQueryRepositoryScopeBuilder.from(this)

    class Builder : TrackedEntityInstanceQueryRepositoryScopeBuilder() {
        override fun build(): TrackedEntityInstanceQueryRepositoryScope {
            if (states != null) {
                mode(RepositoryMode.OFFLINE_ONLY)
            }
            return super.build()
        }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
            .filter(emptyList())
            .dataValue(emptyList())
            .orgUnits(emptyList())
            .eventFilters(emptyList())
            .order(emptyList())
            .mode(RepositoryMode.OFFLINE_ONLY)
            .includeDeleted(false)
            .allowOnlineCache(false)

        @JvmStatic
        fun empty(): TrackedEntityInstanceQueryRepositoryScope = builder().build()
    }
}
