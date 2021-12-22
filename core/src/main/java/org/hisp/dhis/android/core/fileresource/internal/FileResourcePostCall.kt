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
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory.objectMapper
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import java.io.File
import java.io.IOException
import javax.inject.Inject

@Reusable
internal class FileResourcePostCall @Inject constructor(
    private val fileResourceService: FileResourceService,
    private val apiCallExecutor: APICallExecutor,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore,
    private val fileResourceStore: IdentifiableDataObjectStore<FileResource>,
    private val fileResourceHandler: HandlerWithTransformer<FileResource>,
    private val context: Context
) {
    fun uploadFileResources(filteredFileResources: List<FileResource>): Observable<D2Progress> {
        return Observable.empty<D2Progress>()
    }

    fun uploadTrackedEntityFileResources(
        trackedEntityInstances: List<TrackedEntityInstance>
    ): Single<List<TrackedEntityInstance>> {
        return Single.create { emitter ->
            val fileResources = getPendingFileResources()

            if (fileResources.isEmpty()) {
                emitter.onSuccess(trackedEntityInstances)
            } else {
                val successfulTeis = trackedEntityInstances.mapNotNull {
                    catchErrorToNull { uploadTrackedEntityInstance(it, fileResources) }
                }
                emitter.onSuccess(successfulTeis)
            }
        }
    }

    fun uploadEventsFileResources(
        events: List<Event>
    ): Single<List<Event>> {
        return Single.create { emitter ->
            val fileResources = getPendingFileResources()

            if (fileResources.isEmpty()) {
                emitter.onSuccess(events)
            } else {
                val successfulEvents = events.mapNotNull {
                    catchErrorToNull { uploadEvent(it, fileResources) }
                }
                emitter.onSuccess(successfulEvents)
            }
        }
    }

    private fun uploadTrackedEntityInstance(
        trackedEntityInstance: TrackedEntityInstance,
        fileResources: List<FileResource>
    ): TrackedEntityInstance? {
        val updatedAttributes = trackedEntityInstance.trackedEntityAttributeValues()?.map { attributeValue ->
            fileResources.find { it.uid() == attributeValue.value() }?.let { fileResource ->
                val newUid = uploadFileResource(fileResource)
                attributeValue.toBuilder().value(newUid).build()
            } ?: attributeValue
        }

        val updatedEnrollments = TrackedEntityInstanceInternalAccessor.accessEnrollments(trackedEntityInstance)
            .map { uploadEnrollment(it, fileResources) }

        return TrackedEntityInstanceInternalAccessor
            .insertEnrollments(trackedEntityInstance.toBuilder(), updatedEnrollments)
            .trackedEntityAttributeValues(updatedAttributes)
            .build()
    }

    private fun uploadEnrollment(
        enrollment: Enrollment,
        fileResources: List<FileResource>
    ): Enrollment {
        val updatedEvents = EnrollmentInternalAccessor.accessEvents(enrollment)
            .map { uploadEvent(it, fileResources) }

        return EnrollmentInternalAccessor
            .insertEvents(enrollment.toBuilder(), updatedEvents)
            .build()
    }

    private fun uploadEvent(
        event: Event,
        fileResources: List<FileResource>
    ): Event {
        val updatedDataValues = event.trackedEntityDataValues()?.map { dataValue ->
            // TODO Filter by value type
            fileResources.find { it.uid() == dataValue.value() }?.let { fileResource ->
                val newUid = uploadFileResource(fileResource)
                dataValue.toBuilder().value(newUid).build()
            } ?: dataValue
        }

        return event.toBuilder()
            .trackedEntityDataValues(updatedDataValues)
            .build()
    }

    private fun uploadFileResource(fileResource: FileResource): String {
        val file = getRelatedFile(fileResource)
        val filePart = getFilePart(file)

        val responseBody = apiCallExecutor.executeObjectCall(fileResourceService.uploadFile(filePart))

        return handleResponse(responseBody.string(), fileResource, file)
    }

    private fun getPendingFileResources(): List<FileResource> {
        val query = WhereClauseBuilder()
            .appendInKeyStringValues(
                DataColumns.SYNC_STATE,
                EnumHelper.asStringList(State.uploadableStatesIncludingError().toList())
            ).build()

        return fileResourceStore.selectWhere(query)
    }

    @Throws(D2Error::class)
    private fun getRelatedFile(fileResource: FileResource): File {
        return FileResourceUtil.getFile(context, fileResource)
    }

    private fun getFilePart(file: File): MultipartBody.Part {
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/*"

        return MultipartBody.Part
            .createFormData("file", file.name, RequestBody.create(MediaType.parse(type), file))
    }

    private fun handleResponse(responseBody: String, fileResource: FileResource, file: File): String {
        try {
            val downloadedFileResource = getDownloadedFileResource(responseBody)
            updateValue(fileResource, downloadedFileResource)

            val downloadedFile = updateFile(file, downloadedFileResource, context)
            updateFileResource(fileResource, downloadedFileResource, downloadedFile)

            return downloadedFileResource.uid()!!
        } catch (e: IOException) {
            Log.v(FileResourcePostCall::class.java.canonicalName, e.message!!)
            throw RuntimeException("Resource cannot be handled")
        }
    }

    @Throws(IOException::class)
    private fun getDownloadedFileResource(responseBody: String): FileResource {
        val fileResourceResponse = objectMapper().readValue(responseBody, FileResourceResponse::class.java)
        return fileResourceResponse.response()!!.fileResource()!!
    }

    private fun updateValue(fileResource: FileResource, downloadedFileResource: FileResource) {
        if (!updateTrackedEntityAttributeValue(fileResource, downloadedFileResource)) {
            updateTrackedEntityDataValue(fileResource, downloadedFileResource)
        }
    }

    private fun updateTrackedEntityAttributeValue(
        fileResource: FileResource,
        downloadedFileResource: FileResource
    ): Boolean {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(TrackedEntityAttributeValueTableInfo.Columns.VALUE, fileResource.uid())
            .build()

        return trackedEntityAttributeValueStore.selectOneWhere(whereClause)?.let { attributeValue ->
            trackedEntityAttributeValueStore.updateWhere(
                attributeValue.toBuilder()
                    .value(downloadedFileResource.uid())
                    .build()
            )
            true
        } ?: false
    }

    private fun updateTrackedEntityDataValue(fileResource: FileResource, downloadedFileResource: FileResource) {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.VALUE, fileResource.uid())
            .build()

        trackedEntityDataValueStore.selectOneWhere(whereClause)?.let { dataValue ->
            trackedEntityDataValueStore.updateWhere(
                dataValue.toBuilder()
                    .value(downloadedFileResource.uid())
                    .build()
            )
        }
    }

    private fun updateFile(file: File, fileResource: FileResource?, context: Context): File {
        return FileResourceUtil.renameFile(file, fileResource!!.uid(), context)
    }

    private fun updateFileResource(fileResource: FileResource, downloadedFileResource: FileResource, file: File) {
        fileResourceStore.delete(fileResource.uid()!!)
        fileResourceHandler.handle(
            downloadedFileResource.toBuilder()
                .syncState(State.SYNCED)
                .path(file.absolutePath)
                .build()
        )
    }

    private fun <T> catchErrorToNull(f: () -> T): T? {
        return try {
            f()
        } catch (e: java.lang.RuntimeException) {
            null
        } catch (e: RuntimeException) {
            null
        } catch (e: D2Error) {
            null
        }
    }
}