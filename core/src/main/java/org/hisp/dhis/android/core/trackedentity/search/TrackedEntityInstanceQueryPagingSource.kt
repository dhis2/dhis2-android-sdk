/*
 *  Copyright (c) 2004-2021, University of Oslo
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

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.hisp.dhis.android.core.arch.cache.internal.D2Cache
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore

internal class TrackedEntityInstanceQueryPagingSourceFetcher constructor(
    store: TrackedEntityInstanceStore,
    onlineCallFactory: TrackedEntityInstanceQueryCallFactory,
    scope: TrackedEntityInstanceQueryRepositoryScope,
    childrenAppenders: Map<String, ChildrenAppender<TrackedEntityInstance>>,
    onlineCache: D2Cache<TrackedEntityInstanceQueryOnline, List<TrackedEntityInstance>?>,
    onlineHelper: TrackedEntityInstanceQueryOnlineHelper,
    localQueryHelper: TrackedEntityInstanceLocalQueryHelper
): PagingSource<TrackedEntityInstance, TrackedEntityInstance>() {

    private val fetcher = TrackedEntityInstanceQuerySourceFetcher(store, onlineCallFactory, scope, childrenAppenders,
        onlineCache, onlineHelper, localQueryHelper)

    override fun getRefreshKey(
        state: PagingState<TrackedEntityInstance, TrackedEntityInstance>
    ): TrackedEntityInstance? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)
        }
    }

    override suspend fun load(
        params: LoadParams<TrackedEntityInstance>
    ): LoadResult<TrackedEntityInstance, TrackedEntityInstance> {
        if (params is LoadParams.Refresh) {
            fetcher.refresh()
        }

        return try {
            val trackedEntityInstances = fetcher.loadTeis(params.loadSize)
            LoadResult.Page(trackedEntityInstances, null, null)
        } catch (d2Error: D2Error) {
            LoadResult.Error(d2Error)
        } catch (e: Exception) {
            LoadResult.Page(emptyList(), null, null)
        }
    }
}