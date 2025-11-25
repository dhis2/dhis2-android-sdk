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
package org.hisp.dhis.android.core.trackedentity.internal

import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.FileResourceDataDomainType
import org.hisp.dhis.android.core.fileresource.internal.FileResourceHelper
import org.hisp.dhis.android.core.fileresource.internal.FileResourcePostCall
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStorageStatusVerifier
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUploadResult
import org.hisp.dhis.android.core.fileresource.internal.FileResourceValue
import org.hisp.dhis.android.core.fileresource.internal.FileResourceVerificationResult
import org.hisp.dhis.android.core.imports.internal.ItemsWithFileResources
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
internal class OldTrackerImporterFileResourcesPostCall internal constructor(
    private val fileResourcePostCall: FileResourcePostCall,
    private val fileResourceHelper: FileResourceHelper,
    private val fileResourceStorageStatusVerifier: FileResourceStorageStatusVerifier,
) {

    suspend fun uploadTrackedEntityFileResources(
        trackedEntityInstances: List<TrackedEntityInstance>,
    ): ItemsWithFileResources<TrackedEntityInstance> {
        val fileResources = fileResourceHelper.getUploadableFileResources()

        return if (fileResources.isEmpty()) {
            ItemsWithFileResources(trackedEntityInstances, emptyList())
        } else {
            val successfulTeis = trackedEntityInstances.mapNotNull {
                catchErrorToNull { uploadTrackedEntityInstance(it, fileResources) }
            }

            ItemsWithFileResources(successfulTeis.map { it.first }, successfulTeis.flatMap { it.second })
        }
    }

    suspend fun uploadEventsFileResources(
        events: List<Event>,
    ): ItemsWithFileResources<Event> {
        val fileResources = fileResourceHelper.getUploadableFileResources()

        return if (fileResources.isEmpty()) {
            ItemsWithFileResources(events, emptyList())
        } else {
            val successfulEvents = events.mapNotNull {
                catchErrorToNull { uploadEvent(it, fileResources) }
            }

            ItemsWithFileResources(successfulEvents.map { it.first }, successfulEvents.flatMap { it.second })
        }
    }

    private suspend fun uploadTrackedEntityInstance(
        trackedEntityInstance: TrackedEntityInstance,
        fileResources: List<FileResource>,
    ): Pair<TrackedEntityInstance, List<String>> {
        val attributeUploadResults = uploadAttributeFileResources(trackedEntityInstance, fileResources)
        val enrollmentResults = uploadEnrollmentFileResources(trackedEntityInstance, fileResources)

        val verificationResults = verifyAllUploadedFiles(attributeUploadResults, enrollmentResults)
        updateVerifiedAttributeValues(attributeUploadResults, verificationResults)

        val uploadedFileResources = mutableListOf<String>()
        val updatedAttributes = buildUpdatedAttributes(
            trackedEntityInstance,
            fileResources,
            attributeUploadResults,
            verificationResults,
            uploadedFileResources,
        )
        val updatedEnrollments = buildUpdatedEnrollments(
            enrollmentResults,
            verificationResults,
            uploadedFileResources,
        )

        return Pair(
            TrackedEntityInstanceInternalAccessor
                .insertEnrollments(trackedEntityInstance.toBuilder(), updatedEnrollments)
                .trackedEntityAttributeValues(updatedAttributes)
                .build(),
            uploadedFileResources,
        )
    }

    private suspend fun uploadAttributeFileResources(
        trackedEntityInstance: TrackedEntityInstance,
        fileResources: List<FileResource>,
    ): List<FileResourceUploadResult> {
        val uploadResults = mutableListOf<FileResourceUploadResult>()
        trackedEntityInstance.trackedEntityAttributeValues()?.forEach { attributeValue ->
            fileResourceHelper.findAttributeFileResource(attributeValue, fileResources)?.let { fileResource ->
                val fValue = FileResourceValue.AttributeValue(attributeValue.trackedEntityAttribute()!!)
                val uploadResult = fileResourcePostCall.uploadFileResourceWithoutUpdate(fileResource, fValue)
                uploadResults.add(uploadResult)
            }
        }
        return uploadResults
    }

    private suspend fun uploadEnrollmentFileResources(
        trackedEntityInstance: TrackedEntityInstance,
        fileResources: List<FileResource>,
    ): List<Pair<Enrollment, List<String>>> {
        return TrackedEntityInstanceInternalAccessor.accessEnrollments(trackedEntityInstance)
            .map { enrollment -> uploadEnrollment(enrollment, fileResources) }
    }

    private suspend fun verifyAllUploadedFiles(
        attributeUploadResults: List<FileResourceUploadResult>,
        enrollmentResults: List<Pair<Enrollment, List<String>>>,
    ): Map<String, FileResourceVerificationResult> {
        val allUidsToVerify = mutableListOf<String>()
        allUidsToVerify.addAll(attributeUploadResults.mapNotNull { it.uploadedUid })
        enrollmentResults.forEach { allUidsToVerify.addAll(it.second) }

        return if (allUidsToVerify.isNotEmpty()) {
            fileResourceStorageStatusVerifier.verifyStorageStatusBatch(allUidsToVerify)
        } else {
            emptyMap()
        }
    }

    private suspend fun updateVerifiedAttributeValues(
        attributeUploadResults: List<FileResourceUploadResult>,
        verificationResults: Map<String, FileResourceVerificationResult>,
    ) {
        attributeUploadResults.forEach { uploadResult ->
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

    private suspend fun buildUpdatedAttributes(
        trackedEntityInstance: TrackedEntityInstance,
        fileResources: List<FileResource>,
        attributeUploadResults: List<FileResourceUploadResult>,
        verificationResults: Map<String, FileResourceVerificationResult>,
        uploadedFileResources: MutableList<String>,
    ): List<TrackedEntityAttributeValue>? {
        return trackedEntityInstance.trackedEntityAttributeValues()?.map { attributeValue ->
            fileResourceHelper.findAttributeFileResource(attributeValue, fileResources)?.let { fileResource ->
                val uploadResult = attributeUploadResults.find {
                    it.originalFileResource.uid() == fileResource.uid()
                }
                val newUid = getVerifiedUidForAttribute(uploadResult, verificationResults, uploadedFileResources)
                attributeValue.toBuilder().value(newUid).build()
            } ?: attributeValue
        }
    }

    private fun getVerifiedUidForAttribute(
        uploadResult: FileResourceUploadResult?,
        verificationResults: Map<String, FileResourceVerificationResult>,
        uploadedFileResources: MutableList<String>,
    ): String? {
        val uploadedUid = uploadResult?.uploadedUid ?: return null
        val verificationResult = verificationResults[uploadedUid]
        return if (verificationResult?.isVerified == true) {
            uploadedFileResources.add(uploadedUid)
            uploadedUid
        } else {
            null
        }
    }

    private fun buildUpdatedEnrollments(
        enrollmentResults: List<Pair<Enrollment, List<String>>>,
        verificationResults: Map<String, FileResourceVerificationResult>,
        uploadedFileResources: MutableList<String>,
    ): List<Enrollment> {
        return enrollmentResults.map { (enrollment, fileResourceUids) ->
            fileResourceUids.forEach { uid ->
                val verificationResult = verificationResults[uid]
                if (verificationResult?.isVerified == true) {
                    uploadedFileResources.add(uid)
                }
            }
            enrollment
        }
    }

    private suspend fun uploadEnrollment(
        enrollment: Enrollment,
        fileResources: List<FileResource>,
    ): Pair<Enrollment, List<String>> {
        val eventResults = EnrollmentInternalAccessor.accessEvents(enrollment)
            .map { event -> uploadEvent(event, fileResources) }

        val allUploadedUids = eventResults.flatMap { it.second }
        val updatedEvents = eventResults.map { it.first }

        return Pair(
            EnrollmentInternalAccessor.insertEvents(enrollment.toBuilder(), updatedEvents).build(),
            allUploadedUids,
        )
    }

    private suspend fun uploadEvent(
        event: Event,
        fileResources: List<FileResource>,
    ): Pair<Event, List<String>> {
        // Upload file resources without updating values
        val uploadResults = mutableListOf<FileResourceUploadResult>()
        event.trackedEntityDataValues()?.forEach { dataValue ->
            fileResourceHelper.findDataValueFileResource(dataValue, fileResources)?.let { fileResource ->
                val fValue = FileResourceValue.EventValue(dataValue.dataElement()!!)
                val uploadResult = fileResourcePostCall.uploadFileResourceWithoutUpdate(fileResource, fValue)
                uploadResults.add(uploadResult)
            }
        }

        // Collect uploaded UIDs (verification will be done at TEI level)
        val uploadedUids = uploadResults.mapNotNull { it.uploadedUid }

        // Build event with uploaded UIDs (values will be updated after verification)
        val updatedDataValues = event.trackedEntityDataValues()?.map { dataValue ->
            fileResourceHelper.findDataValueFileResource(dataValue, fileResources)?.let { fileResource ->
                val uploadResult = uploadResults.find {
                    it.originalFileResource.uid() == fileResource.uid()
                }
                val newUid = uploadResult?.uploadedUid
                dataValue.toBuilder().value(newUid).build()
            } ?: dataValue
        }

        return Pair(
            event.toBuilder().trackedEntityDataValues(updatedDataValues).build(),
            uploadedUids,
        )
    }

    suspend fun updateFileResourceStates(fileResources: List<String>) {
        fileResourceHelper.updateFileResourceStates(fileResources, FileResourceDataDomainType.TRACKER)
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
