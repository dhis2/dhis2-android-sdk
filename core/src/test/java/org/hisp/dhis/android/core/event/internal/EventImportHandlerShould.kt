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
package org.hisp.dhis.android.core.event.internal

import com.nhaarman.mockitokotlin2.*
import java.util.*
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.EventImportSummary
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictParser
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import org.hisp.dhis.android.core.tracker.importer.internal.JobReportEventHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString

@RunWith(JUnit4::class)
class EventImportHandlerShould {
    private val importSummary: EventImportSummary = mock()

    private val eventStore: EventStore = mock()

    private val enrollmentStore: EnrollmentStore = mock()

    private val trackerImportConflictStore: TrackerImportConflictStore = mock()

    private val jobReportEventHandler: JobReportEventHandler = mock()

    private val dataStatePropagator: DataStatePropagator = mock()

    private val trackerImportConflictParser: TrackerImportConflictParser = mock()

    private val trackedEntityDataValueStore: TrackedEntityDataValueStore = mock()

    private val fileResourceStore: IdentifiableDataObjectStore<FileResource> = mock()

    private val events: List<Event> = ArrayList()

    private val event: Event = mock()

    // object to test
    private lateinit var eventImportHandler: EventImportHandler

    @Before
    @Throws(Exception::class)
    fun setUp() {
        whenever(importSummary.status()).thenReturn(ImportStatus.SUCCESS)

        eventImportHandler = EventImportHandler(
            eventStore, enrollmentStore, trackerImportConflictStore, trackerImportConflictParser,
            jobReportEventHandler, dataStatePropagator, trackedEntityDataValueStore, fileResourceStore
        )
    }

    @Test
    fun do_nothing_when_passing_null_argument() {
        eventImportHandler.handleEventImportSummaries(null, events, emptyList())

        verify(eventStore, never()).setSyncStateOrDelete(anyString(), any())
    }

    @Test
    fun invoke_set_state_after_handle_event_import_summaries_with_success_status_and_reference() {
        whenever(importSummary.status()).thenReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).thenReturn("test_event_uid")

        eventImportHandler.handleEventImportSummaries(listOf(importSummary), events, emptyList())

        verify(eventStore, times(1)).setSyncStateOrDelete("test_event_uid", State.SYNCED)
    }

    @Test
    fun invoke_set_state_after_handle_event_import_summaries_with_error_status_and_reference() {
        whenever(importSummary.status()).thenReturn(ImportStatus.ERROR)
        whenever(importSummary.reference()).thenReturn("test_event_uid")

        eventImportHandler.handleEventImportSummaries(listOf(importSummary), events, emptyList())

        verify(eventStore, times(1)).setSyncStateOrDelete("test_event_uid", State.ERROR)
    }

    @Test
    fun mark_as_to_update_events_not_present_in_the_response() {
        whenever(importSummary.status()).thenReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).thenReturn("test_event_uid")

        val events = listOf(event)
        whenever(event.uid()).thenReturn("missing_event_uid")

        eventImportHandler.handleEventImportSummaries(listOf(importSummary), events, emptyList())

        verify(eventStore, times(1)).setSyncStateOrDelete("test_event_uid", State.SYNCED)
        verify(eventStore, times(1)).setSyncStateOrDelete("missing_event_uid", State.TO_UPDATE)
    }

    @Test
    fun mark_file_resources_as_synced_if_success() {
        whenever(importSummary.status()).doReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).doReturn("test_event_uid")

        val events = listOf(
            sampleEvent("test_event_uid", listOf(sampleDataValue("test_event_uid", "de1", "resource1")))
        )

        eventImportHandler.handleEventImportSummaries(
            listOf(importSummary), events, listOf("resource1", "resource2")
        )

        verify(eventStore, times(1)).setSyncStateOrDelete("test_event_uid", State.SYNCED)
        verify(fileResourceStore, times(1)).setSyncStateIfUploading("resource1", State.SYNCED)
        verifyNoMoreInteractions(fileResourceStore)
    }

    @Test
    fun mark_file_resources_as_to_post_if_failure() {
        whenever(importSummary.status()).doReturn(ImportStatus.ERROR)
        whenever(importSummary.reference()).doReturn("test_event_uid")

        val events = listOf(
            sampleEvent("test_event_uid", listOf(sampleDataValue("test_event_uid", "de1", "resource1")))
        )

        eventImportHandler.handleEventImportSummaries(
            listOf(importSummary), events, listOf("resource1", "resource2")
        )

        verify(eventStore, times(1)).setSyncStateOrDelete("test_event_uid", State.ERROR)
        verify(fileResourceStore, times(1)).setSyncStateIfUploading("resource1", State.TO_POST)
        verifyNoMoreInteractions(fileResourceStore)
    }

    private fun sampleEvent(uid: String, dataValues: List<TrackedEntityDataValue>): Event {
        return Event.builder()
            .uid(uid)
            .trackedEntityDataValues(dataValues)
            .build()
    }

    private fun sampleDataValue(event: String, dataElement: String, value: String): TrackedEntityDataValue {
        return TrackedEntityDataValue.builder()
            .event(event)
            .dataElement(dataElement)
            .value(value)
            .build()
    }
}
