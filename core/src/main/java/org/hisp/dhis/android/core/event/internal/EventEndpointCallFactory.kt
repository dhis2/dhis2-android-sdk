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
package org.hisp.dhis.android.core.event.internal

import dagger.Reusable
import java.util.concurrent.Callable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery

@Reusable
internal class EventEndpointCallFactory @Inject constructor(
    private val service: EventService,
    private val apiCallExecutor: APICallExecutor
) {

    fun getCall(eventQuery: TrackerAPIQuery): Callable<List<Event>> {
        return Callable {
            val call = service.getEvents(
                eventQuery.orgUnit,
                eventQuery.commonParams.ouMode.name,
                eventQuery.commonParams.program,
                getEventStartDate(eventQuery),
                EventFields.allFields,
                paging = true,
                eventQuery.page,
                eventQuery.pageSize,
                eventQuery.lastUpdatedStr,
                includeDeleted = true,
                getUidStr(eventQuery)
            )
            apiCallExecutor.executePayloadCall(call)
        }
    }

    private fun getUidStr(query: TrackerAPIQuery): String? {
        return if (query.uids.isEmpty()) null else CollectionsHelper.joinCollectionWithSeparator(query.uids, ";")
    }

    private fun getEventStartDate(query: TrackerAPIQuery): String? {
        return when {
            query.commonParams.program != null -> query.commonParams.startDate
            else -> null
        }
    }
}
