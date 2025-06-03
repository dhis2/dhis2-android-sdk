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

import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderExecutor
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenSelection
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository.Companion.TRACKED_ENTITY_ATTRIBUTE_VALUES
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory

@Suppress("TooManyFunctions")
internal class TrackedEntityInstanceQueryDataFetcher(
    private val store: TrackedEntityInstanceStore,
    private val databaseAdapter: DatabaseAdapter,
    private val trackerParentCallFactory: TrackerParentCallFactory,
    private val scope: TrackedEntityInstanceQueryRepositoryScope,
    private val childrenAppenders: ChildrenAppenderGetter<TrackedEntityInstance>,
    private val onlineCache: TrackedEntityInstanceOnlineCache,
    onlineHelper: TrackedEntityInstanceQueryOnlineHelper,
    private val localQueryHelper: TrackedEntityInstanceLocalQueryHelper,
) {
    private val baseOnlineQueries: List<TrackedEntityInstanceQueryOnline> = onlineHelper.fromScope(scope)
    private val onlineQueryStatusMap: MutableMap<TrackedEntityInstanceQueryOnline, OnlineQueryStatus> = HashMap()

    private var returnedUidsOffline: MutableSet<String> = scope.excludedUids() ?: HashSet()
    private var returnedUidsOnline: MutableSet<String> = HashSet()
    private var returnedErrorCodes: MutableSet<D2ErrorCode> = HashSet()
    private var isExhaustedOffline = false

    init {
        for (onlineQuery in baseOnlineQueries) {
            onlineQueryStatusMap[onlineQuery] = OnlineQueryStatus()
        }
    }

    fun refresh() {
        returnedUidsOffline = scope.excludedUids() ?: HashSet()
        returnedUidsOnline = HashSet()
        returnedErrorCodes = HashSet()
    }

    suspend fun loadPages(requestedLoadSize: Int): List<Result<TrackedEntityInstance, D2Error>> {
        val result: MutableList<Result<TrackedEntityInstance, D2Error>> = ArrayList()

        if (scope.mode() == RepositoryMode.OFFLINE_ONLY || scope.mode() == RepositoryMode.OFFLINE_FIRST) {
            if (!isExhaustedOffline) {
                val instances = queryOffline(requestedLoadSize)
                result.addAll(instances)
                isExhaustedOffline = instances.size < requestedLoadSize
            }
            if (result.size < requestedLoadSize && scope.mode() == RepositoryMode.OFFLINE_FIRST) {
                val onlineInstances = queryOnline(requestedLoadSize)
                result.addAll(onlineInstances)
            }
        } else {
            val instances = queryOnline(requestedLoadSize)
            result.addAll(instances)
            if (result.size < requestedLoadSize && scope.mode() == RepositoryMode.ONLINE_FIRST) {
                val onlineInstances = queryOffline(requestedLoadSize)
                result.addAll(onlineInstances)
            }
        }
        return result
    }

    suspend fun queryAllOffline(): List<Result<TrackedEntityInstance, D2Error>> {
        return queryOffline(-1)
    }

    suspend fun queryAllOfflineUids(): List<String> {
        val sqlQuery = localQueryHelper.getUidsWhereClause(scope, scope.excludedUids(), -1)
        return store.selectUidsWhere(sqlQuery)
    }

    fun queryAllOnline(): List<Result<TrackedEntityInstance, D2Error>> {
        return queryOnline(-1)
    }

    private suspend fun queryOffline(requestedLoadSize: Int): List<Result<TrackedEntityInstance, D2Error>> {
        val sqlQuery = localQueryHelper.getSqlQuery(
            scope,
            returnedUidsOffline,
            requestedLoadSize,
        )
        val instances = store.selectRawQuery(sqlQuery)
        returnedUidsOffline.addAll(instances.map { it.uid() })

        return appendAttributes(instances).map {
            Result.Success(it)
        }
    }

    @Suppress("ComplexCondition")
    private fun queryOnline(requestLoadSize: Int): List<Result<TrackedEntityInstance, D2Error>> {
        val result: MutableList<Result<TrackedEntityInstance, D2Error>> = ArrayList()

        do {
            for (baseOnlineQuery in baseOnlineQueries) {
                val queryResult = getOnlineQueryResults(baseOnlineQuery, requestLoadSize)
                result.addAll(queryResult)
            }
        } while (
            result.all { it.succeeded } &&
            requestLoadSize > 0 &&
            result.size < requestLoadSize &&
            !areAllOnlineQueriesExhausted()
        )

        return result
    }

    private fun getOnlineQueryResults(
        baseOnlineQuery: TrackedEntityInstanceQueryOnline,
        requestLoadSize: Int,
    ): List<Result<TrackedEntityInstance, D2Error>> {
        val status = onlineQueryStatusMap[baseOnlineQuery]!!
        if (status.isExhausted) {
            return emptyList()
        }

        val page = (status.requestedItems / requestLoadSize) + 1
        val onlineQuery = if (requestLoadSize > 0) {
            baseOnlineQuery.copy(
                page = page,
                pageSize = requestLoadSize,
                paging = true,
            )
        } else {
            baseOnlineQuery.copy(
                paging = false,
            )
        }
        val queryInstances = queryOnline(onlineQuery)

        status.requestedItems += requestLoadSize
        status.isExhausted = queryInstances.exhausted

        return queryInstances.items
            .filter {
                when (it) {
                    is Result.Success ->
                        !returnedUidsOffline.contains(it.value.uid()) && !returnedUidsOnline.contains(it.value.uid())

                    is Result.Failure ->
                        !returnedErrorCodes.contains(it.failure.errorCode())
                }
            }.onEach {
                when (it) {
                    is Result.Success -> returnedUidsOnline.add(it.value.uid())
                    is Result.Failure -> {
                        status.isExhausted = true
                        returnedErrorCodes.add(it.failure.errorCode())
                    }
                }
            }
    }

    private fun queryOnline(
        onlineQuery: TrackedEntityInstanceQueryOnline,
    ): TrackedEntityInstanceOnlineResult {
        return try {
            val cachedInstances = if (scope.allowOnlineCache()) onlineCache[onlineQuery] else null

            cachedInstances ?: runBlocking {
                trackerParentCallFactory.getTrackedEntityCall()
                    .getQueryCall(onlineQuery)
            }
                .let { result ->
                    TrackedEntityInstanceOnlineResult(
                        items = result.trackedEntities.map { Result.Success(it) },
                        exhausted = result.exhausted,
                    )
                }
                .also { onlineCache[onlineQuery] = it }
        } catch (e: D2Error) {
            TrackedEntityInstanceOnlineResult(
                items = listOf(Result.Failure(e)),
                exhausted = true,
            )
        }
    }

    private fun appendAttributes(withoutChildren: List<TrackedEntityInstance>): List<TrackedEntityInstance> {
        return ChildrenAppenderExecutor.appendInObjectCollection(
            withoutChildren,
            databaseAdapter,
            childrenAppenders,
            ChildrenSelection(
                setOf(
                    TRACKED_ENTITY_ATTRIBUTE_VALUES,
                ),
            ),
        )
    }

    private fun areAllOnlineQueriesExhausted(): Boolean {
        return onlineQueryStatusMap.values.all { it.isExhausted }
    }
}

private data class OnlineQueryStatus(
    var requestedItems: Int = 0,
    var isExhausted: Boolean = false,
)
