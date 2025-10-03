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
package org.hisp.dhis.android.core.tracker.importer

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.internal.FileResourceHelper
import org.hisp.dhis.android.core.fileresource.internal.FileResourcePostCall
import org.hisp.dhis.android.core.fileresource.internal.FileResourceValue
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayload
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayloadWrapper
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterFileResourcesPostCall
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TrackerImporterFileResourcesPostCallShould {

    private val fileResourcesPostCall: FileResourcePostCall = mock()
    private val fileResourceHelper: FileResourceHelper = mock()

    private lateinit var fileResourcePostCall: TrackerImporterFileResourcesPostCall

    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("LongMethod")
    @Test
    fun `Should create a single post call for repeated attributes`() = runTest {
        val resourceId = "resourceId"
        val attributeId = "attributeId"

        val attributeValue = NewTrackerImporterTrackedEntityAttributeValue(
            trackedEntityAttribute = attributeId,
            value = resourceId,
            createdAt = null,
            updatedAt = null,
            trackedEntityInstance = null,
        )

        val fileResource = FileResource.builder().uid(resourceId).build()
        val fileResources = listOf(fileResource)

        val payloadWrapper = NewTrackerImporterPayloadWrapper(
            updated = NewTrackerImporterPayload(
                trackedEntities = mutableListOf(
                    NewTrackerImporterTrackedEntity(
                        uid = "tei_uid",
                        trackedEntityAttributeValues = listOf(attributeValue),
                        deleted = null,
                        syncState = null,
                        createdAt = null,
                        updatedAt = null,
                        createdAtClient = null,
                        updatedAtClient = null,
                        organisationUnit = null,
                        trackedEntityType = null,
                        geometry = null,
                        aggregatedSyncState = null,
                        enrollments = null,
                        programOwners = null,
                        relationships = null,
                    ),
                ),
                enrollments = mutableListOf(
                    NewTrackerImporterEnrollment(
                        uid = "enrollment_uid",
                        attributes = listOf(attributeValue),
                        deleted = null,
                        syncState = null,
                        createdAt = null,
                        updatedAt = null,
                        createdAtClient = null,
                        updatedAtClient = null,
                        organisationUnit = null,
                        program = null,
                        enrolledAt = null,
                        occurredAt = null,
                        completedAt = null,
                        followUp = null,
                        status = null,
                        trackedEntity = null,
                        geometry = null,
                        aggregatedSyncState = null,
                        events = null,
                        notes = null,
                        relationships = null,
                    ),
                ),
            ),
        )

        val fValue = FileResourceValue.AttributeValue(attributeValue.trackedEntityAttribute!!)
        whenever(fileResourceHelper.getUploadableFileResources()).doReturn(fileResources)
        whenever(fileResourceHelper.findAttributeFileResource(attributeValue, fileResources)).doReturn(fileResource)
        whenever(fileResourcesPostCall.uploadFileResource(fileResource, fValue))
            .doReturn(Math.random().toString())

        val result = fileResourcePostCall.uploadFileResources(payloadWrapper)

        verify(fileResourcesPostCall, times(1)).uploadFileResource(fileResource, fValue)

        val entityValue = result.updated.trackedEntities.first().trackedEntityAttributeValues!!.first().value!!
        val enrollmentValue = result.updated.enrollments.first().attributes!!.first().value!!

        assertThat(entityValue).isEqualTo(enrollmentValue)
        assertThat(entityValue).isNotEqualTo(resourceId)
    }

    @Before
    fun setUp() {
        fileResourcePostCall = TrackerImporterFileResourcesPostCall(
            fileResourcesPostCall,
            fileResourceHelper,
        )
    }
}
