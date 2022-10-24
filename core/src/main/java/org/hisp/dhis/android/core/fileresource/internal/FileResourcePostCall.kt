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
import android.webkit.MimeTypeMap
import dagger.Reusable
import java.io.File
import java.io.IOException
import javax.inject.Inject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory.objectMapper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.systeminfo.internal.PingCall
import org.hisp.dhis.android.core.trackedentity.*
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore

@Reusable
internal class FileResourcePostCall @Inject constructor(
    private val fileResourceService: FileResourceService,
    private val apiCallExecutor: APICallExecutor,
    private val dataValueStore: DataValueStore,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore,
    private val fileResourceStore: IdentifiableDataObjectStore<FileResource>,
    private val fileResourceHandler: HandlerWithTransformer<FileResource>,
    private val pingCall: PingCall,
    private val context: Context
) {

    private var alreadyPinged = false

    fun uploadFileResource(fileResource: FileResource, value: FileResourceValue): String? {
        // Workaround for ANDROSDK-1452 (see comments restricted to Contributors).
        if (!alreadyPinged) {
            pingCall.getCompletable(true).blockingAwait()
            alreadyPinged = true
        }

        val file = FileResourceUtil.getFile(fileResource)

        return if (file != null) {
            val filePart = getFilePart(file)
            val responseBody = apiCallExecutor.executeObjectCall(fileResourceService.uploadFile(filePart))
            handleResponse(responseBody.string(), fileResource, file, value)
        } else {
            handleMissingFile(fileResource, value)
            null
        }
    }

    private fun getFilePart(file: File): MultipartBody.Part {
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/*"

        return MultipartBody.Part
            .createFormData("file", file.name, RequestBody.create(MediaType.parse(type), file))
    }

    private fun handleResponse(
        responseBody: String,
        fileResource: FileResource,
        file: File,
        value: FileResourceValue
    ): String {
        try {
            val downloadedFileResource = getDownloadedFileResource(responseBody)
            updateValue(fileResource, downloadedFileResource.uid(), value)

            val downloadedFile = FileResourceUtil.renameFile(file, downloadedFileResource.uid()!!, context)
            updateFileResource(fileResource, downloadedFileResource, downloadedFile)

            return downloadedFileResource.uid()!!
        } catch (e: IOException) {
            Log.v(FileResourcePostCall::class.java.canonicalName, e.message!!)
            throw D2Error.builder()
                .errorCode(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE)
                .errorDescription(e.message!!)
                .build()
        }
    }

    private fun handleMissingFile(fileResource: FileResource, value: FileResourceValue) {
        if (fileResource.uid() != null) {
            updateValue(fileResource, null, value)
            fileResourceStore.deleteIfExists(fileResource.uid()!!)
        }
    }

    @Throws(IOException::class)
    private fun getDownloadedFileResource(responseBody: String): FileResource {
        val fileResourceResponse = objectMapper().readValue(responseBody, FileResourceResponse::class.java)
        return fileResourceResponse.response()!!.fileResource()!!
    }

    private fun updateValue(
        fileResource: FileResource,
        newUid: String?,
        value: FileResourceValue
    ) {
        val updateValueMethod = when (value) {
            is FileResourceValue.DataValue -> ::updateAggregatedDataValue
            is FileResourceValue.EventValue -> ::updateTrackedEntityDataValue
            is FileResourceValue.AttributeValue -> ::updateTrackedEntityAttributeValue
        }

        updateValueMethod(fileResource, newUid, value.uid)
    }

    private fun updateAggregatedDataValue(fileResource: FileResource, newUid: String?, elementUid: String) {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(DataValueTableInfo.Columns.VALUE, fileResource.uid())
            .appendKeyStringValue(DataValueTableInfo.Columns.DATA_ELEMENT, elementUid)
            .build()

        dataValueStore.selectOneWhere(whereClause)?.let { dataValue ->
            val newValue =
                if (newUid == null) dataValue.toBuilder().deleted(true).build()
                else dataValue.toBuilder().value(newUid).build()

            dataValueStore.updateWhere(newValue)
        }
    }

    private fun updateTrackedEntityAttributeValue(
        fileResource: FileResource,
        newUid: String?,
        elementUid: String
    ): Boolean {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(TrackedEntityAttributeValueTableInfo.Columns.VALUE, fileResource.uid())
            .appendKeyStringValue(TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE, elementUid)
            .build()

        return trackedEntityAttributeValueStore.selectOneWhere(whereClause)?.let { attributeValue ->
            trackedEntityAttributeValueStore.updateWhere(
                attributeValue.toBuilder()
                    .value(newUid)
                    .build()
            )
            true
        } ?: false
    }

    private fun updateTrackedEntityDataValue(fileResource: FileResource, newUid: String?, elementUid: String) {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.VALUE, fileResource.uid())
            .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT, elementUid)
            .build()

        trackedEntityDataValueStore.selectOneWhere(whereClause)?.let { dataValue ->
            trackedEntityDataValueStore.updateWhere(
                dataValue.toBuilder()
                    .value(newUid)
                    .build()
            )
        }
    }

    private fun updateFileResource(
        fileResource: FileResource,
        downloadedFileResource: FileResource,
        file: File
    ) {
        fileResourceStore.delete(fileResource.uid()!!)
        fileResourceHandler.handle(
            downloadedFileResource.toBuilder()
                .syncState(State.UPLOADING)
                .path(file.absolutePath)
                .build()
        )
    }
}
