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
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryScopeOrderByItem
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import retrofit2.Call

@Reusable
internal class TrackedEntityInstancesEndpointCallFactory @Inject constructor(
    private val trackedEntityInstanceService: TrackedEntityInstanceService
) {

    fun getCollectionCall(query: TrackerAPIQuery): Single<Payload<TrackedEntityInstance>> {
        return trackedEntityInstanceService.getTrackedEntityInstances(
            getUidStr(query),
            query.orgUnit,
            query.commonParams.ouMode.name,
            query.commonParams.program,
            getProgramStatus(query),
            getProgramStartDate(query),
            TrackedEntityInstanceFields.allFields,
            TrackedEntityInstanceQueryScopeOrderByItem.DEFAULT_TRACKER_ORDER.toAPIString(),
            paging = true,
            query.page,
            query.pageSize,
            query.lastUpdatedStr,
            includeAllAttributes = true,
            includeDeleted = true
        )
    }

    private fun getUidStr(query: TrackerAPIQuery): String? {
        return if (query.uids.isEmpty()) null else CollectionsHelper.joinCollectionWithSeparator(query.uids, ";")
    }

    private fun getProgramStatus(query: TrackerAPIQuery): String? {
        return when {
            query.commonParams.program != null -> query.programStatus?.toString()
            else -> null
        }
    }

    private fun getProgramStartDate(query: TrackerAPIQuery): String? {
        return when {
            query.commonParams.program != null -> query.commonParams.startDate
            else -> null
        }
    }

    fun getEntityCall(uid: String, query: TrackerAPIQuery): Call<TrackedEntityInstance> {
        return trackedEntityInstanceService.getSingleTrackedEntityInstance(
            uid,
            query.commonParams.ouMode.name,
            query.commonParams.program,
            getProgramStatus(query),
            getProgramStartDate(query),
            TrackedEntityInstanceFields.allFields,
            includeAllAttributes = true,
            includeDeleted = true
        )
    }
}
