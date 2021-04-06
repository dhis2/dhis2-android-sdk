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
package org.hisp.dhis.android.core.arch.api.executors.internal

import androidx.annotation.VisibleForTesting
import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.resource.internal.Resource
import org.hisp.dhis.android.core.resource.internal.ResourceHandler

@Reusable
@VisibleForTesting
internal class APIDownloaderImpl @Inject constructor(private val resourceHandler: ResourceHandler) : APIDownloader {

    override fun <P> downloadPartitionedWithCustomHandling(
        uids: Set<String>,
        pageSize: Int,
        customHandling: Consumer<List<P>>,
        pageDownloader: (Set<String>) -> Single<Payload<P>>
    ): Single<List<P>> {
        return downloadPartitionedWithCustomHandling(uids, pageSize, customHandling, pageDownloader, null)
    }

    private fun <P> downloadPartitionedWithCustomHandling(
        uids: Set<String>,
        pageSize: Int,
        customHandling: Consumer<List<P>>,
        pageDownloader: (Set<String>) -> Single<Payload<P>>,
        transform: ((P) -> P)?
    ): Single<List<P>> {
        val partitions = CollectionsHelper.setPartition(uids, pageSize)
        return Observable.fromIterable(partitions)
            .flatMapSingle(pageDownloader)
            .map { obj: Payload<P> -> obj.items() }
            .reduce(
                listOf(),
                { items: List<P>, items2: List<P> ->
                    items + items2
                }
            )
            .map { items: List<P> ->
                if (transform == null) {
                    items
                } else {
                    items.map { transform(it) }
                }
            }
            .doOnSuccess(customHandling)
    }

    override fun <P> downloadPartitioned(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: (Set<String>) -> Single<Payload<P>>
    ): Single<List<P>> {
        return downloadPartitioned(uids, pageSize, handler, pageDownloader, null)
    }

    override fun <P> downloadPartitioned(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: (Set<String>) -> Single<Payload<P>>,
        transform: ((P) -> P)?
    ): Single<List<P>> {
        return downloadPartitionedWithCustomHandling(
            uids,
            pageSize,
            Consumer { oCollection: List<P> -> handler.handleMany(oCollection) },
            pageDownloader,
            transform
        )
    }

    override fun <P, O : CoreObject> downloadLink(
        masterUid: String,
        handler: LinkHandler<P, O>,
        downloader: (String) -> Single<Payload<P>>,
        transform: (P) -> O
    ): Single<List<P>> {
        return Single.just(masterUid)
            .flatMap(downloader)
            .map { obj: Payload<P> -> obj.items() }
            .doOnSuccess { items: List<P> -> handler.handleMany(masterUid, items, transform) }
    }

    override fun <P> downloadWithLastUpdated(
        handler: Handler<P>,
        resourceType: Resource.Type,
        downloader: (String?) -> Single<Payload<P>>
    ): Single<List<P>> {
        return Single.defer {
            downloader(
                resourceHandler.getLastUpdated(resourceType)
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

    override fun <P> downloadObject(handler: Handler<P>, downloader: Single<P>): Single<P> {
        return downloader
            .doOnSuccess { o: P -> handler.handle(o) }
    }
}
