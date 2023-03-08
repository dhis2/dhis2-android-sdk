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

import java.util.*
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode

data class TrackedEntityInstanceQueryOnline(
    val page: Int,
    val pageSize: Int,
    val paging: Boolean,
    val orgUnits: List<String>,
    val orgUnitMode: OrganisationUnitMode? = null,
    val program: String? = null,
    val programStage: String? = null,
    val query: String? = null,
    val attribute: List<RepositoryScopeFilterItem> = emptyList(),
    val filter: List<RepositoryScopeFilterItem> = emptyList(),
    val dataValue: List<RepositoryScopeFilterItem> = emptyList(),
    val programStartDate: Date? = null,
    val programEndDate: Date? = null,
    val enrollmentStatus: EnrollmentStatus? = null,
    val incidentStartDate: Date? = null,
    val incidentEndDate: Date? = null,
    val followUp: Boolean? = null,
    val eventStatus: EventStatus? = null,
    val eventStartDate: Date? = null,
    val eventEndDate: Date? = null,
    val dueStartDate: Date? = null,
    val dueEndDate: Date? = null,
    val trackedEntityType: String? = null,
    val includeDeleted: Boolean,
    val assignedUserMode: AssignedUserMode? = null,
    val uids: List<String>? = null,
    val lastUpdatedStartDate: Date? = null,
    val lastUpdatedEndDate: Date? = null,
    val order: String? = null,
)
