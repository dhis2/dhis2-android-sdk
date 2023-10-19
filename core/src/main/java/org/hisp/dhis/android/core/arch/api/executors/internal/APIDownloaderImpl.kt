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
package org.hisp.dhis.android.core.arch.api.executors.internal

import androidx.annotation.VisibleForTesting
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.resource.internal.Resource
import org.hisp.dhis.android.core.resource.internal.ResourceHandler
import org.koin.core.annotation.Singleton

@Singleton
@VisibleForTesting
@Suppress("TooManyFunctions")
internal class APIDownloaderImpl(private val resourceHandler: ResourceHandler) : APIDownloader {

    override suspend fun <P> downloadPartitioned(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: suspend(Set<String>) -> Payload<P>,
    ): List<P> {
        return downloadPartitionedWithCustomHandling(
            uids,
            pageSize,
            handler,
            pageDownloader,
        ) { it }
    }

    override suspend fun <P, O> downloadPartitioned(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: suspend(Set<String>) -> Payload<O>,
        transform: (O) -> P,
    ): List<P> {
        return downloadPartitionedWithCustomHandling(
            uids,
            pageSize,
            handler,
            pageDownloader,
            transform,
        )
    }

    override suspend fun <P> downloadPartitioned(
        uids: Set<String>,
        pageSize: Int,
        pageDownloader: suspend(Set<String>) -> Payload<P>,
    ): List<P> {
        return downloadPartitionedWithoutHandling(
            uids = uids,
            pageSize = pageSize,
            pageDownloader = pageDownloader,
            transform = { it },
        )
    }

    private suspend fun <P, O> downloadPartitionedWithoutHandling(
        uids: Set<String>,
        pageSize: Int,
        pageDownloader: suspend(Set<String>) -> Payload<O>,
        transform: (O) -> P,
    ): List<P> {
        val partitions = CollectionsHelper.setPartition(uids, pageSize)

        val results = mutableListOf<P>()

        partitions.forEach { partition ->
            val transformedItems = pageDownloader(partition).items().map { transform(it) }
            results.addAll(transformedItems)
        }

        return results
    }

    private suspend fun <P, O> downloadPartitionedWithCustomHandling(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: suspend(Set<String>) -> Payload<O>,
        transform: (O) -> P,
    ): List<P> {
        val oCollection = downloadPartitionedWithoutHandling(
            uids = uids,
            pageSize = pageSize,
            pageDownloader = pageDownloader,
            transform = transform,
        )
        handler.handleMany(oCollection)
        return oCollection
    }

    override suspend fun <K, V> downloadPartitionedMap(
        uids: Set<String>,
        pageSize: Int,
        handler: (Map<K, V>) -> Any,
        pageDownloader: suspend(Set<String>) -> Map<K, V>,
    ): Map<K, V> {
        val partitions = CollectionsHelper.setPartition(uids, pageSize)

        val results = partitions.map { partition ->
            pageDownloader(partition)
        }
        val resultsMap = results.fold(mapOf<K, V>()) { acc, map ->
            acc + map
        }
        handler(resultsMap)
        return resultsMap
    }

    override suspend fun <P, O : CoreObject> downloadLink(
        masterUid: String,
        handler: LinkHandler<P, O>,
        downloader: suspend(String) -> Payload<P>,
        transform: (P) -> O,
    ): List<P> {
        val items = downloader.invoke(masterUid).items()
        handler.handleMany(masterUid, items, transform)

        return items
    }

    override suspend fun <P> downloadWithLastUpdated(
        handler: Handler<P>,
        resourceType: Resource.Type,
        downloader: suspend(String?) -> Payload<P>,
    ): List<P> {
        val items = downloader(
            resourceHandler.getLastUpdated(resourceType),
        ).items()

        handler.handleMany(items)
        resourceHandler.handleResource(resourceType)
        return items
    }

    override fun <P> download(handler: Handler<P>, downloader: Single<Payload<P>>): Single<List<P>> {
        return downloader
            .map { obj: Payload<P> -> obj.items() }
            .doOnSuccess { oCollection: List<P> -> handler.handleMany(oCollection) }
    }

    override fun <P> downloadList(handler: Handler<P>, downloader: Single<List<P>>): Single<List<P>> {
        return downloader
            .doOnSuccess { oCollection: List<P> -> handler.handleMany(oCollection) }
    }

    override suspend fun <P> downloadListAsCoroutine(handler: Handler<P>, downloader: suspend () -> List<P>): List<P> {
        return downloader.invoke()
            .also { handler.handleMany(it) }
    }

    override fun <P> downloadObject(handler: Handler<P>, downloader: Single<P>): Single<P> {
        return downloader
            .doOnSuccess { o: P -> handler.handle(o) }
    }

    override suspend fun <P> downloadObjectAsCoroutine(handler: Handler<P>, downloader: suspend () -> P): P {
        return downloader.invoke()
            .also { handler.handle(it) }
    }

    override suspend fun <P> downloadPagedPayload(
        pageSize: Int,
        downloader: suspend(page: Int, pageSize: Int) -> Payload<P>,
    ): Payload<P> {
        var page = 1

        val itemsList = mutableListOf<P>()

        while (true) {
            val payload = downloader(page++, pageSize)
            val items = payload.items()

            itemsList.addAll(items)

            if (items.size < pageSize) {
                break
            }
        }

        return Payload(itemsList)
    }
}
