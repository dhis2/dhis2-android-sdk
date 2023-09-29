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

import org.hisp.dhis.android.core.arch.cache.internal.D2Cache
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.program.trackerheaderengine.internal.TrackerHeaderEngine
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory

internal class TrackedEntitySearchDataFetcher(
    store: TrackedEntityInstanceStore,
    trackerParentCallFactory: TrackerParentCallFactory,
    scope: TrackedEntityInstanceQueryRepositoryScope,
    childrenAppenders: Map<String, ChildrenAppender<TrackedEntityInstance>>,
    onlineCache: D2Cache<TrackedEntityInstanceQueryOnline, TrackedEntityInstanceOnlineResult>,
    onlineHelper: TrackedEntityInstanceQueryOnlineHelper,
    localQueryHelper: TrackedEntityInstanceLocalQueryHelper,
    private val trackerHeaderEngine: TrackerHeaderEngine,
) {

    private val instanceFetcher = TrackedEntityInstanceQueryDataFetcher(
        store,
        trackerParentCallFactory,
        scope,
        childrenAppenders,
        onlineCache,
        onlineHelper,
        localQueryHelper,
    )

    private val sampleHeaderExpression =
        "d2:concatenate(A{w75KJ2mc4zz}, ' ', A{zDhUuAYrxNC}, ', ', d2:substring(A{cejWyOfXge6}, 0, 1))"

    fun refresh() {
        instanceFetcher.refresh()
    }

    fun loadPages(requestedLoadSize: Int): List<Result<TrackedEntitySearchItem, D2Error>> {
        return transform(instanceFetcher.loadPages(requestedLoadSize))
    }

    fun queryAllOffline(): List<Result<TrackedEntitySearchItem, D2Error>> {
        return transform(instanceFetcher.queryAllOffline())
    }

    fun queryAllOfflineUids(): List<String> {
        return instanceFetcher.queryAllOfflineUids()
    }

    fun queryAllOnline(): List<Result<TrackedEntitySearchItem, D2Error>> {
        return transform(instanceFetcher.queryAllOnline())
    }

    private fun transform(
        list: List<Result<TrackedEntityInstance, D2Error>>,
    ): List<Result<TrackedEntitySearchItem, D2Error>> {
        return list.map { item ->
            item.map { instance ->
                val searchItem = TrackedEntitySearchItemHelper.from(instance)
                appendHeader(searchItem)
            }
        }
    }

    private fun appendHeader(item: TrackedEntitySearchItem): TrackedEntitySearchItem {
        val header = trackerHeaderEngine.getTrackedEntityHeader(
            expression = sampleHeaderExpression,
            attributeValues = item.trackedEntityAttributeValues ?: emptyList(),
        )

        return item.copy(header = header)
    }
}