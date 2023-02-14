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
package org.hisp.dhis.android.core.tracker.importer.internal

import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.internal.FileResourceHelper
import org.hisp.dhis.android.core.fileresource.internal.FileResourcePostCall
import org.hisp.dhis.android.core.fileresource.internal.FileResourceValue
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayload
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayloadWrapper

@Reusable
internal class TrackerImporterFileResourcesPostCall @Inject internal constructor(
    private val fileResourcePostCall: FileResourcePostCall,
    private val fileResourceHelper: FileResourceHelper
) {

    fun uploadFileResources(
        payloadWrapper: NewTrackerImporterPayloadWrapper
    ): Single<NewTrackerImporterPayloadWrapper> {
        return Single.create { emitter ->
            val fileResources = fileResourceHelper.getUploadableFileResources()

            if (fileResources.isEmpty()) {
                emitter.onSuccess(payloadWrapper)
            } else {
                emitter.onSuccess(
                    payloadWrapper.copy(
                        deleted = uploadPayloadFileResources(payloadWrapper.deleted, fileResources),
                        updated = uploadPayloadFileResources(payloadWrapper.updated, fileResources)
                    )
                )
            }
        }
    }

    private fun uploadPayloadFileResources(
        payload: NewTrackerImporterPayload,
        fileResources: List<FileResource>
    ): NewTrackerImporterPayload {
        val uploadedAttributes = uploadAttributes(payload.trackedEntities, payload.enrollments, fileResources)
        val uploadedDataValues = uploadDataValues(payload.events, fileResources)

        return payload.copy(
            trackedEntities = uploadedAttributes.first.toMutableList(),
            enrollments = uploadedAttributes.second.toMutableList(),
            events = uploadedDataValues.first.toMutableList(),
            fileResourcesMap = uploadedAttributes.third + uploadedDataValues.second
        )
    }

    private fun uploadAttributes(
        entities: List<NewTrackerImporterTrackedEntity>,
        enrollments: List<NewTrackerImporterEnrollment>,
        fileResources: List<FileResource>
    ): Triple<List<NewTrackerImporterTrackedEntity>, List<NewTrackerImporterEnrollment>, Map<String, List<String>>> {
        // Map from original uid to new uid (the uid from the server)
        val uploadedFileResourcesMap = mutableMapOf<String, FileResource>()

        // Map from entity/enrollment uid to the list of associated fileResources
        val fileResourcesByEntity = mutableMapOf<String, List<String>>()

        val successfulEntities: List<NewTrackerImporterTrackedEntity> = entities.mapNotNull { entity ->
            catchErrorToNull {
                val updatedAttributes = getUpdatedAttributes(
                    entityUid = entity.uid()!!,
                    attributeValues = entity.trackedEntityAttributeValues(),
                    fileResources = fileResources,
                    uploadedFileResources = uploadedFileResourcesMap,
                    fileResourcesByEntity = fileResourcesByEntity
                )
                entity.toBuilder().trackedEntityAttributeValues(updatedAttributes).build()
            }
        }

        val successfulEnrollments: List<NewTrackerImporterEnrollment> = enrollments.mapNotNull { enrollment ->
            catchErrorToNull {
                val updatedAttributes = getUpdatedAttributes(
                    entityUid = enrollment.uid()!!,
                    attributeValues = enrollment.attributes(),
                    fileResources = fileResources,
                    uploadedFileResources = uploadedFileResourcesMap,
                    fileResourcesByEntity = fileResourcesByEntity
                )
                enrollment.toBuilder().attributes(updatedAttributes).build()
            }
        }

        return Triple(successfulEntities, successfulEnrollments, fileResourcesByEntity)
    }

    private fun getUpdatedAttributes(
        entityUid: String,
        attributeValues: List<NewTrackerImporterTrackedEntityAttributeValue>?,
        fileResources: List<FileResource>,
        uploadedFileResources: MutableMap<String, FileResource>,
        fileResourcesByEntity: MutableMap<String, List<String>>
    ): List<NewTrackerImporterTrackedEntityAttributeValue>? {
        val entityFileResources = mutableListOf<String>()
        val updatedAttributes = attributeValues?.map { attributeValue ->
            fileResourceHelper.findAttributeFileResource(attributeValue, fileResources)?.let { fileResource ->
                val uploadedFileResource = uploadedFileResources[fileResource.uid()]

                val newUid = if (uploadedFileResource != null) {
                    uploadedFileResource.uid()!!
                } else {
                    val fValue = FileResourceValue.AttributeValue(attributeValue.trackedEntityAttribute()!!)
                    fileResourcePostCall.uploadFileResource(fileResource, fValue)?.also {
                        uploadedFileResources[fileResource.uid()!!] = fileResource.toBuilder().uid(it).build()
                    }
                }
                newUid?.let { entityFileResources.add(it) }
                attributeValue.toBuilder().value(newUid).build()
            } ?: attributeValue
        }

        if (entityFileResources.isNotEmpty()) {
            fileResourcesByEntity[entityUid] = entityFileResources
        }

        return updatedAttributes
    }

    private fun uploadDataValues(
        events: List<NewTrackerImporterEvent>,
        fileResources: List<FileResource>
    ): Pair<List<NewTrackerImporterEvent>, Map<String, List<String>>> {
        val uploadedFileResources = mutableMapOf<String, List<String>>()

        val successfulEvents = events.mapNotNull { event ->
            catchErrorToNull {
                val eventFileResources = mutableListOf<String>()
                val updatedDataValues = event.trackedEntityDataValues()?.map { dataValue ->
                    fileResourceHelper.findDataValueFileResource(dataValue, fileResources)?.let { fileResource ->
                        val fValue = FileResourceValue.EventValue(dataValue.dataElement()!!)
                        val newUid = fileResourcePostCall.uploadFileResource(fileResource, fValue)?.also {
                            eventFileResources.add(it)
                        }
                        dataValue.toBuilder().value(newUid).build()
                    } ?: dataValue
                }
                if (eventFileResources.isNotEmpty()) {
                    uploadedFileResources[event.uid()!!] = eventFileResources
                }
                event.toBuilder().trackedEntityDataValues(updatedDataValues).build()
            }
        }

        return Pair(successfulEvents, uploadedFileResources)
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
