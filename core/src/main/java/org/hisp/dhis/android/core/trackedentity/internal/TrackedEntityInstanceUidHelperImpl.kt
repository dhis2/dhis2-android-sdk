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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor

@Reusable
internal class TrackedEntityInstanceUidHelperImpl @Inject constructor(
    private val organisationUnitStore: IdentifiableObjectStore<OrganisationUnit>
) : TrackedEntityInstanceUidHelper {

    override fun hasMissingOrganisationUnitUids(
        trackedEntityInstances: Collection<TrackedEntityInstance>
    ): Boolean {
        return getMissingOrganisationUnitUids(trackedEntityInstances).isNotEmpty()
    }

    override fun getMissingOrganisationUnitUids(
        trackedEntityInstances: Collection<TrackedEntityInstance>
    ): Set<String> {
        val uids = trackedEntityInstances.flatMap {
            val enrollments = TrackedEntityInstanceInternalAccessor.accessEnrollments(it)
            getEnrollmentsUids(enrollments) + it.organisationUnit()
        }.filterNotNull().toSet()

        return uids - organisationUnitStore.selectUids()
    }

    private fun getEnrollmentsUids(enrollments: List<Enrollment>?): Set<String> {
        return enrollments?.flatMap {
            val events = EnrollmentInternalAccessor.accessEvents(it)
            getEventsUids(events) + it.organisationUnit()
        }?.filterNotNull()?.toSet() ?: emptySet()
    }

    private fun getEventsUids(events: MutableList<Event>?): Set<String> {
        return events?.mapNotNull { it.organisationUnit() }?.toSet() ?: emptySet()
    }
}
