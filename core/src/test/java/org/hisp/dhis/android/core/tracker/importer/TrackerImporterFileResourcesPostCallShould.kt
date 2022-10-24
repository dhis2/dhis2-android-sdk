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
import com.nhaarman.mockitokotlin2.*
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

class TrackerImporterFileResourcesPostCallShould {

    private val fileResourcesPostCall: FileResourcePostCall = mock()
    private val fileResourceHelper: FileResourceHelper = mock()

    private lateinit var fileResourcePostCall: TrackerImporterFileResourcesPostCall

    @Before
    fun setUp() {
        fileResourcePostCall = TrackerImporterFileResourcesPostCall(
            fileResourcesPostCall,
            fileResourceHelper
        )
    }

    @Test
    fun `Should create a single post call for repeated attributes`() {
        val resourceId = "resourceId"
        val attributeId = "attributeId"

        val attributeValue = NewTrackerImporterTrackedEntityAttributeValue.builder()
            .trackedEntityAttribute(attributeId)
            .value(resourceId)
            .build()

        val fileResource = FileResource.builder().uid(resourceId).build()
        val fileResources = listOf(fileResource)

        val payloadWrapper = NewTrackerImporterPayloadWrapper(
            updated = NewTrackerImporterPayload(
                trackedEntities = mutableListOf(
                    NewTrackerImporterTrackedEntity.builder()
                        .uid("tei_uid")
                        .trackedEntityAttributeValues(listOf(attributeValue))
                        .build()
                ),
                enrollments = mutableListOf(
                    NewTrackerImporterEnrollment.builder()
                        .uid("enrollment_uid")
                        .attributes(listOf(attributeValue))
                        .build()
                )
            )
        )

        val fValue = FileResourceValue.AttributeValue(attributeValue.trackedEntityAttribute()!!)
        whenever(fileResourceHelper.getUploadableFileResources()).doReturn(fileResources)
        whenever(fileResourceHelper.findAttributeFileResource(attributeValue, fileResources)).doReturn(fileResource)
        whenever(fileResourcesPostCall.uploadFileResource(fileResource, fValue))
            .doReturn(Math.random().toString())

        val result = fileResourcePostCall.uploadFileResources(payloadWrapper).blockingGet()

        verify(fileResourcesPostCall, times(1)).uploadFileResource(fileResource, fValue)

        val entityValue = result.updated.trackedEntities.first().trackedEntityAttributeValues()!!.first().value()!!
        val enrollmentValue = result.updated.enrollments.first().attributes()!!.first().value()!!

        assertThat(entityValue).isEqualTo(enrollmentValue)
        assertThat(entityValue).isNotEqualTo(resourceId)
    }
}
