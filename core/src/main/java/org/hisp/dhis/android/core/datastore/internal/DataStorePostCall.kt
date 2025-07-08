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
package org.hisp.dhis.android.core.datastore.internal

import io.ktor.http.HttpStatusCode
import io.reactivex.Observable
import kotlinx.coroutines.rx2.rxObservable
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.errorIfOnline
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.forcedOrOwn
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datastore.DataStoreEntry
import org.koin.core.annotation.Singleton

@Singleton
internal class DataStorePostCall(
    private val networkHandler: DataStoreNetworkHandler,
    private val dataStoreEntryImportHandler: DataStoreImportHandler,
    private val store: DataStoreEntryStore,
) {
    fun uploadDataStoreEntries(entries: List<DataStoreEntry>): Observable<D2Progress> =
        Observable.defer {
            if (entries.isEmpty()) {
                Observable.empty()
            } else {
                val progressManager = D2ProgressManager(entries.size)
                rxObservable {
                    entries.forEach { entry ->
                        postEntry(entry)
                        send(progressManager.increaseProgress(DataStoreEntry::class.java, isComplete = false))
                    }
                    send(progressManager.increaseProgress(DataStoreEntry::class.java, isComplete = true))
                }
            }
        }

    private suspend fun postEntry(entry: DataStoreEntry) {
        store.setState(entry, forcedOrOwn(entry, State.UPLOADING))

        if (entry.deleted() == true) {
            deleteEntry(entry)
        } else {
            createOrUpdate(entry)
        }
    }

    private suspend fun deleteEntry(entry: DataStoreEntry) {
        networkHandler.deleteNamespaceKeyValue(entry).fold(
            onSuccess = { r -> dataStoreEntryImportHandler.handleDelete(entry, r) },
            onFailure = { t -> store.setStateIfUploading(entry, forcedOrOwn(entry, errorIfOnline(t))) },
        )
    }

    private suspend fun createOrUpdate(entry: DataStoreEntry) {
        val result = when (entry.syncState()) {
            State.TO_POST ->
                networkHandler.postNamespaceKeyValue(entry).flatMap { r ->
                    when (r.httpStatusCode()) {
                        HttpStatusCode.Conflict.value -> networkHandler.putNamespaceKeyValue(entry)
                        else -> Result.Success(r)
                    }
                }

            else ->
                networkHandler.putNamespaceKeyValue(entry).flatMap { r ->
                    when (r.httpStatusCode()) {
                        HttpStatusCode.NotFound.value -> networkHandler.postNamespaceKeyValue(entry)
                        else -> Result.Success(r)
                    }
                }
        }

        result.fold(
            onSuccess = { r -> dataStoreEntryImportHandler.handleUpdateOrCreate(entry, r) },
            onFailure = { t -> store.setStateIfUploading(entry, forcedOrOwn(entry, errorIfOnline(t))) },
        )
    }
}
