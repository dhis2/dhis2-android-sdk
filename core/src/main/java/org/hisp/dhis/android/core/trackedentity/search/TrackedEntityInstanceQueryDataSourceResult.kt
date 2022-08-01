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
package org.hisp.dhis.android.core.trackedentity.search

import androidx.paging.PageKeyedDataSource
import org.hisp.dhis.android.core.arch.cache.internal.D2Cache
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore

internal class TrackedEntityInstanceQueryDataSourceResult constructor(
    store: TrackedEntityInstanceStore,
    onlineCallFactory: TrackedEntityInstanceQueryCallFactory,
    scope: TrackedEntityInstanceQueryRepositoryScope,
    childrenAppenders: Map<String, ChildrenAppender<TrackedEntityInstance>>,
    onlineCache: D2Cache<TrackedEntityInstanceQueryOnline, List<Result<TrackedEntityInstance, D2Error>>>,
    onlineHelper: TrackedEntityInstanceQueryOnlineHelper,
    localQueryHelper: TrackedEntityInstanceLocalQueryHelper
) : PageKeyedDataSource<
    TrackedEntityInstance, Result<@JvmSuppressWildcards TrackedEntityInstance, @JvmSuppressWildcards D2Error>>() {

    private val dataFetcher = TrackedEntityInstanceQueryDataFetcher(
        store, onlineCallFactory, scope, childrenAppenders,
        onlineCache, onlineHelper, localQueryHelper
    )

    override fun loadInitial(
        params: LoadInitialParams<TrackedEntityInstance>,
        callback: LoadInitialCallback<TrackedEntityInstance, Result<TrackedEntityInstance, D2Error>>
    ) {
        dataFetcher.refresh()
        val result = loadPages(params.requestedLoadSize)
        callback.onResult(result, null, getKey(result))
    }

    override fun loadAfter(
        params: LoadParams<TrackedEntityInstance>,
        callback: LoadCallback<TrackedEntityInstance, Result<TrackedEntityInstance, D2Error>>
    ) {
        val result = loadPages(params.requestedLoadSize)
        callback.onResult(result, getKey(result))
    }

    override fun loadBefore(
        params: LoadParams<TrackedEntityInstance>,
        callback: LoadCallback<TrackedEntityInstance, Result<TrackedEntityInstance, D2Error>>
    ) {
        // do nothing
    }

    private fun getKey(result: List<Result<TrackedEntityInstance, D2Error>>): TrackedEntityInstance? {
        return result.filterIsInstance<Result.Success<TrackedEntityInstance, D2Error>>().lastOrNull()?.value
    }

    private fun loadPages(requestedLoadSize: Int): List<Result<TrackedEntityInstance, D2Error>> {
        return dataFetcher.loadPages(requestedLoadSize)
    }
}
