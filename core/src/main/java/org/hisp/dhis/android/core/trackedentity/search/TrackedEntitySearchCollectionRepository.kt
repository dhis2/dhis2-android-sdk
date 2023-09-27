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

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dagger.Reusable
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.cache.internal.D2Cache
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.helpers.UidsHelper
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUidCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedFilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListCollectionRepository
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilterCollectionRepository
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory
import javax.inject.Inject

@Reusable
@Suppress("TooManyFunctions", "LongParameterList")
class TrackedEntitySearchCollectionRepository @Inject internal constructor(
    private val store: TrackedEntityInstanceStore,
    private val trackerParentCallFactory: TrackerParentCallFactory,
    private val childrenAppenders: MutableMap<String, ChildrenAppender<TrackedEntityInstance>>,
    scope: TrackedEntityInstanceQueryRepositoryScope,
    scopeHelper: TrackedEntityInstanceQueryRepositoryScopeHelper,
    versionManager: DHISVersionManager,
    filtersRepository: TrackedEntityInstanceFilterCollectionRepository,
    workingListRepository: ProgramStageWorkingListCollectionRepository,
    private val onlineCache: D2Cache<TrackedEntityInstanceQueryOnline, TrackedEntityInstanceOnlineResult>,
    private val onlineHelper: TrackedEntityInstanceQueryOnlineHelper,
    private val localQueryHelper: TrackedEntityInstanceLocalQueryHelper,
    private val searchDataFetcherHelper: TrackedEntitySearchDataFetcherHelper,
) : ReadOnlyWithUidCollectionRepository<TrackedEntitySearchItem>,
    TrackedEntitySearchOperators<TrackedEntitySearchCollectionRepository>(
        scope,
        scopeHelper,
        versionManager,
        filtersRepository,
        workingListRepository,
    ) {

    override val connectorFactory:
        ScopedFilterConnectorFactory<
            TrackedEntitySearchCollectionRepository,
            TrackedEntityInstanceQueryRepositoryScope,
            > =
        ScopedFilterConnectorFactory { s: TrackedEntityInstanceQueryRepositoryScope ->
            TrackedEntitySearchCollectionRepository(
                store, trackerParentCallFactory, childrenAppenders,
                s, scopeHelper, versionManager, filtersRepository, workingListRepository, onlineCache,
                onlineHelper, localQueryHelper, searchDataFetcherHelper,
            )
        }

    @Deprecated("Use {@link #getPagingData()} instead}", replaceWith = ReplaceWith("getPagingData()"))
    override fun getPaged(pageSize: Int): LiveData<PagedList<TrackedEntitySearchItem>> {
        val factory: DataSource.Factory<TrackedEntitySearchItem, TrackedEntitySearchItem> =
            object : DataSource.Factory<TrackedEntitySearchItem, TrackedEntitySearchItem>() {
                override fun create(): DataSource<TrackedEntitySearchItem, TrackedEntitySearchItem> {
                    return dataSource
                }
            }
        return LivePagedListBuilder(factory, pageSize).build()
    }

    val dataSource: DataSource<TrackedEntitySearchItem, TrackedEntitySearchItem>
        get() = TrackedEntitySearchDataSource(getDataFetcher())

    val resultDataSource: DataSource<TrackedEntitySearchItem, Result<TrackedEntitySearchItem, D2Error>>
        get() = TrackedEntitySearchDataSourceResult(getDataFetcher())

    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")
    override fun blockingGet(): List<TrackedEntitySearchItem> {
        val dataFetcher = getDataFetcher()
        val searchResult =
            if (scope.mode() == RepositoryMode.OFFLINE_ONLY || scope.mode() == RepositoryMode.OFFLINE_FIRST) {
                dataFetcher.queryAllOffline()
            } else {
                dataFetcher.queryAllOnline()
            }

        return searchResult.map {
            when (it) {
                is Result.Success -> it.value
                is Result.Failure -> throw it.failure
            }
        }
    }

    override fun get(): Single<List<TrackedEntitySearchItem>> {
        return Single.fromCallable { blockingGet() }
    }

    override fun count(): Single<Int> {
        return Single.fromCallable { blockingCount() }
    }

    override fun blockingCount(): Int {
        return blockingGet().size
    }

    override fun isEmpty(): Single<Boolean> {
        return Single.fromCallable { blockingIsEmpty() }
    }

    override fun blockingIsEmpty(): Boolean {
        return blockingCount() == 0
    }

    override fun one(): ReadOnlyObjectRepository<TrackedEntitySearchItem> {
        return objectRepository(
            object : Transformer<List<TrackedEntitySearchItem>, TrackedEntitySearchItem?> {
                override fun transform(o: List<TrackedEntitySearchItem>): TrackedEntitySearchItem? {
                    return o.firstOrNull()
                }
            },
        )
    }

    private fun getDataFetcher(): TrackedEntitySearchDataFetcher {
        return TrackedEntitySearchDataFetcher(
            store,
            trackerParentCallFactory,
            scope,
            childrenAppenders,
            onlineCache,
            onlineHelper,
            localQueryHelper,
            searchDataFetcherHelper,
        )
    }

    override fun uid(uid: String?): ReadOnlyObjectRepository<TrackedEntitySearchItem> {
        return objectRepository(
            object : Transformer<List<TrackedEntitySearchItem>, TrackedEntitySearchItem?> {
                override fun transform(o: List<TrackedEntitySearchItem>): TrackedEntitySearchItem? {
                    return o.find { uid == it.uid() }
                }
            },
        )
    }

    override fun getUids(): Single<List<String>> {
        return Single.fromCallable { blockingGetUids() }
    }

    override fun blockingGetUids(): List<String> {
        return if (scope.mode() == RepositoryMode.OFFLINE_ONLY || scope.mode() == RepositoryMode.OFFLINE_FIRST) {
            getDataFetcher().queryAllOfflineUids()
        } else {
            UidsHelper.getUids(blockingGet()).toList()
        }
    }

    private fun objectRepository(
        transformer: Transformer<List<TrackedEntitySearchItem>, TrackedEntitySearchItem?>,
    ): ReadOnlyObjectRepository<TrackedEntitySearchItem> {
        return object : ReadOnlyObjectRepository<TrackedEntitySearchItem> {
            override fun get(): Single<TrackedEntitySearchItem?> {
                return Single.fromCallable { this.blockingGet() }
            }

            override fun blockingGet(): TrackedEntitySearchItem? {
                val list = this@TrackedEntitySearchCollectionRepository.blockingGet()
                return transformer.transform(list)
            }

            override fun exists(): Single<Boolean> {
                return Single.fromCallable { blockingExists() }
            }

            override fun blockingExists(): Boolean {
                return blockingGet() != null
            }
        }
    }
}
