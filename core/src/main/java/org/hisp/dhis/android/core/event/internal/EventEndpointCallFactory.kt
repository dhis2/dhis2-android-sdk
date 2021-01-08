/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.event.internal

import dagger.Reusable
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.event.Event
import java.util.concurrent.Callable
import javax.inject.Inject

@Reusable
internal class EventEndpointCallFactory @Inject constructor(
    private val service: EventService,
    private val apiCallExecutor: APICallExecutor,
    private val lastUpdatedManager: EventLastUpdatedManager
) {

    fun getCall(eventQuery: EventQuery): Callable<List<Event>> {
        return Callable {
            val call = service.getEvents(
                eventQuery.orgUnit(), eventQuery.commonParams().ouMode.name,
                eventQuery.commonParams().program, EventFields.allFields, true,
                eventQuery.page(), eventQuery.pageSize(), getLastUpdated(eventQuery), true,
                getUidStr(eventQuery)
            )
            apiCallExecutor.executePayloadCall(call)
        }
    }

    private fun getLastUpdated(query: EventQuery): String? {
        val lastUpdated = lastUpdatedManager.getLastUpdated(query.commonParams())
        return if (lastUpdated == null) null else BaseIdentifiableObject.dateToDateStr(lastUpdated)
    }

    private fun getUidStr(query: EventQuery): String? {
        return if (query.uids().isEmpty()) null else CollectionsHelper.joinCollectionWithSeparator(query.uids(), ";")
    }
}