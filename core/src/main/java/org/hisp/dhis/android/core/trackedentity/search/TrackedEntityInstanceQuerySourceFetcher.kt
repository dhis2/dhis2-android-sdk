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

import org.hisp.dhis.android.core.arch.cache.internal.D2Cache
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderExecutor
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenSelection
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFields
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import java.util.*

internal class TrackedEntityInstanceQuerySourceFetcher constructor(
    private val store: TrackedEntityInstanceStore,
    private val onlineCallFactory: TrackedEntityInstanceQueryCallFactory,
    private val scope: TrackedEntityInstanceQueryRepositoryScope,
    private val childrenAppenders: Map<String, ChildrenAppender<TrackedEntityInstance>>,
    private val onlineCache: D2Cache<TrackedEntityInstanceQueryOnline, List<TrackedEntityInstance>?>,
    onlineHelper: TrackedEntityInstanceQueryOnlineHelper,
    private val localQueryHelper: TrackedEntityInstanceLocalQueryHelper
) {
    private val baseOnlineQueries: List<TrackedEntityInstanceQueryOnline> = onlineHelper.fromScope(scope)
    private val onlineQueryStatusMap: MutableMap<TrackedEntityInstanceQueryOnline, OnlineQueryStatus> = HashMap()

    private var returnedUidsOffline: MutableSet<String> = HashSet()
    private var returnedUidsOnline: MutableSet<String> = HashSet()
    private var isExhaustedOffline = false

    init {
        for (onlineQuery in baseOnlineQueries) {
            onlineQueryStatusMap[onlineQuery] = OnlineQueryStatus()
        }
    }

    fun refresh() {
        returnedUidsOffline = HashSet()
        returnedUidsOnline = HashSet()
    }

    fun loadTeis(requestedLoadSize: Int): List<TrackedEntityInstance> {
        val result: MutableList<TrackedEntityInstance> = ArrayList()
        if (scope.mode() == RepositoryMode.OFFLINE_ONLY || scope.mode() == RepositoryMode.OFFLINE_FIRST) {
            if (!isExhaustedOffline) {
                val instances = queryOffline(requestedLoadSize)
                result.addAll(instances)
                isExhaustedOffline = instances.size < requestedLoadSize
            }
            if (result.size < requestedLoadSize && scope.mode() == RepositoryMode.OFFLINE_FIRST) {
                val onlineInstances = queryOnlineRecursive(requestedLoadSize)
                result.addAll(onlineInstances)
            }
        } else {
            val instances = queryOnlineRecursive(requestedLoadSize)
            result.addAll(instances)
            if (result.size < requestedLoadSize && scope.mode() == RepositoryMode.ONLINE_FIRST) {
                val onlineInstances = queryOffline(requestedLoadSize)
                result.addAll(onlineInstances)
            }
        }
        return result
    }

    private fun queryOffline(requestedLoadSize: Int): List<TrackedEntityInstance> {
        val sqlQuery = localQueryHelper.getSqlQuery(
            scope, returnedUidsOffline,
            requestedLoadSize
        )
        val instances = store.selectRawQuery(sqlQuery)
        returnedUidsOffline.addAll(instances.map { it.uid() })

        return appendAttributes(instances)
    }

    private fun queryOnlineRecursive(requestLoadSize: Int): List<TrackedEntityInstance> {
        val result: MutableList<TrackedEntityInstance> = ArrayList()

        do {
            for (baseOnlineQuery in baseOnlineQueries) {
                val status = onlineQueryStatusMap[baseOnlineQuery]
                if (status!!.isExhausted) {
                    continue
                }
                val page = status.requestedItems / requestLoadSize + 1
                val onlineQuery = baseOnlineQuery.toBuilder()
                    .page(page)
                    .pageSize(requestLoadSize)
                    .paging(true)
                    .build()
                val queryInstances = queryOnline(onlineQuery)

                // If first page, the requestedSize is three times the original. Increment in three.
                status.requestedItems += requestLoadSize
                status.isExhausted = queryInstances.size < requestLoadSize

                val newInstances = queryInstances.filter {
                    !returnedUidsOffline.contains(it.uid()) && !returnedUidsOnline.contains(it.uid())
                }
                result.addAll(newInstances)

                returnedUidsOnline.addAll(queryInstances.map { it.uid() })
            }
        } while (result.size < requestLoadSize && !areAllOnlineQueriesExhausted())

        return result
    }

    private fun queryOnline(onlineQuery: TrackedEntityInstanceQueryOnline): List<TrackedEntityInstance> {
        var queryInstances = if (scope.allowOnlineCache()) onlineCache[onlineQuery] else null

        if (queryInstances == null) {
            queryInstances = onlineCallFactory.getCall(onlineQuery).call()
            onlineCache[onlineQuery] = queryInstances
        }

        return queryInstances!!
    }

    private fun appendAttributes(withoutChildren: List<TrackedEntityInstance>): List<TrackedEntityInstance> {
        return ChildrenAppenderExecutor.appendInObjectCollection(
            withoutChildren, childrenAppenders,
            ChildrenSelection(
                setOf(
                    TrackedEntityInstanceFields.TRACKED_ENTITY_ATTRIBUTE_VALUES
                )
            )
        )
    }

    private fun areAllOnlineQueriesExhausted(): Boolean {
        for (status in onlineQueryStatusMap.values) {
            if (!status.isExhausted) {
                return false
            }
        }
        return true
    }
}

internal data class OnlineQueryStatus(
    var requestedItems: Int = 0,
    var isExhausted: Boolean = false
)
