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
package org.hisp.dhis.android.core.arch.api.executors.internal

import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.resource.internal.Resource

internal interface APIDownloader {
    fun <P> downloadPartitioned(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: (Set<String>) -> Single<Payload<P>>
    ): Single<List<P>>

    fun <P, O> downloadPartitioned(
        uids: Set<String>,
        pageSize: Int,
        handler: Handler<P>,
        pageDownloader: (Set<String>) -> Single<Payload<O>>,
        transform: (O) -> P
    ): Single<List<P>>

    fun <P> downloadPartitioned(
        uids: Set<String>,
        pageSize: Int,
        pageDownloader: (Set<String>) -> Single<Payload<P>>,
    ): Single<List<P>>

    fun <K, V> downloadPartitionedMap(
        uids: Set<String>,
        pageSize: Int,
        handler: (Map<K, V>) -> Any,
        pageDownloader: (Set<String>) -> Single<out Map<K, V>>
    ): Single<Map<K, V>>

    fun <P, O : CoreObject> downloadLink(
        masterUid: String,
        handler: LinkHandler<P, O>,
        downloader: (String) -> Single<Payload<P>>,
        transform: ((P) -> O)
    ): Single<List<P>>

    fun <P> downloadWithLastUpdated(
        handler: Handler<P>,
        resourceType: Resource.Type,
        downloader: (String?) -> Single<Payload<P>>
    ): Single<List<P>>

    fun <P> download(handler: Handler<P>, downloader: Single<Payload<P>>): Single<List<P>>
    fun <P> downloadList(handler: Handler<P>, downloader: Single<List<P>>): Single<List<P>>
    fun <P> downloadObject(handler: Handler<P>, downloader: Single<P>): Single<P>
}
