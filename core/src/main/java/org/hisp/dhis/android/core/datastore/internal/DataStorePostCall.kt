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

import com.fasterxml.jackson.databind.JsonNode
import dagger.Reusable
import io.reactivex.Observable
import javax.inject.Inject
import kotlinx.coroutines.rx2.rxObservable
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.errorIfOnline
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.forcedOrOwn
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datastore.DataStoreEntry
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.hisp.dhis.android.core.maintenance.D2Error

@Reusable
@Suppress("MagicNumber")
internal class DataStorePostCall @Inject constructor(
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val dataStoreEntryService: DataStoreService,
    private val dataStoreEntryImportHandler: DataStoreImportHandler,
    private val store: DataStoreEntryStore
) {
    fun uploadDataStoreEntries(entries: List<DataStoreEntry>): Observable<D2Progress> {
        return Observable.defer {
            if (entries.isEmpty()) {
                return@defer Observable.empty<D2Progress>()
            } else {
                val progressManager = D2ProgressManager(entries.size)

                return@defer rxObservable {
                    entries.forEach {
                        postEntry(it)
                        send(progressManager.increaseProgress(DataStoreEntry::class.java, false))
                    }
                    send(progressManager.increaseProgress(DataStoreEntry::class.java, true))
                }
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
        val result = coroutineAPICallExecutor.wrap(
            storeError = false,
            acceptedErrorCodes = listOf(404),
            errorClass = HttpMessageResponse::class.java
        ) {
            dataStoreEntryService.deleteNamespaceKeyValue(entry.namespace(), entry.key())
        }

        result.fold(
            onSuccess = { r -> dataStoreEntryImportHandler.handleDelete(entry, r) },
            onFailure = { t -> store.setStateIfUploading(entry, forcedOrOwn(entry, errorIfOnline(t))) }
        )
    }

    private suspend fun createOrUpdate(entry: DataStoreEntry) {
        val result = when (entry.syncState()) {
            State.TO_POST ->
                tryCreate(entry).flatMap { r ->
                    when (r.httpStatusCode()) {
                        409 -> tryUpdate(entry)
                        else -> Result.Success(r)
                    }
                }
            else ->
                tryUpdate(entry).flatMap { r ->
                    when (r.httpStatusCode()) {
                        404 -> tryCreate(entry)
                        else -> Result.Success(r)
                    }
                }
        }

        result.fold(
            onSuccess = { r -> dataStoreEntryImportHandler.handleUpdateOrCreate(entry, r) },
            onFailure = { t -> store.setStateIfUploading(entry, forcedOrOwn(entry, errorIfOnline(t))) }
        )
    }

    private suspend fun tryCreate(entry: DataStoreEntry): Result<HttpMessageResponse, D2Error> {
        return coroutineAPICallExecutor.wrap(
            storeError = false,
            acceptedErrorCodes = listOf(409),
            errorClass = HttpMessageResponse::class.java
        ) {
            dataStoreEntryService.postNamespaceKeyValue(entry.namespace(), entry.key(), readValue(entry))
        }
    }

    private suspend fun tryUpdate(entry: DataStoreEntry): Result<HttpMessageResponse, D2Error> {
        return coroutineAPICallExecutor.wrap(
            storeError = false,
            acceptedErrorCodes = listOf(404),
            errorClass = HttpMessageResponse::class.java
        ) {
            dataStoreEntryService.putNamespaceKeyValue(entry.namespace(), entry.key(), readValue(entry))
        }
    }

    private fun readValue(entry: DataStoreEntry): JsonNode? {
        return entry.value()?.let { ObjectMapperFactory.objectMapper().readTree(it) }
    }
}
