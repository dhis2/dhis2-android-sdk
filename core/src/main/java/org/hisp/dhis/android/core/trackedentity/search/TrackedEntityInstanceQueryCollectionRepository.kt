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
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUidCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EqFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedFilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListCollectionRepository
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilterCollectionRepository
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory
import org.koin.core.annotation.Singleton
import java.util.Date

@Singleton
@Suppress("TooManyFunctions", "LongParameterList")
class TrackedEntityInstanceQueryCollectionRepository internal constructor(
    private val store: TrackedEntityInstanceStore,
    private val trackerParentCallFactory: TrackerParentCallFactory,
    private val databaseAdapter: DatabaseAdapter,
    scope: TrackedEntityInstanceQueryRepositoryScope,
    scopeHelper: TrackedEntityInstanceQueryRepositoryScopeHelper,
    versionManager: DHISVersionManager,
    filtersRepository: TrackedEntityInstanceFilterCollectionRepository,
    workingListRepository: ProgramStageWorkingListCollectionRepository,
    private val onlineCache: TrackedEntityInstanceOnlineCache,
    private val onlineHelper: TrackedEntityInstanceQueryOnlineHelper,
    private val localQueryHelper: TrackedEntityInstanceLocalQueryHelper,
) : ReadOnlyWithUidCollectionRepository<TrackedEntityInstance>,
    TrackedEntitySearchOperators<TrackedEntityInstanceQueryCollectionRepository>(
        scope,
        scopeHelper,
        versionManager,
        filtersRepository,
        workingListRepository,
    ) {

    override val connectorFactory:
        ScopedFilterConnectorFactory<
            TrackedEntityInstanceQueryCollectionRepository,
            TrackedEntityInstanceQueryRepositoryScope,
            > =
        ScopedFilterConnectorFactory { s: TrackedEntityInstanceQueryRepositoryScope ->
            TrackedEntityInstanceQueryCollectionRepository(
                store, trackerParentCallFactory, databaseAdapter,
                s, scopeHelper, versionManager, filtersRepository, workingListRepository, onlineCache,
                onlineHelper, localQueryHelper,
            )
        }

    @Deprecated("use {@link #byProgramDate()} instead.")
    fun byProgramStartDate(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Date> {
        return connectorFactory.eqConnector { byProgramDate().afterOrEqual(it!!).scope }
    }

    @Deprecated("use {@link #byProgramDate()} instead.")
    fun byProgramEndDate(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Date> {
        return connectorFactory.eqConnector { byProgramDate().beforeOrEqual(it!!).scope }
    }

    @Deprecated("use {@link #byEventDate()} instead.")
    fun byEventStartDate(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Date> {
        return connectorFactory.eqConnector {
            byEventDate().afterOrEqual(it!!).scope
        }
    }

    @Deprecated("use {@link #byEventDate()} instead.")
    fun byEventEndDate(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Date> {
        return connectorFactory.eqConnector {
            byEventDate().beforeOrEqual(it!!).scope
        }
    }

    @Deprecated("use {@link #byEnrollmentStatus()} instead.")
    fun byProgramStatus(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, EnrollmentStatus> {
        return connectorFactory.eqConnector { status: EnrollmentStatus? ->
            scope.toBuilder().enrollmentStatus(listOf(status)).build()
        }
    }

    @Deprecated("Use {@link #getPagingData()} instead}", replaceWith = ReplaceWith("getPagingData()"))
    override fun getPaged(pageSize: Int): LiveData<PagedList<TrackedEntityInstance>> {
        val factory: DataSource.Factory<TrackedEntityInstance, TrackedEntityInstance> =
            object : DataSource.Factory<TrackedEntityInstance, TrackedEntityInstance>() {
                override fun create(): DataSource<TrackedEntityInstance, TrackedEntityInstance> {
                    return dataSource
                }
            }
        return LivePagedListBuilder(factory, pageSize).build()
    }

    override fun getPagingData(pageSize: Int): Flow<PagingData<TrackedEntityInstance>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize),
        ) {
            pagingSource
        }.flow
    }

    val dataSource: DataSource<TrackedEntityInstance, TrackedEntityInstance>
        get() = TrackedEntityInstanceQueryDataSource(getDataFetcher())

    val pagingSource: PagingSource<TrackedEntityInstance, TrackedEntityInstance>
        get() = TrackedEntityInstanceQueryPagingSource(
            store,
            databaseAdapter,
            trackerParentCallFactory,
            scope,
            childrenAppenders,
            onlineCache,
            onlineHelper,
            localQueryHelper,
        )

    @Deprecated("use getPagingdata")
    val resultDataSource: DataSource<TrackedEntityInstance, Result<TrackedEntityInstance, D2Error>>
        get() = TrackedEntityInstanceQueryDataSourceResult(getDataFetcher())

    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")
    override fun blockingGet(): List<TrackedEntityInstance> {
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

    override fun get(): Single<List<TrackedEntityInstance>> {
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

    override fun one(): ReadOnlyObjectRepository<TrackedEntityInstance> {
        return objectRepository(
            object : Transformer<List<TrackedEntityInstance>, TrackedEntityInstance?> {
                override fun transform(o: List<TrackedEntityInstance>): TrackedEntityInstance? {
                    return o.firstOrNull()
                }
            },
        )
    }

    private fun getDataFetcher(): TrackedEntityInstanceQueryDataFetcher {
        return TrackedEntityInstanceQueryDataFetcher(
            store,
            databaseAdapter,
            trackerParentCallFactory,
            scope,
            childrenAppenders,
            onlineCache,
            onlineHelper,
            localQueryHelper,
        )
    }

    override fun uid(uid: String?): ReadOnlyObjectRepository<TrackedEntityInstance> {
        return objectRepository(
            object : Transformer<List<TrackedEntityInstance>, TrackedEntityInstance?> {
                override fun transform(o: List<TrackedEntityInstance>): TrackedEntityInstance? {
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
            getUids(blockingGet()).toList()
        }
    }

    private fun objectRepository(
        transformer: Transformer<List<TrackedEntityInstance>, TrackedEntityInstance?>,
    ): ReadOnlyObjectRepository<TrackedEntityInstance> {
        return object : ReadOnlyObjectRepository<TrackedEntityInstance> {
            override fun get(): Single<TrackedEntityInstance?> {
                return Single.fromCallable { this.blockingGet() }
            }

            override fun blockingGet(): TrackedEntityInstance? {
                val list = this@TrackedEntityInstanceQueryCollectionRepository.blockingGet()
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

    internal companion object {
        val childrenAppenders = TrackedEntityInstanceCollectionRepository.childrenAppenders
    }
}
