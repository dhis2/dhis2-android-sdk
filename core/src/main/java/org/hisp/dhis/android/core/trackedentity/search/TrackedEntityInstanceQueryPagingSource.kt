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

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory

internal class TrackedEntityInstanceQueryPagingSource(
    store: TrackedEntityInstanceStore,
    databaseAdapter: DatabaseAdapter,
    trackerParentCallFactory: TrackerParentCallFactory,
    scope: TrackedEntityInstanceQueryRepositoryScope,
    childrenAppenders: ChildrenAppenderGetter<TrackedEntityInstance>,
    onlineCache: TrackedEntityInstanceOnlineCache,
    onlineHelper: TrackedEntityInstanceQueryOnlineHelper,
    localQueryHelper: TrackedEntityInstanceLocalQueryHelper,
) : PagingSource<TrackedEntityInstance, TrackedEntityInstance>() {

    private val dataFetcher = TrackedEntityInstanceQueryDataFetcher(
        store,
        databaseAdapter,
        trackerParentCallFactory,
        scope,
        childrenAppenders,
        onlineCache,
        onlineHelper,
        localQueryHelper,
    )

    init {
        dataFetcher.refresh()
    }

    override fun getRefreshKey(
        state: PagingState<TrackedEntityInstance, TrackedEntityInstance>,
    ): TrackedEntityInstance? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }

    override suspend fun load(
        params: LoadParams<TrackedEntityInstance>,
    ): LoadResult<TrackedEntityInstance, TrackedEntityInstance> {
        val pages = dataFetcher.loadPages(params.loadSize)

        return pages.firstOrNull { it is Result.Failure }?.let {
            LoadResult.Error((it as Result.Failure).failure)
        } ?: LoadResult.Page(
            data = pages.map { it.getOrThrow() },
            prevKey = pages.firstOrNull()?.getOrThrow(),
            nextKey = pages.getOrNull(params.loadSize - 1)?.getOrThrow(),
        )
    }
}
