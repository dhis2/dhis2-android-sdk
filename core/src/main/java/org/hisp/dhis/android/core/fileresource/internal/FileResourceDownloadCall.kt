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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.fetchers.internal.UidsNoResourceCallFetcher
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.call.queries.internal.UidsQuery
import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper.DimensionSize
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.FileResourceDataDomainType
import org.hisp.dhis.android.core.fileresource.FileResourceDomainType
import org.hisp.dhis.android.core.fileresource.FileResourceElementType
import org.hisp.dhis.android.core.fileresource.FileResourceInternalAccessor
import org.hisp.dhis.android.core.fileresource.FileResourceRoutine
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil.computeScalingDimension
import org.hisp.dhis.android.core.icon.CustomIcon
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.settings.internal.SynchronizationSettingStore
import org.koin.core.annotation.Singleton

@SuppressWarnings("LongParameterList", "MagicNumber")
@Singleton
internal class FileResourceDownloadCall(
    private val fileResourceStore: FileResourceStore,
    private val helper: FileResourceDownloadCallHelper,
    private val fileResourceNetworkHandlder: FileResourceNetworkHandler,
    private val handler: FileResourceHandler,
    private val fileResourceRoutine: FileResourceRoutine,
    private val synchronizationSettingsStore: SynchronizationSettingStore,
    private val context: Context,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
) {

    fun download(params: FileResourceDownloadParams): Flow<D2Progress> = flow {
        val progressManager = D2ProgressManager(4)
        val existingFileResources = fileResourceStore.selectUids()

        val paramsWithCorrectedMaxContentLength = params.copy(
            maxContentLength = params.maxContentLength
                ?: synchronizationSettingsStore.selectFirst()?.fileMaxLengthBytes()
                ?: defaultDownloadMaxContentLength,
        )

        downloadAggregatedValues(paramsWithCorrectedMaxContentLength, existingFileResources)
        emit(progressManager.increaseProgress(FileResource::class.java, isComplete = false))

        downloadTrackerValues(paramsWithCorrectedMaxContentLength, existingFileResources)
        emit(progressManager.increaseProgress(FileResource::class.java, isComplete = false))

        downloadCustomIcons(paramsWithCorrectedMaxContentLength, existingFileResources)
        emit(progressManager.increaseProgress(FileResource::class.java, isComplete = false))

        fileResourceRoutine.blockingDeleteOutdatedFileResources()
        emit(progressManager.increaseProgress(FileResource::class.java, isComplete = true))
    }

    private suspend fun downloadAggregatedValues(
        params: FileResourceDownloadParams,
        existingFileResources: List<String>,
    ) {
        if (params.domainTypes.contains(FileResourceDomainType.DATA_VALUE) &&
            params.dataDomainTypes.contains(FileResourceDataDomainType.AGGREGATED) &&
            !params.hasAnyTrackerData()
        ) {
            val dataValues = helper.getMissingAggregatedDataValues(params, existingFileResources)

            downloadAndPersistFiles(
                values = dataValues,
                maxContentLength = params.maxContentLength,
                download = fileResourceNetworkHandlder::getFileFromDataValue,
                getUid = { v -> v.value() },
            )
        }
    }

    private suspend fun downloadTrackerValues(params: FileResourceDownloadParams, existingFileResources: List<String>) {
        if (params.domainTypes.contains(FileResourceDomainType.DATA_VALUE) &&
            params.dataDomainTypes.contains(FileResourceDataDomainType.TRACKER) &&
            !params.hasAnyAggregatedData()
        ) {
            if (params.elementTypes.contains(FileResourceElementType.TRACKED_ENTITY_ATTRIBUTE)) {
                val attributeDataValues = helper.getMissingTrackerAttributeValues(params, existingFileResources)

                downloadAndPersistFiles(
                    values = attributeDataValues,
                    maxContentLength = params.maxContentLength,
                    download = { v, dimension ->
                        when (v.valueType) {
                            ValueType.IMAGE ->
                                fileResourceNetworkHandlder.getImageFromTrackedEntityAttribute(v, dimension)

                            ValueType.FILE_RESOURCE ->
                                fileResourceNetworkHandlder.getFileFromTrackedEntityAttribute(v)

                            else -> null
                        }
                    },
                    getUid = { v -> v.value.value() },
                )
            }

            if (params.elementTypes.contains(FileResourceElementType.DATA_ELEMENT)) {
                val trackerDataValues = helper.getMissingTrackerDataValues(params, existingFileResources)

                downloadAndPersistFiles(
                    values = trackerDataValues,
                    maxContentLength = params.maxContentLength,
                    download = fileResourceNetworkHandlder::getFileFromEventValue,
                    getUid = { v -> v.value() },
                )
            }
        }
    }

    private suspend fun downloadCustomIcons(params: FileResourceDownloadParams, existingFileResources: List<String>) {
        if (params.domainTypes.contains(FileResourceDomainType.ICON) && !params.hasAnyData()) {
            val iconKeys: List<CustomIcon> = helper.getMissingCustomIcons(existingFileResources)

            downloadAndPersistFiles(
                values = iconKeys,
                maxContentLength = params.maxContentLength,
                download = { v, _ -> fileResourceNetworkHandlder.getCustomIcon(v) },
                getUid = { v -> v.fileResource().uid() },
            )
        }
    }

    private suspend fun <V> downloadAndPersistFiles(
        values: List<V>,
        maxContentLength: Int?,
        download: suspend (V, String) -> ByteArray?,
        getUid: (V) -> String?,
    ) {
        val fileResources = getFileResources(values, getUid)
        val storedFileResources = fileResources.mapNotNull { (fileResource, value) ->
            downloadFile(value, maxContentLength, download, fileResource)
        }

        handler.handleMany(storedFileResources) { fileResource: FileResource ->
            fileResource.toBuilder()
                .syncState(State.SYNCED)
                .build()
        }
    }

    private suspend fun <V> getFileResources(
        values: List<V>,
        getUid: (V) -> String?,
    ): List<Pair<FileResource, V>> {
        val valueMap = values.associateBy { value -> getUid(value) }
        return if (valueMap.isEmpty()) {
            emptyList()
        } else {
            val getIdsValuePairsFunction = getIdsValuePairsFactory<V>()
            try {
                getIdsValuePairsFunction(valueMap)
            } catch (d2Error: D2Error) {
                Log.v(FileResourceDownloadCall::class.java.canonicalName, d2Error.errorDescription())
                emptyList()
            }
        }
    }

    private fun <V> getIdsValuePairsFactory(
        shouldGetFileResourcesInBulk: Boolean = false,
    ): suspend (Map<String?, V>) -> List<Pair<FileResource, V>> {
        // request type forced to be sequential while ticket DHIS2-17535 gets resolved
        // then, use version manager to return inBulk request type from proper version
        return if (shouldGetFileResourcesInBulk) {
            ::getIdsValuePairsInBulk
        } else {
            ::getIdsValuePairsSequentially
        }
    }

    private suspend fun <V> getIdsValuePairsInBulk(valueMap: Map<String?, V>): List<Pair<FileResource, V>> {
        val fileResourcesList =
            object : UidsNoResourceCallFetcher<FileResource>(
                valueMap.keys.filterNotNull().toSet(),
                MAX_UID_LIST_SIZE,
                coroutineAPICallExecutor,
            ) {
                override suspend fun getCall(query: UidsQuery): Payload<FileResource> {
                    return fileResourceNetworkHandlder.getFileResources(query)
                }
            }.fetch()
        return fileResourcesList.mapNotNull { fileResource ->
            valueMap[fileResource.uid()]?.let { value ->
                Pair(fileResource, value)
            }
        }
    }

    private suspend fun <V> getIdsValuePairsSequentially(valueMap: Map<String?, V>): List<Pair<FileResource, V>> {
        return valueMap.mapNotNull { (uid, value) ->
            uid?.let {
                val frResult = coroutineAPICallExecutor.wrap {
                    fileResourceNetworkHandlder.getFileResource(
                        uid,
                    )
                }
                when (frResult) {
                    is Result.Success -> {
                        Pair(frResult.value, value)
                    }

                    is Result.Failure -> {
                        Log.v(FileResourceDownloadCall::class.java.canonicalName, frResult.failure.errorDescription())
                        null
                    }
                }
            }
        }
    }

    @Suppress("NestedBlockDepth")
    private suspend fun <V> downloadFile(
        value: V,
        maxContentLength: Int?,
        download: suspend (V, String) -> ByteArray?,
        fileResource: FileResource,
    ): FileResource? {
        val contentLength = fileResource.contentLength()
        val fileIsImage = fileResource.contentType()?.startsWith("image") ?: false
        val scalingDimension = computeScalingDimension(contentLength, maxContentLength?.toLong(), fileIsImage)

        return try {
            if (scalingDimension != DimensionSize.NotSupported.name &&
                FileResourceInternalAccessor.isStored(fileResource)
            ) {
                val responseByteArray = coroutineAPICallExecutor.wrap { download(value, scalingDimension) }.getOrThrow()
                responseByteArray?.let {
                    val file = FileResourceUtil.saveFileFromResponse(it, fileResource, context)
                    fileResource.toBuilder().path(file.absolutePath).build()
                }
            } else {
                null
            }
        } catch (d2Error: D2Error) {
            fileResource.uid()?.let { fileResourceStore.deleteIfExists(it) }
            Log.v(FileResourceDownloadCall::class.java.canonicalName, d2Error.errorDescription())
            null
        }
    }

    companion object {
        const val defaultDownloadMaxContentLength: Int = 6000000
        private const val MAX_UID_LIST_SIZE = 100
    }
}
