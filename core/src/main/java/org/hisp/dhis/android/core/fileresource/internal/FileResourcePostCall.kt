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
import android.webkit.MimeTypeMap
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.systeminfo.internal.PingCall
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import org.hisp.dhis.android.persistence.datavalue.DataValueTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityDataValueTableInfo
import org.koin.core.annotation.Singleton
import java.io.File

@Singleton
@Suppress("TooManyFunctions")
internal class FileResourcePostCall(
    private val fileResourceNetworkHandler: FileResourceNetworkHandler,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val dataValueStore: DataValueStore,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore,
    private val fileResourceStore: FileResourceStore,
    private val fileResourceHandler: FileResourceHandler,
    private val pingCall: PingCall,
    private val context: Context,
) {

    private var alreadyPinged = false

    /**
     * Uploads a file resource without updating the associated value immediately.
     * This allows for batch verification of storage status before updating values.
     *
     * @return FileResourceUploadResult containing upload information
     */
    @Suppress("TooGenericExceptionCaught")
    suspend fun uploadFileResourceWithoutUpdate(
        fileResource: FileResource,
        value: FileResourceValue,
    ): FileResourceUploadResult {
        // Workaround for ANDROSDK-1452 (see comments restricted to Contributors).
        if (!alreadyPinged) {
            pingCall.download(true)
            alreadyPinged = true
        }

        val file = FileResourceUtil.getFile(fileResource)

        return if (file != null) {
            try {
                val fileName = fileResource.name() ?: file.name
                val filePart = getFilePart(file, fileName)
                val responseFileResource = coroutineAPICallExecutor.wrap(storeError = true) {
                    fileResourceNetworkHandler.uploadFile(filePart)
                }.getOrThrow()

                val uploadedUid = responseFileResource.uid()!!
                val downloadedFile = FileResourceUtil.renameFile(file, uploadedUid, context)
                updateFileResource(fileResource, responseFileResource, downloadedFile)

                FileResourceUploadResult(
                    originalFileResource = fileResource,
                    uploadedUid = uploadedUid,
                    value = value,
                    success = true,
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading file resource: ${e.message}", e)
                FileResourceUploadResult(
                    originalFileResource = fileResource,
                    uploadedUid = null,
                    value = value,
                    success = false,
                )
            }
        } else {
            handleMissingFile(fileResource, value)
            FileResourceUploadResult(
                originalFileResource = fileResource,
                uploadedUid = null,
                value = value,
                success = false,
            )
        }
    }

    /**
     * Updates the value associated with a file resource after verification.
     * This should be called after verifying that the file resource is stored.
     */
    suspend fun updateValueAfterVerification(
        originalFileResource: FileResource,
        verifiedUid: String,
        value: FileResourceValue,
    ) {
        updateValue(originalFileResource, verifiedUid, value)
    }

    private fun getFilePart(file: File, fileName: String): MultiPartFormDataContent {
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/*"

        return MultiPartFormDataContent(
            formData {
                append(
                    key = "file",
                    value = file.readBytes(),
                    headers = Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        append(HttpHeaders.ContentType, type)
                    },
                )
            },
        )
    }

    private suspend fun handleMissingFile(fileResource: FileResource, value: FileResourceValue) {
        if (fileResource.uid() != null) {
            updateValue(fileResource, null, value)
            fileResourceStore.deleteIfExists(fileResource.uid()!!)
        }
    }

    private suspend fun updateValue(
        fileResource: FileResource,
        newUid: String?,
        value: FileResourceValue,
    ) {
        val updateValueMethod = when (value) {
            is FileResourceValue.DataValue -> ::updateAggregatedDataValue
            is FileResourceValue.EventValue -> ::updateTrackedEntityDataValue
            is FileResourceValue.AttributeValue -> ::updateTrackedEntityAttributeValue
        }

        updateValueMethod(fileResource, newUid, value.uid)
    }

    private suspend fun updateAggregatedDataValue(fileResource: FileResource, newUid: String?, elementUid: String) {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(DataValueTableInfo.Columns.VALUE, fileResource.uid()!!)
            .appendKeyStringValue(DataValueTableInfo.Columns.DATA_ELEMENT, elementUid)
            .build()

        dataValueStore.selectOneWhere(whereClause)?.let { dataValue ->
            val newValue =
                if (newUid == null) {
                    dataValue.toBuilder().deleted(true).build()
                } else {
                    dataValue.toBuilder().value(newUid).build()
                }

            dataValueStore.updateWhere(newValue)
        }
    }

    private suspend fun updateTrackedEntityAttributeValue(
        fileResource: FileResource,
        newUid: String?,
        elementUid: String,
    ): Boolean {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(TrackedEntityAttributeValueTableInfo.Columns.VALUE, fileResource.uid()!!)
            .appendKeyStringValue(TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE, elementUid)
            .build()

        return trackedEntityAttributeValueStore.selectOneWhere(whereClause)?.let { attributeValue ->
            trackedEntityAttributeValueStore.updateWhere(
                attributeValue.toBuilder()
                    .value(newUid)
                    .build(),
            )
            true
        } ?: false
    }

    private suspend fun updateTrackedEntityDataValue(fileResource: FileResource, newUid: String?, elementUid: String) {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.VALUE, fileResource.uid()!!)
            .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT, elementUid)
            .build()

        trackedEntityDataValueStore.selectOneWhere(whereClause)?.let { dataValue ->
            trackedEntityDataValueStore.updateWhere(
                dataValue.toBuilder()
                    .value(newUid)
                    .build(),
            )
        }
    }

    private suspend fun updateFileResource(
        fileResource: FileResource,
        downloadedFileResource: FileResource,
        file: File,
    ) {
        fileResourceStore.deleteByEntity(fileResource)
        fileResourceHandler.handle(
            downloadedFileResource.toBuilder()
                .syncState(State.UPLOADING)
                .path(file.absolutePath)
                .build(),
        )
    }

    companion object {
        internal const val TAG = "FileResourcePostCall"
    }
}
