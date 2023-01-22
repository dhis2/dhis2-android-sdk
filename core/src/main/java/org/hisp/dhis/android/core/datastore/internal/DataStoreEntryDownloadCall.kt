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

import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datastore.DataStoreEntry
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import javax.inject.Inject

@Reusable
internal class DataStoreEntryDownloadCall @Inject constructor(
    private val rxCallExecutor: RxAPICallExecutor,
    private val dataStoreEntryService: DataStoreEntryService,
    private val dataStoreEntryHandler: LinkHandler<DataStoreEntry, DataStoreEntry>,
    private val versionManager: DHISVersionManager
) {
    fun download(params: DataStoreEntryDownloadParams): Observable<D2Progress> {
        return rxCallExecutor.wrapObservableTransactionally(
            rxCallExecutor.wrapSingle(dataStoreEntryService.getNamespaces(), storeError = false)
                .map { filterNamespaces(params, it) }
                .flatMapObservable { namespaces ->
                    val progressManager = D2ProgressManager(namespaces.size)
                    Observable.fromIterable(namespaces)
                        .flatMapSingle { downloadNamespace(it) }
                        .map { progressManager.increaseProgress(DataStoreEntry::class.java, isComplete = false) }
                },
            cleanForeignKeys = false
        )
    }

    private fun filterNamespaces(params: DataStoreEntryDownloadParams, namespaces: List<String>): List<String> {
        return if (params.namespaces.isNotEmpty()) {
            namespaces.filter { params.namespaces.contains(it) }
        } else {
            namespaces
        }
    }

    private fun downloadNamespace(namespace: String): Single<Unit> {
        return fetchNamespace(namespace).map { list ->
            dataStoreEntryHandler.handleMany(namespace, list) { t -> t }
        }
    }

    private fun fetchNamespace(namespace: String): Single<List<DataStoreEntry>> {
        return if (versionManager.isGreaterOrEqualThan(DHISVersion.V2_38)) {
            fetchNamespace38(namespace)
        } else {
            fetchNamespace37(namespace)
        }
    }

    private fun fetchNamespace38(namespace: String): Single<List<DataStoreEntry>> {
        var page = 1
        return rxCallExecutor.wrapSingle(Single.defer {
            dataStoreEntryService.getNamespaceValues38(namespace, page, PAGE_SIZE)
        }, storeError = false)
            .map { pagedEntry ->
                pagedEntry.entries.map { keyValuePair ->
                    val strValue = ObjectMapperFactory.objectMapper().writeValueAsString(keyValuePair.value)
                    DataStoreEntry.builder()
                        .namespace(namespace)
                        .key(keyValuePair.key)
                        .value(strValue)
                        .build()
                }
            }
            .doOnSuccess { page++ }
            .repeat()
            .takeUntil { entries -> entries.size < PAGE_SIZE }
            .toList()
            .map { list -> list.flatten() }
    }

    private fun fetchNamespace37(namespace: String): Single<List<DataStoreEntry>> {
        return rxCallExecutor.wrapSingle(dataStoreEntryService.getNamespaceKeys(namespace), storeError = false)
            .flatMap { keys ->
                Observable.fromIterable(keys)
                    .flatMapSingle { key ->
                        rxCallExecutor.wrapSingle(dataStoreEntryService.getNamespaceKeyValue(namespace, key), false)
                            .map { value ->
                                val strValue = ObjectMapperFactory.objectMapper().writeValueAsString(value)
                                DataStoreEntry.builder()
                                    .namespace(namespace)
                                    .key(key)
                                    .value(strValue)
                                    .syncState(State.SYNCED)
                                    .deleted(false)
                                    .build()
                            }
                    }
                    .toList()
            }
    }

    companion object {
        private const val PAGE_SIZE = 50
    }
}
