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

import io.reactivex.Observable
import kotlinx.coroutines.rx2.rxObservable
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.datastore.DataStoreEntry
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.koin.core.annotation.Singleton

@Singleton
internal class DataStoreDownloadCall(
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val networkHandler: DataStoreNetworkHandler,
    private val dataStoreEntryHandler: DataStoreHandler,
    private val versionManager: DHISVersionManager,
) {
    fun download(params: DataStoreDownloadParams): Observable<D2Progress> {
        return rxObservable {
            return@rxObservable coroutineAPICallExecutor.wrapTransactionally(
                cleanForeignKeyErrors = true,
            ) {
                networkHandler.getNamespaces()
                    .map { filterNamespaces(params, it) }
                    .fold(
                        onSuccess = { namespaces ->
                            val progressManager = D2ProgressManager(namespaces.size)
                            namespaces.forEach { namespace ->
                                downloadNamespace(namespace)
                                send(progressManager.increaseProgress(DataStoreEntry::class.java, isComplete = false))
                            }
                            send(progressManager.increaseProgress(DataStoreEntry::class.java, isComplete = true))
                        },
                        onFailure = { t -> throw t },
                    )
            }
        }
    }

    private fun filterNamespaces(params: DataStoreDownloadParams, namespaces: List<String>): List<String> {
        return if (params.namespaces.isNotEmpty()) {
            namespaces.filter { params.namespaces.contains(it) }
        } else {
            namespaces
        }
    }

    private suspend fun downloadNamespace(namespace: String): Result<List<DataStoreEntry>, D2Error> {
        return fetchNamespace(namespace).map { list ->
            dataStoreEntryHandler.handleMany(namespace, list) { t -> t }
            list
        }
    }

    private suspend fun fetchNamespace(namespace: String): Result<List<DataStoreEntry>, D2Error> {
        return if (versionManager.isGreaterOrEqualThan(DHISVersion.V2_38)) {
            fetchNamespace38(namespace)
        } else {
            fetchNamespace37(namespace)
        }
    }

    private suspend fun fetchNamespace38(namespace: String): Result<List<DataStoreEntry>, D2Error> {
        var pag = 1
        var entries: Result<List<DataStoreEntry>, D2Error> = Result.Success(emptyList())
        var lastPage: List<DataStoreEntry> = emptyList()

        do {
            val result = networkHandler.getNamespaceValues38(namespace, pag, PAGE_SIZE)
            result.fold(
                onSuccess = { dataStoreEntries ->
                    val pagedDataStoreEntries = dataStoreEntries.map { it.toBuilder().namespace(namespace).build() }

                    entries = Result.Success((entries.getOrNull() ?: emptyList()) + pagedDataStoreEntries)
                    lastPage = pagedDataStoreEntries
                    pag++
                },
                onFailure = { t ->
                    entries = Result.Failure(t)
                },
            )
        } while (lastPage.size >= PAGE_SIZE && result.succeeded)

        return entries
    }

    private suspend fun fetchNamespace37(namespace: String): Result<List<DataStoreEntry>, D2Error> {
        return networkHandler.getNamespaceKeys(namespace).map { keys ->
            keys.map { key ->
                networkHandler.getNamespaceKeyValue(namespace, key).getOrThrow()
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 50
    }
}
