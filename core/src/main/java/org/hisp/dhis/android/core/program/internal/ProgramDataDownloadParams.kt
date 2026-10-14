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

package org.hisp.dhis.android.core.program.internal

import org.hisp.dhis.android.core.arch.repositories.scope.BaseScope
import org.hisp.dhis.android.core.event.EventFilter
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingList
import org.hisp.dhis.android.core.settings.EnrollmentScope
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import java.util.Date

internal data class ProgramDataDownloadParams(
    val uids: List<String> = emptyList(),
    val orgUnits: List<String> = emptyList(),
    val orgUnitMode: OrganisationUnitMode? = null,
    val program: String? = null,
    val programStatus: EnrollmentScope? = null,
    val programStartDate: Date? = null,
    val programEndDate: Date? = null,
    val trackedEntityType: String? = null,
    val limitByOrgunit: Boolean? = null,
    val limitByProgram: Boolean? = null,
    val limit: Int? = null,
    val overwrite: Boolean = false,
    val trackedEntityInstanceFilters: List<TrackedEntityInstanceFilter>? = null,
    val programStageWorkingLists: List<ProgramStageWorkingList>? = null,
    val eventFilters: List<EventFilter>? = null,
    val downloadFileResources: Boolean = false,
) : BaseScope {

    fun hasProgramOrFilters(): Boolean {
        return program != null ||
            !programStageWorkingLists.isNullOrEmpty() ||
            !trackedEntityInstanceFilters.isNullOrEmpty() ||
            !eventFilters.isNullOrEmpty()
    }

    companion object {
        const val DEFAULT_LIMIT: Int = 500
    }
}
