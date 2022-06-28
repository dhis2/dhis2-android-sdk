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
package org.hisp.dhis.android.core.fileresource.internal

import android.content.Context
import android.util.Log
import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import okhttp3.ResponseBody
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer
import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.FileResourceDomain
import org.hisp.dhis.android.core.fileresource.FileResourceElement
import org.hisp.dhis.android.core.maintenance.D2Error
import retrofit2.Call
import javax.inject.Inject

@Reusable
internal class FileResourceDownloadCall @Inject constructor(
    private val rxCallExecutor: RxAPICallExecutor,
    private val fileResourceStore: IdentifiableDataObjectStore<FileResource>,
    private val helper: FileResourceDownloadCallHelper,
    private val fileResourceService: FileResourceService,
    private val handler: HandlerWithTransformer<FileResource>,
    private val apiCallExecutor: APICallExecutor,
    private val context: Context
) {

    fun download(params: FileResourceDownloadParams): Observable<D2Progress> {
        val progressManager = D2ProgressManager(2)
        val existingFileResources = fileResourceStore.selectUids()

        return rxCallExecutor.wrapObservableTransactionally(
            Observable.create { emitter: ObservableEmitter<D2Progress> ->
                downloadAggregatedValues(params, existingFileResources)
                emitter.onNext(progressManager.increaseProgress(FileResource::class.java, isComplete = false))

                downloadTrackerValues(params, existingFileResources)
                emitter.onNext(progressManager.increaseProgress(FileResource::class.java, isComplete = false))

                emitter.onComplete()
            },
            true
        )
    }

    private fun downloadAggregatedValues(params: FileResourceDownloadParams, existingFileResources: List<String>) {
        if (params.domains.contains(FileResourceDomain.AGGREGATED)) {
            val dataValues = helper.getMissingAggregatedDataValues(params, existingFileResources)

            downloadAndPersistFiles(
                values = dataValues,
                maxContentLength = params.maxContentLength,
                download = { v ->
                    fileResourceService.getFileFromDataValue(
                        v.dataElement()!!,
                        v.period()!!,
                        v.organisationUnit()!!,
                        v.attributeOptionCombo()!!,
                        FileResizerHelper.Dimension.MEDIUM.name
                    )
                },
                getUid = { v -> v.value() }
            )
        }
    }

    private fun downloadTrackerValues(params: FileResourceDownloadParams, existingFileResources: List<String>) {
        if (params.domains.contains(FileResourceDomain.TRACKER)) {
            if (params.elements.contains(FileResourceElement.TRACED_ENTITY_ATTRIBUTE)) {
                val attributeDataValues = helper.getMissingTrackerAttributeValues(params, existingFileResources)

                downloadAndPersistFiles(
                    values = attributeDataValues,
                    maxContentLength = params.maxContentLength,
                    download = { v ->
                        fileResourceService.getFileFromTrackedEntityAttribute(
                            v.trackedEntityInstance()!!,
                            v.trackedEntityAttribute()!!,
                            FileResizerHelper.Dimension.MEDIUM.name
                        )
                    },
                    getUid = { v -> v.value() }
                )
            }

            if (params.elements.contains(FileResourceElement.DATA_ELEMENT)) {
                val trackerDataValues = helper.getMissingTrackerDataValues(params, existingFileResources)

                downloadAndPersistFiles(
                    values = trackerDataValues,
                    maxContentLength = params.maxContentLength,
                    download = { v ->
                        fileResourceService.getFileFromEventValue(
                            v.event()!!,
                            v.dataElement()!!,
                            FileResizerHelper.Dimension.MEDIUM.name
                        )
                    },
                    getUid = { v -> v.value() }
                )
            }
        }
    }

    private fun <V> downloadAndPersistFiles(
        values: List<V>,
        maxContentLength: Int?,
        download: (V) -> Call<ResponseBody>,
        getUid: (V) -> String?,
    ) {
        val fileResources: MutableList<FileResource> = ArrayList()
        for (value in values) {
            getUid(value)?.let { uid ->
                try {
                    val fileResource = apiCallExecutor.executeObjectCall(fileResourceService.getFileResource(uid))

                    if (maxContentLength == null ||
                        fileResource.contentLength() == null ||
                        fileResource.contentLength()!! <= maxContentLength
                    ) {
                        val responseBody = apiCallExecutor.executeObjectCall(download(value))

                        val file = FileResourceUtil.saveFileFromResponse(responseBody, uid, context)
                        fileResources.add(fileResource.toBuilder().path(file.absolutePath).build())
                    } else {
                        // Ignore
                    }
                } catch (d2Error: D2Error) {
                    fileResourceStore.deleteIfExists(uid)
                    Log.v(FileResourceDownloadCall::class.java.canonicalName, d2Error.errorDescription())
                }
            }
        }

        handler.handleMany(fileResources) { fileResource: FileResource ->
            fileResource.toBuilder()
                .syncState(State.SYNCED)
                .build()
        }
    }
}
