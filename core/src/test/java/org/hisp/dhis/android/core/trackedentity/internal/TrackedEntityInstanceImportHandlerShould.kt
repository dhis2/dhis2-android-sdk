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
package org.hisp.dhis.android.core.trackedentity.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentImportHandler
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.*
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString

@RunWith(JUnit4::class)
class TrackedEntityInstanceImportHandlerShould {
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore = mock()

    private val enrollmentImportHandler: EnrollmentImportHandler = mock()

    private val importSummary: TEIImportSummary = mock()

    private val enrollmentSummary: EnrollmentImportSummary = mock()

    private val importEnrollment: EnrollmentImportSummaries = mock()

    private val trackerImportConflictStore: TrackerImportConflictStore = mock()

    private val trackerImportConflictParser: TrackerImportConflictParser = mock()

    private val relationshipStore: RelationshipStore = mock()

    private val dataStatePropagator: DataStatePropagator = mock()

    private val relationshipDHISVersionManager: RelationshipDHISVersionManager = mock()

    private val relationshipCollectionRepository: RelationshipCollectionRepository = mock()

    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore = mock()

    private val trackedEntityInstance: TrackedEntityInstance = mock()

    private val instances: List<TrackedEntityInstance> = ArrayList()

    private val sampleTeiUid = "LIbziAZIe6K"

    // object to test
    private lateinit var trackedEntityInstanceImportHandler: TrackedEntityInstanceImportHandler

    @Before
    fun setUp() {
        trackedEntityInstanceImportHandler = TrackedEntityInstanceImportHandler(
            trackedEntityInstanceStore, enrollmentImportHandler, trackerImportConflictStore,
            trackerImportConflictParser, relationshipStore, dataStatePropagator, relationshipDHISVersionManager,
            relationshipCollectionRepository, trackedEntityAttributeValueStore
        )

        whenever(trackedEntityInstanceStore.setSyncStateOrDelete(any(), any())).doReturn(HandleAction.Update)
    }

    @Test
    fun do_nothing_when_passing_null_argument() {
        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(null, instances)
        verify(trackedEntityInstanceStore, never()).setSyncStateOrDelete(anyString(), any())
    }

    @Test
    fun setStatus_shouldUpdateTrackedEntityInstanceStatusSuccess() {
        whenever(importSummary.status()).doReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).doReturn(sampleTeiUid)

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
            listOf(importSummary), instances
        )

        verify(trackedEntityInstanceStore, times(1))
            .setSyncStateOrDelete(sampleTeiUid, State.SYNCED)
    }

    @Test
    fun setStatus_shouldUpdateTrackedEntityInstanceStatusError() {
        whenever(importSummary.status()).doReturn(ImportStatus.ERROR)
        whenever(importSummary.reference()).doReturn(sampleTeiUid)

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
            listOf(importSummary), instances
        )

        verify(trackedEntityInstanceStore, times(1))
            .setSyncStateOrDelete(sampleTeiUid, State.ERROR)
    }

    @Test
    fun update_tracker_entity_instance_status_success_status_and_handle_import_enrollment_on_import_success() {
        whenever(importSummary.status()).doReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).doReturn(sampleTeiUid)
        whenever(importSummary.enrollments()).thenReturn(importEnrollment)

        val enrollmentSummaries = listOf(enrollmentSummary)
        whenever(importEnrollment.importSummaries()).doReturn(enrollmentSummaries)

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
            listOf(importSummary), instances
        )

        verify(trackedEntityInstanceStore, times(1)).setSyncStateOrDelete(sampleTeiUid, State.SYNCED)
        verify(enrollmentImportHandler, times(1)).handleEnrollmentImportSummary(
            eq(enrollmentSummaries), anyList(), eq(State.SYNCED)
        )
    }

    @Test
    fun mark_as_to_update_tracked_entity_instances_not_present_in_the_response() {
        whenever(importSummary.status()).doReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).doReturn(sampleTeiUid)

        val instances = listOf(trackedEntityInstance)
        whenever(trackedEntityInstance.uid()).thenReturn("missing_tei_uid")

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
            listOf(importSummary), instances
        )

        verify(trackedEntityInstanceStore, times(1)).setSyncStateOrDelete(sampleTeiUid, State.SYNCED)
        verify(trackedEntityInstanceStore, times(1)).setSyncStateOrDelete("missing_tei_uid", State.TO_UPDATE)
    }

    @Test
    fun return_tracked_entity_instances_not_present_in_the_response() {
        whenever(importSummary.status()).doReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).doReturn(sampleTeiUid)

        val instances = listOf(trackedEntityInstance)
        whenever(trackedEntityInstance.uid()).thenReturn("missing_tei_uid")

        val response = trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
            listOf(importSummary), instances
        )

        assertThat(response.teis.ignored.size).isEqualTo(1)
        assertThat(response.teis.ignored.first().uid()).isEqualTo("missing_tei_uid")
    }

    @Test
    fun deleted_tei_if_not_present_in_server_and_is_deleted_locally() {
        whenever(importSummary.status()).doReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).doReturn(null)
        whenever(importSummary.description())
            .doReturn("Tracked entity instance $sampleTeiUid cannot be deleted as it is not present in the system")

        whenever(trackedEntityInstanceStore.selectByUid(sampleTeiUid)).doReturn(trackedEntityInstance)
        whenever(trackedEntityInstance.deleted()).doReturn(true)

        val teis = listOf(sampleTei(sampleTeiUid, emptyList()))

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
            listOf(importSummary), teis
        )

        verify(trackedEntityInstanceStore, times(1)).setSyncStateOrDelete(sampleTeiUid, State.SYNCED)
    }

    private fun sampleTei(uid: String, attributeValues: List<TrackedEntityAttributeValue>): TrackedEntityInstance {
        return TrackedEntityInstance.builder()
            .uid(uid)
            .trackedEntityAttributeValues(attributeValues)
            .build()
    }
}
