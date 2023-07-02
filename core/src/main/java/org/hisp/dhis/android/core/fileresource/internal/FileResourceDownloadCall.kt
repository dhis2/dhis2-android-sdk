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
package org.hisp.dhis.android.core.fileresource.internal

import android.content.Context
import android.util.Log
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer
import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.FileResourceDomainType
import org.hisp.dhis.android.core.fileresource.FileResourceElementType
import org.hisp.dhis.android.core.fileresource.FileResourceInternalAccessor
import org.hisp.dhis.android.core.fileresource.FileResourceRoutine
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.settings.SynchronizationSettings
import javax.inject.Inject

@Reusable
internal class FileResourceDownloadCall @Inject constructor(
    private val fileResourceStore: IdentifiableDataObjectStore<FileResource>,
    private val helper: FileResourceDownloadCallHelper,
    private val fileResourceService: FileResourceService,
    private val handler: HandlerWithTransformer<FileResource>,
    private val fileResourceRoutine: FileResourceRoutine,
    private val synchronizationSettingsStore: ObjectWithoutUidStore<SynchronizationSettings>,
    private val context: Context,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor

) {

    fun download(params: FileResourceDownloadParams): Flow<D2Progress> = flow {
        val progressManager = D2ProgressManager(2)
        val existingFileResources = fileResourceStore.selectUids()

        val paramsWithCorrectedMaxContentLength = params.copy(
            maxContentLength = params.maxContentLength
                ?: synchronizationSettingsStore.selectFirst()?.fileMaxLengthBytes()
                ?: defaultDownloadMaxContentLength
        )

        downloadAggregatedValues(paramsWithCorrectedMaxContentLength, existingFileResources)
        emit(progressManager.increaseProgress(FileResource::class.java, isComplete = false))

        downloadTrackerValues(paramsWithCorrectedMaxContentLength, existingFileResources)
        emit(progressManager.increaseProgress(FileResource::class.java, isComplete = false))
        fileResourceRoutine.blockingDeleteOutdatedFileResources()
    }

    private suspend fun downloadAggregatedValues(
        params: FileResourceDownloadParams,
        existingFileResources: List<String>
    ) {
        if (params.domainTypes.contains(FileResourceDomainType.AGGREGATED)) {
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

    private suspend fun downloadTrackerValues(params: FileResourceDownloadParams, existingFileResources: List<String>) {
        if (params.domainTypes.contains(FileResourceDomainType.TRACKER)) {
            if (params.elementTypes.contains(FileResourceElementType.TRACED_ENTITY_ATTRIBUTE)) {
                val attributeDataValues = helper.getMissingTrackerAttributeValues(params, existingFileResources)

                downloadAndPersistFiles(
                    values = attributeDataValues,
                    maxContentLength = params.maxContentLength,
                    download = { v ->
                        when (v.valueType) {
                            ValueType.IMAGE ->
                                fileResourceService.getImageFromTrackedEntityAttribute(
                                    v.value.trackedEntityInstance()!!,
                                    v.value.trackedEntityAttribute()!!,
                                    FileResizerHelper.Dimension.MEDIUM.name
                                )

                            ValueType.FILE_RESOURCE ->
                                fileResourceService.getFileFromTrackedEntityAttribute(
                                    v.value.trackedEntityInstance()!!,
                                    v.value.trackedEntityAttribute()!!
                                )

                            else -> null
                        }
                    },
                    getUid = { v -> v.value.value() }
                )
            }

            if (params.elementTypes.contains(FileResourceElementType.DATA_ELEMENT)) {
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

    private suspend fun <V> downloadAndPersistFiles(
        values: List<V>,
        maxContentLength: Int?,
        download: suspend (V) -> ResponseBody?,
        getUid: (V) -> String?
    ) {
        val fileResources = values.mapNotNull { downloadFile(it, maxContentLength, download, getUid) }

        handler.handleMany(fileResources) { fileResource: FileResource ->
            fileResource.toBuilder()
                .syncState(State.SYNCED)
                .build()
        }
    }


    private suspend fun <V> downloadFile(
        value: V,
        maxContentLength: Int?,
        download: suspend (V) -> ResponseBody?,
        getUid: (V) -> String?
    ): FileResource? {

        return getUid(value)?.let { uid ->
            try {
                val fileResource =
                    coroutineAPICallExecutor.wrap { fileResourceService.getFileResource(uid) }.getOrThrow()

                val acceptedContentLength = (maxContentLength == null) ||
                        (fileResource.contentLength() == null) ||
                        (fileResource.contentLength()!! <= maxContentLength)

                if (acceptedContentLength && FileResourceInternalAccessor.isStored(fileResource)) {
                    val responseBody = coroutineAPICallExecutor.wrap { download(value) }.getOrThrow()

                    if (responseBody == null) {
                        null
                    } else {
                        val file = FileResourceUtil.saveFileFromResponse(responseBody, fileResource, context)
                        fileResource.toBuilder().path(file.absolutePath).build()
                    }
                } else {
                    null
                }
            } catch (d2Error: D2Error) {
                fileResourceStore.deleteIfExists(uid)
                Log.v(FileResourceDownloadCall::class.java.canonicalName, d2Error.errorDescription())
                null
            }
        }

    }


    companion object {
        const val defaultDownloadMaxContentLength: Int = 6000000
    }
}
