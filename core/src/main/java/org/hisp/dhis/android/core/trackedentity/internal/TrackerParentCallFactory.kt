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
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentEndpointCallFactory
import org.hisp.dhis.android.core.enrollment.internal.NewEnrollmentEndpointCallFactory
import org.hisp.dhis.android.core.enrollment.internal.OldEnrollmentEndpointCallFactory
import org.hisp.dhis.android.core.event.internal.EventEndpointCallFactory
import org.hisp.dhis.android.core.event.internal.NewEventEndpointCallFactory
import org.hisp.dhis.android.core.event.internal.OldEventEndpointCallFactory
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.tracker.TrackerPostParentCallHelper
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import retrofit2.Call
import javax.inject.Inject

@Reusable
internal class TrackerParentCallFactory @Inject constructor(
    private val oldTrackedEntityCallFactory: OldTrackedEntityEndpointCallFactory,
    private val newTrackedEntityEndpointCallFactory: NewTrackedEntityEndpointCallFactory,
    private val oldEnrollmentEndpointCallFactory: OldEnrollmentEndpointCallFactory,
    private val newEnrollmentEndpointCallFactory: NewEnrollmentEndpointCallFactory,
    private val oldEventEndpointCallFactory: OldEventEndpointCallFactory,
    private val newEventEndpointCallFactory: NewEventEndpointCallFactory,
    private val trackerParentCallHelper: TrackerPostParentCallHelper
) {

    fun getTrackedEntityCall(): TrackedEntityEndpointCallFactory {
        return if (trackerParentCallHelper.useNewTrackerImporter()) {
            newTrackedEntityEndpointCallFactory
        } else {
            oldTrackedEntityCallFactory
        }
    }

    fun getEnrollmentCall(): EnrollmentEndpointCallFactory {
        return if (trackerParentCallHelper.useNewTrackerImporter()) {
            newEnrollmentEndpointCallFactory
        } else {
            oldEnrollmentEndpointCallFactory
        }
    }

    fun getEventCall(): EventEndpointCallFactory {
        return if (trackerParentCallHelper.useNewTrackerImporter()) {
            newEventEndpointCallFactory
        } else {
            oldEventEndpointCallFactory
        }
    }
}
