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

package org.hisp.dhis.android.core.event.search

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.Pager
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.event.EventObjectRepository
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnline

@Suppress("TooManyFunctions")
internal class EventQueryDataFetcher(
    private val scope: EventQueryRepositoryScope,
    private val offlineAdapter: EventCollectionRepositoryAdapter,
    private val trackerCallFatory: TrackerParentCallFactory,
    private val onlineAdapter: EventQueryOnlineAdapter,
) {
    suspend fun get(): List<Event> {
        return if (isOnline(scope)) {
            trackerCallFatory.getEventCall().getQueryCall(getOnlineQuery(scope)).items
        } else {
            getOfflineRepository().getInternal()
        }
    }

    suspend fun getUids(): List<String> {
        return if (isOnline(scope)) {
            trackerCallFatory.getEventCall().getQueryUids(getOnlineQuery(scope))
        } else {
            getOfflineRepository().getUidsInternal()
        }
    }

    suspend fun count(): Int {
        return if (isOnline(scope)) {
            getUids().size
        } else {
            getOfflineRepository().countInternal()
        }
    }

    suspend fun isEmpty(): Boolean {
        return if (isOnline(scope)) {
            count() > 0
        } else {
            getOfflineRepository().isEmptyProtected()
        }
    }

    /**
     * Offline only functions
     */

    fun getPaged(pageSize: Int): LiveData<PagedList<Event>> {
        return getOfflineRepository().getPaged(pageSize)
    }

    fun getPagingData(pageSize: Int): Flow<PagingData<Event>> {
        return getOfflineRepository().getPagingData(pageSize)
    }

    fun getPager(pageSize: Int): Pager<Int, Event> {
        return getOfflineRepository().getPager(pageSize)
    }

    val dataSource: DataSource<Int, Event>
        get() = getOfflineRepository().dataSource

    fun one(): ReadOnlyObjectRepository<Event> {
        return getOfflineRepository().one()
    }

    fun uid(uid: String?): EventObjectRepository {
        return getOfflineRepository().uid(uid)
    }

    private fun isOnline(scope: EventQueryRepositoryScope): Boolean {
        return scope.mode() == RepositoryMode.ONLINE_FIRST || scope.mode() == RepositoryMode.ONLINE_ONLY
    }

    private fun getOfflineRepository(): EventCollectionRepository {
        return offlineAdapter.getCollectionRepository(scope)
    }

    private fun getOnlineQuery(scope: EventQueryRepositoryScope): TrackedEntityInstanceQueryOnline {
        return onlineAdapter.scopeToOnlineQuery(scope)
    }
}
