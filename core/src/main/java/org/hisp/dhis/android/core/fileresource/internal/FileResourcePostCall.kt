/*
 *  Copyright (c) 2004-2021, University of Oslo
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
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.systeminfo.internal.PingCall
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore

@Reusable
internal class FileResourcePostCall @Inject constructor(
    private val fileResourceService: FileResourceService,
    private val apiCallExecutor: APICallExecutor,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore,
    private val fileResourceStore: IdentifiableDataObjectStore<FileResource>,
    private val fileResourceHandler: HandlerWithTransformer<FileResource>,
    private val pingCall: PingCall,
    private val context: Context
) {

    private var alreadyPinged = false

    fun uploadFileResource(fileResource: FileResource, successState: State = State.UPLOADING): String? {
        // Workaround for ANDROSDK-1452 (see comments restricted to Contributors).
        if (!alreadyPinged) {
            pingCall.getCompletable(true).blockingAwait()
            alreadyPinged = true
        }

        val file = FileResourceUtil.getFile(context, fileResource)

        return if (file != null) {
            val filePart = getFilePart(file)
            val responseBody = apiCallExecutor.executeObjectCall(fileResourceService.uploadFile(filePart))
            handleResponse(responseBody.string(), fileResource, file, successState)
        } else {
            handleMissingFile(fileResource)
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
        successState: State
    ): String {
        try {
            val downloadedFileResource = getDownloadedFileResource(responseBody)
            updateValue(fileResource, downloadedFileResource)

            val downloadedFile = FileResourceUtil.renameFile(file, downloadedFileResource.uid(), context)
            updateFileResource(fileResource, downloadedFileResource, downloadedFile, successState)

            return downloadedFileResource.uid()!!
        } catch (e: IOException) {
            Log.v(FileResourcePostCall::class.java.canonicalName, e.message!!)
            throw D2Error.builder()
                .errorCode(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE)
                .errorDescription(e.message!!)
                .build()
        }
    }

    private fun handleMissingFile(fileResource: FileResource) {
        fileResource.uid()?.let {
            if (!updateTrackedEntityAttributeValue(fileResource, null)) {
                updateTrackedEntityDataValue(fileResource, null)
            }
            fileResourceStore.deleteIfExists(it)
        }
    }

    @Throws(IOException::class)
    private fun getDownloadedFileResource(responseBody: String): FileResource {
        val fileResourceResponse = objectMapper().readValue(responseBody, FileResourceResponse::class.java)
        return fileResourceResponse.response()!!.fileResource()!!
    }

    private fun updateValue(fileResource: FileResource, downloadedFileResource: FileResource) {
        if (!updateTrackedEntityAttributeValue(fileResource, downloadedFileResource.uid())) {
            updateTrackedEntityDataValue(fileResource, downloadedFileResource.uid())
        }
    }

    private fun updateTrackedEntityAttributeValue(fileResource: FileResource, newUid: String?): Boolean {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(TrackedEntityAttributeValueTableInfo.Columns.VALUE, fileResource.uid())
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

    private fun updateTrackedEntityDataValue(fileResource: FileResource, newUid: String?) {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.VALUE, fileResource.uid())
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
        file: File,
        successState: State
    ) {
        fileResourceStore.delete(fileResource.uid()!!)
        fileResourceHandler.handle(
            downloadedFileResource.toBuilder()
                .syncState(successState)
                .path(file.absolutePath)
                .build()
        )
    }
}
