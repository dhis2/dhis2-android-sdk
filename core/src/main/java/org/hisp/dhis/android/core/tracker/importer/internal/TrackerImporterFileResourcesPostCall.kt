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
package org.hisp.dhis.android.core.tracker.importer.internal

import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.internal.FileResourceHelper
import org.hisp.dhis.android.core.fileresource.internal.FileResourcePostCall
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStorageStatusVerifier
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUploadResult
import org.hisp.dhis.android.core.fileresource.internal.FileResourceValue
import org.hisp.dhis.android.core.fileresource.internal.FileResourceVerificationResult
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayload
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayloadWrapper
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
internal class TrackerImporterFileResourcesPostCall internal constructor(
    private val fileResourcePostCall: FileResourcePostCall,
    private val fileResourceHelper: FileResourceHelper,
    private val fileResourceStorageStatusVerifier: FileResourceStorageStatusVerifier,
) {

    suspend fun uploadFileResources(
        payloadWrapper: NewTrackerImporterPayloadWrapper,
    ): NewTrackerImporterPayloadWrapper {
        val fileResources = fileResourceHelper.getUploadableFileResources()

        return if (fileResources.isEmpty()) {
            payloadWrapper
        } else {
            payloadWrapper.copy(
                deleted = uploadPayloadFileResources(payloadWrapper.deleted, fileResources),
                updated = uploadPayloadFileResources(payloadWrapper.updated, fileResources),
            )
        }
    }

    private suspend fun uploadPayloadFileResources(
        payload: NewTrackerImporterPayload,
        fileResources: List<FileResource>,
    ): NewTrackerImporterPayload {
        val uploadedAttributes = uploadAttributes(payload.trackedEntities, payload.enrollments, fileResources)
        val uploadedDataValues = uploadDataValues(payload.events, fileResources)

        return payload.copy(
            trackedEntities = uploadedAttributes.first.toMutableList(),
            enrollments = uploadedAttributes.second.toMutableList(),
            events = uploadedDataValues.first.toMutableList(),
            fileResourcesMap = uploadedAttributes.third + uploadedDataValues.second,
        )
    }

    private suspend fun uploadAttributes(
        entities: List<NewTrackerImporterTrackedEntity>,
        enrollments: List<NewTrackerImporterEnrollment>,
        fileResources: List<FileResource>,
    ): Triple<List<NewTrackerImporterTrackedEntity>, List<NewTrackerImporterEnrollment>, Map<String, List<String>>> {
        // Map from original uid to new uid (the uid from the server)
        val uploadedFileResourcesMap = mutableMapOf<String, FileResource>()

        // Map from entity/enrollment uid to the list of associated fileResources
        val fileResourcesByEntity = mutableMapOf<String, List<String>>()

        val successfulEntities: List<NewTrackerImporterTrackedEntity> = entities.mapNotNull { entity ->
            catchErrorToNull {
                val updatedAttributes = getUpdatedAttributes(
                    entityUid = entity.uid(),
                    attributeValues = entity.trackedEntityAttributeValues,
                    fileResources = fileResources,
                    uploadedFileResources = uploadedFileResourcesMap,
                    fileResourcesByEntity = fileResourcesByEntity,
                )
                entity.copy(trackedEntityAttributeValues = updatedAttributes)
            }
        }

        val successfulEnrollments: List<NewTrackerImporterEnrollment> = enrollments.mapNotNull { enrollment ->
            catchErrorToNull {
                val updatedAttributes = getUpdatedAttributes(
                    entityUid = enrollment.uid(),
                    attributeValues = enrollment.attributes,
                    fileResources = fileResources,
                    uploadedFileResources = uploadedFileResourcesMap,
                    fileResourcesByEntity = fileResourcesByEntity,
                )
                enrollment.copy(attributes = updatedAttributes)
            }
        }

        return Triple(successfulEntities, successfulEnrollments, fileResourcesByEntity)
    }

    private suspend fun getUpdatedAttributes(
        entityUid: String,
        attributeValues: List<NewTrackerImporterTrackedEntityAttributeValue>?,
        fileResources: List<FileResource>,
        uploadedFileResources: MutableMap<String, FileResource>,
        fileResourcesByEntity: MutableMap<String, List<String>>,
    ): List<NewTrackerImporterTrackedEntityAttributeValue>? {
        val entityFileResources = mutableListOf<String>()

        // First pass: collect all file resources that need to be uploaded
        val uploadResults =
            mutableListOf<Pair<NewTrackerImporterTrackedEntityAttributeValue, FileResourceUploadResult>>()

        attributeValues?.forEach { attributeValue ->
            fileResourceHelper.findAttributeFileResource(attributeValue, fileResources)?.let { fileResource ->
                val uploadedFileResource = uploadedFileResources[fileResource.uid()]

                if (uploadedFileResource == null) {
                    // Upload without updating value immediately
                    val fValue = FileResourceValue.AttributeValue(attributeValue.trackedEntityAttribute!!)
                    val uploadResult = fileResourcePostCall.uploadFileResourceWithoutUpdate(fileResource, fValue)
                    uploadResults.add(attributeValue to uploadResult)
                }
            }
        }

        // Verify storage status in batch
        val uidsToVerify = uploadResults.mapNotNull { it.second.uploadedUid }
        val verificationResults = if (uidsToVerify.isNotEmpty()) {
            fileResourceStorageStatusVerifier.verifyStorageStatusBatch(uidsToVerify)
        } else {
            emptyMap()
        }

        // Update uploadedFileResources map with verified files
        uploadResults.forEach { (_, uploadResult) ->
            uploadResult.uploadedUid?.let { uploadedUid ->
                val verificationResult = verificationResults[uploadedUid]
                if (verificationResult?.isVerified == true) {
                    uploadedFileResources[uploadResult.originalFileResource.uid()!!] =
                        uploadResult.originalFileResource.toBuilder().uid(uploadedUid).build()

                    // Update the value now that we've verified the file is stored
                    fileResourcePostCall.updateValueAfterVerification(
                        uploadResult.originalFileResource,
                        uploadedUid,
                        uploadResult.value,
                    )
                }
            }
        }

        // Second pass: build updated attributes with verified UIDs
        val updatedAttributes = attributeValues?.map { attributeValue ->
            fileResourceHelper.findAttributeFileResource(attributeValue, fileResources)?.let { fileResource ->
                val uploadedFileResource = uploadedFileResources[fileResource.uid()]
                val newUid = uploadedFileResource?.uid()

                newUid?.let { entityFileResources.add(it) }
                attributeValue.copy(value = newUid)
            } ?: attributeValue
        }

        if (entityFileResources.isNotEmpty()) {
            fileResourcesByEntity[entityUid] = entityFileResources
        }

        return updatedAttributes
    }

    private suspend fun uploadDataValues(
        events: List<NewTrackerImporterEvent>,
        fileResources: List<FileResource>,
    ): Pair<List<NewTrackerImporterEvent>, Map<String, List<String>>> {
        val eventUploadResults = uploadEventFileResources(events, fileResources)
        val verificationResults = verifyEventFileResources(eventUploadResults)
        updateVerifiedEventValues(eventUploadResults, verificationResults)

        val uploadedFileResources = mutableMapOf<String, List<String>>()
        val successfulEvents = buildUpdatedEvents(
            events,
            fileResources,
            eventUploadResults,
            verificationResults,
            uploadedFileResources,
        )

        return Pair(successfulEvents, uploadedFileResources)
    }

    private suspend fun uploadEventFileResources(
        events: List<NewTrackerImporterEvent>,
        fileResources: List<FileResource>,
    ): Map<String, List<FileResourceUploadResult>> {
        val eventUploadResults = mutableMapOf<String, List<FileResourceUploadResult>>()

        events.forEach { event ->
            val uploadResults = mutableListOf<FileResourceUploadResult>()
            event.trackedEntityDataValues?.forEach { dataValue ->
                fileResourceHelper.findDataValueFileResource(dataValue, fileResources)?.let { fileResource ->
                    val fValue = FileResourceValue.EventValue(dataValue.dataElement!!)
                    val uploadResult = fileResourcePostCall.uploadFileResourceWithoutUpdate(fileResource, fValue)
                    uploadResults.add(uploadResult)
                }
            }
            if (uploadResults.isNotEmpty()) {
                eventUploadResults[event.uid()] = uploadResults
            }
        }

        return eventUploadResults
    }

    private suspend fun verifyEventFileResources(
        eventUploadResults: Map<String, List<FileResourceUploadResult>>,
    ): Map<String, FileResourceVerificationResult> {
        val allUidsToVerify = eventUploadResults.values.flatten().mapNotNull { it.uploadedUid }
        return if (allUidsToVerify.isNotEmpty()) {
            fileResourceStorageStatusVerifier.verifyStorageStatusBatch(allUidsToVerify)
        } else {
            emptyMap()
        }
    }

    private suspend fun updateVerifiedEventValues(
        eventUploadResults: Map<String, List<FileResourceUploadResult>>,
        verificationResults: Map<String, FileResourceVerificationResult>,
    ) {
        eventUploadResults.forEach { (_, uploadResults) ->
            uploadResults.forEach { uploadResult ->
                val uploadedUid = uploadResult.uploadedUid ?: return@forEach
                val verificationResult = verificationResults[uploadedUid]
                if (verificationResult?.isVerified == true) {
                    fileResourcePostCall.updateValueAfterVerification(
                        uploadResult.originalFileResource,
                        uploadedUid,
                        uploadResult.value,
                    )
                }
            }
        }
    }

    private suspend fun buildUpdatedEvents(
        events: List<NewTrackerImporterEvent>,
        fileResources: List<FileResource>,
        eventUploadResults: Map<String, List<FileResourceUploadResult>>,
        verificationResults: Map<String, FileResourceVerificationResult>,
        uploadedFileResources: MutableMap<String, List<String>>,
    ): List<NewTrackerImporterEvent> {
        return events.mapNotNull { event ->
            catchErrorToNull {
                val eventFileResources = mutableListOf<String>()
                val updatedDataValues = event.trackedEntityDataValues?.map { dataValue ->
                    fileResourceHelper.findDataValueFileResource(dataValue, fileResources)?.let { fileResource ->
                        val uploadResult = eventUploadResults[event.uid()]?.find {
                            it.originalFileResource.uid() == fileResource.uid()
                        }
                        val newUid = getVerifiedUidForEvent(uploadResult, verificationResults, eventFileResources)
                        dataValue.copy(value = newUid)
                    } ?: dataValue
                }
                if (eventFileResources.isNotEmpty()) {
                    uploadedFileResources[event.uid()] = eventFileResources
                }
                event.copy(trackedEntityDataValues = updatedDataValues)
            }
        }
    }

    private fun getVerifiedUidForEvent(
        uploadResult: FileResourceUploadResult?,
        verificationResults: Map<String, FileResourceVerificationResult>,
        eventFileResources: MutableList<String>,
    ): String? {
        val uploadedUid = uploadResult?.uploadedUid ?: return null
        val verificationResult = verificationResults[uploadedUid]
        return if (verificationResult?.isVerified == true) {
            eventFileResources.add(uploadedUid)
            uploadedUid
        } else {
            null
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun <T> catchErrorToNull(f: suspend () -> T): T? {
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
