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
package org.hisp.dhis.android.core.arch.repositories.`object`.internal

import io.reactivex.Completable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxCompletable
import org.hisp.dhis.android.core.arch.call.internal.DownloadProvider
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.CoreObject

open class ReadOnlyFirstObjectWithDownloadRepositoryImpl<M : CoreObject, R : ReadOnlyObjectRepository<M>>
internal constructor(
    store: ObjectStore<M>,
    childrenAppenders: ChildrenAppenderGetter<M>,
    scope: RepositoryScope,
    private val downloadProvider: DownloadProvider,
    repositoryFactory: ObjectRepositoryFactory<R>,
) : ReadOnlyOneObjectRepositoryImpl<M, R>(store, childrenAppenders, scope, repositoryFactory),
    ReadOnlyWithDownloadObjectRepository<M> {

    /**
     * Downloads the resource in scope in an asynchronous way. As soon as it's downloaded and processed, the
     * `Completable` is completed.
     * @return a `Completable` that completes when the download and processing is finished
     */
    override fun download(): Completable {
        return rxCompletable { downloadInternal() }
    }

    /**
     * Downloads the resource in scope in a synchronous way. The method will finish
     * as soon as the whole download and processing is finished. Important: this is a blocking method and it should
     * not be executed in the main thread. Consider the asynchronous version [.download].
     */
    override fun blockingDownload() {
        runBlocking { downloadInternal() }
    }

    private suspend fun downloadInternal() {
        downloadProvider.download(true)
    }
}
