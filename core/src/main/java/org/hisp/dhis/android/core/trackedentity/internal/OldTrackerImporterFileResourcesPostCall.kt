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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.internal.FileResourceHelper
import org.hisp.dhis.android.core.fileresource.internal.FileResourcePostCall
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor

@Reusable
internal class OldTrackerImporterFileResourcesPostCall @Inject internal constructor(
    private val fileResourceStore: IdentifiableDataObjectStore<FileResource>,
    private val fileResourcePostCall: FileResourcePostCall,
    private val fileResourceHelper: FileResourceHelper
) {

    fun uploadTrackedEntityFileResources(
        trackedEntityInstances: List<TrackedEntityInstance>
    ): Single<Pair<List<TrackedEntityInstance>, List<String>>> {
        return Single.create { emitter ->
            val fileResources = fileResourceStore.getUploadableSyncStatesIncludingError()

            if (fileResources.isEmpty()) {
                emitter.onSuccess(Pair(trackedEntityInstances, emptyList()))
            } else {
                val successfulTeis = trackedEntityInstances.mapNotNull {
                    catchErrorToNull { uploadTrackedEntityInstance(it, fileResources) }
                }
                emitter.onSuccess(Pair(successfulTeis.map { it.first }, successfulTeis.flatMap { it.second }))
            }
        }
    }

    fun uploadEventsFileResources(
        events: List<Event>
    ): Single<Pair<List<Event>, List<String>>> {
        return Single.create { emitter ->
            val fileResources = fileResourceStore.getUploadableSyncStatesIncludingError()

            if (fileResources.isEmpty()) {
                emitter.onSuccess(Pair(events, emptyList()))
            } else {
                val successfulEvents = events.mapNotNull {
                    catchErrorToNull { uploadEvent(it, fileResources) }
                }
                emitter.onSuccess(Pair(successfulEvents.map { it.first }, successfulEvents.flatMap { it.second }))
            }
        }
    }

    private fun uploadTrackedEntityInstance(
        trackedEntityInstance: TrackedEntityInstance,
        fileResources: List<FileResource>
    ): Pair<TrackedEntityInstance, List<String>> {
        val uploadedFileResources = mutableListOf<String>()
        val updatedAttributes = trackedEntityInstance.trackedEntityAttributeValues()?.map { attributeValue ->
            fileResources.find {
                it.uid() == attributeValue.value() &&
                    fileResourceHelper.isFileAttribute(attributeValue.trackedEntityAttribute())
            }?.let { fileResource ->
                val newUid = fileResourcePostCall.uploadFileResource(fileResource)
                newUid?.let { uploadedFileResources.add(newUid) }
                attributeValue.toBuilder().value(newUid).build()
            } ?: attributeValue
        }

        val updatedEnrollments = TrackedEntityInstanceInternalAccessor.accessEnrollments(trackedEntityInstance)
            .map { enrollment ->
                uploadEnrollment(enrollment, fileResources)
                    .also { uploadedFileResources.addAll(it.second) }
                    .first
            }

        return Pair(
            TrackedEntityInstanceInternalAccessor
                .insertEnrollments(trackedEntityInstance.toBuilder(), updatedEnrollments)
                .trackedEntityAttributeValues(updatedAttributes)
                .build(),
            uploadedFileResources
        )
    }

    private fun uploadEnrollment(
        enrollment: Enrollment,
        fileResources: List<FileResource>
    ): Pair<Enrollment, List<String>> {
        val uploadedFileResources = mutableListOf<String>()
        val updatedEvents = EnrollmentInternalAccessor.accessEvents(enrollment)
            .map { event ->
                uploadEvent(event, fileResources)
                    .also { uploadedFileResources.addAll(it.second) }
                    .first
            }

        return Pair(
            EnrollmentInternalAccessor.insertEvents(enrollment.toBuilder(), updatedEvents).build(),
            uploadedFileResources
        )
    }

    private fun uploadEvent(
        event: Event,
        fileResources: List<FileResource>
    ): Pair<Event, List<String>> {
        val uploadedFileResources = mutableListOf<String>()
        val updatedDataValues = event.trackedEntityDataValues()?.map { dataValue ->
            fileResources.find {
                it.uid() == dataValue.value() && fileResourceHelper.isFileDataElement(dataValue.dataElement())
            }?.let { fileResource ->
                val newUid = fileResourcePostCall.uploadFileResource(fileResource)
                newUid?.let { uploadedFileResources.add(newUid) }
                dataValue.toBuilder().value(newUid).build()
            } ?: dataValue
        }

        return Pair(
            event.toBuilder().trackedEntityDataValues(updatedDataValues).build(),
            uploadedFileResources
        )
    }

    @Suppress("TooGenericExceptionCaught")
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
