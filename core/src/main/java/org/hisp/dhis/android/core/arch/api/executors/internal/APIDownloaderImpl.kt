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
import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.rx2.rxSingle
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.resource.internal.Resource
import org.hisp.dhis.android.core.resource.internal.ResourceHandler
import javax.inject.Inject

@Reusable
@VisibleForTesting
@Suppress("TooManyFunctions")
internal class APIDownloaderImpl @Inject constructor(private val resourceHandler: ResourceHandler) : APIDownloader {

    override fun <P> downloadPartitioned(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: (Set<String>) -> Single<Payload<P>>,
    ): Single<List<P>> {
        return rxSingle {
            downloadPartitionedCoroutines(
                uids,
                pageSize,
                handler,
                pageDownloader,
            ) { it }
        }
    }

    override suspend fun <P> downloadPartitionedCoroutines(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: (Set<String>) -> Single<Payload<P>>,
    ): List<P> {
        return downloadPartitionedWithCustomHandling(
            uids,
            pageSize,
            handler,
            pageDownloader,
        ) { it }
    }

    override fun <P, O> downloadPartitioned(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: (Set<String>) -> Single<Payload<O>>,
        transform: (O) -> P,
    ): Single<List<P>> {
        return rxSingle {
            downloadPartitionedCoroutines(
                uids,
                pageSize,
                handler,
                pageDownloader,
                transform,
            )
        }
    }

    override suspend fun <P, O> downloadPartitionedCoroutines(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: (Set<String>) -> Single<Payload<O>>,
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

    override fun <P> downloadPartitionedCoroutinesJavaCompatible(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: (Set<String>) -> Single<Payload<P>>
    ): List<P> {
        return runBlocking {   downloadPartitionedCoroutines(
            uids,
            pageSize,
            handler,
            pageDownloader,
        ) { it } }

    }

    override fun <P> downloadPartitioned(
        uids: Set<String>,
        pageSize: Int,
        pageDownloader: (Set<String>) -> Single<Payload<P>>,
    ): Single<List<P>> {
        return downloadPartitionedWithoutHandling(
            uids = uids,
            pageSize = pageSize,
            pageDownloader = pageDownloader,
            transform = { it },
        )
    }

    private fun <P, O> downloadPartitionedWithoutHandling(
        uids: Set<String>,
        pageSize: Int,
        pageDownloader: (Set<String>) -> Single<Payload<O>>,
        transform: (O) -> P,
    ): Single<List<P>> {
        val partitions = CollectionsHelper.setPartition(uids, pageSize)

        return Observable.fromIterable(partitions)
            .flatMapSingle(pageDownloader)
            .map { obj: Payload<O> -> obj.items() }
            .reduce(emptyList()) { items: List<O>, items2: List<O> ->
                items + items2
            }
            .map { items: List<O> ->
                items.map { transform(it) }
            }
    }

    // TODO : remove the Single making sure it is not needed by the callers
    private suspend fun <P, O> downloadPartitionedWithCustomHandling(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: (Set<String>) -> Single<Payload<O>>,
        transform: (O) -> P,
    ): List<P> {
        val oCollection = downloadPartitionedWithoutHandling(
            uids = uids,
            pageSize = pageSize,
            pageDownloader = pageDownloader,
            transform = transform,
        ).await()
        handler.handleMany(oCollection)
        return oCollection
    }

    override fun <K, V> downloadPartitionedMap(
        uids: Set<String>,
        pageSize: Int,
        handler: (Map<K, V>) -> Any,
        pageDownloader: (Set<String>) -> Single<out Map<K, V>>,
    ): Single<Map<K, V>> {
        val partitions = CollectionsHelper.setPartition(uids, pageSize)
        return Observable.fromIterable(partitions)
            .flatMapSingle(pageDownloader)
            .reduce(
                mapOf(),
            ) { items: Map<K, V>, items2: Map<K, V> ->
                items + items2
            }
            .doOnSuccess { map: Map<K, V> -> handler(map) }
    }

    override fun <P, O : CoreObject> downloadLink(
        masterUid: String,
        handler: LinkHandler<P, O>,
        downloader: (String) -> Single<Payload<P>>,
        transform: (P) -> O,
    ): Single<List<P>> {
        return Single.just(masterUid)
            .flatMap(downloader)
            .map { obj: Payload<P> -> obj.items() }
            .doOnSuccess { items: List<P> -> handler.handleMany(masterUid, items, transform) }
    }

    override fun <P> downloadWithLastUpdated(
        handler: Handler<P>,
        resourceType: Resource.Type,
        downloader: (String?) -> Single<Payload<P>>,
    ): Single<List<P>> {
        return Single.defer {
            downloader(
                resourceHandler.getLastUpdated(resourceType),
            )
        }
            .map { obj: Payload<P> -> obj.items() }
            .doOnSuccess { items: List<P> ->
                handler.handleMany(items)
                resourceHandler.handleResource(resourceType)
            }
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

    override fun <P> downloadPagedPayload(
        pageSize: Int,
        downloader: (page: Int, pageSize: Int) -> Single<Payload<P>>,
    ): Single<Payload<P>> {
        var page = 1
        return Single.defer { downloader(page++, pageSize) }
            .map { it.items() }
            .repeat()
            .takeUntil { list -> list.size < pageSize }
            .reduce(emptyList()) { items: List<P>, items2: List<P> ->
                items + items2
            }
            .map { list -> Payload(list) }
    }
}
