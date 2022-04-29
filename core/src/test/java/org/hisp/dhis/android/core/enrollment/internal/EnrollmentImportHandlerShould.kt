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
package org.hisp.dhis.android.core.enrollment.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.event.internal.EventImportHandler
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.*
import org.hisp.dhis.android.core.tracker.importer.internal.JobReportEnrollmentHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyList

@RunWith(JUnit4::class)
class EnrollmentImportHandlerShould {
    private val enrollmentStore: EnrollmentStore = mock()

    private val eventImportHandler: EventImportHandler = mock()

    private val importEvent: EventImportSummaries = mock()

    private val eventSummary: EventImportSummary = mock()

    private val importSummary: EnrollmentImportSummary = mock()

    private val trackerImportConflictStore: TrackerImportConflictStore = mock()

    private val trackerImportConflictParser: TrackerImportConflictParser = mock()

    private val jobReportEnrollmentHandler: JobReportEnrollmentHandler = mock()

    private val dataStatePropagator: DataStatePropagator = mock()

    private val enrollment: Enrollment = mock()

    private val missingEnrollment: Enrollment = mock()

    private val enrollmentUid = "enrollment_uid"

    private val teiState = State.SYNCED

    private lateinit var enrollments: List<Enrollment>

    // object to test
    private lateinit var enrollmentImportHandler: EnrollmentImportHandler

    @Before
    @Throws(Exception::class)
    fun setUp() {
        enrollmentImportHandler = EnrollmentImportHandler(
            enrollmentStore, eventImportHandler, trackerImportConflictStore, trackerImportConflictParser,
            jobReportEnrollmentHandler, dataStatePropagator
        )

        whenever(enrollment.trackedEntityInstance()).thenReturn("tei_uid")
        whenever(enrollment.uid()).thenReturn(enrollmentUid)
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.SYNCED)).thenReturn(HandleAction.Update)
        enrollments = listOf(enrollment)
    }

    @Test
    fun invoke_set_state_when_enrollment_import_summary_is_success_with_reference() {
        whenever(importSummary.status()).thenReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).thenReturn(enrollmentUid)

        enrollmentImportHandler.handleEnrollmentImportSummary(listOf(importSummary), enrollments, teiState)

        verify(enrollmentStore, times(1)).setSyncStateOrDelete(enrollmentUid, State.SYNCED)
    }

    @Test
    fun invoke_set_state_when_enrollment_import_summary_is_error_with_reference() {
        whenever(importSummary.status()).thenReturn(ImportStatus.ERROR)
        whenever(importSummary.reference()).thenReturn(enrollmentUid)

        enrollmentImportHandler.handleEnrollmentImportSummary(listOf(importSummary), enrollments, teiState)

        verify(enrollmentStore, times(1)).setSyncStateOrDelete(enrollmentUid, State.ERROR)
    }

    @Test
    fun invoke_set_state_and_handle_event_import_summaries_when_enrollment_is_success_and_event_is_imported() {
        whenever(importSummary.status()).thenReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).thenReturn(enrollmentUid)
        whenever(importSummary.events()).thenReturn(importEvent)

        val eventSummaries: List<EventImportSummary> = listOf(eventSummary)
        whenever(importEvent.importSummaries()).thenReturn(eventSummaries)

        enrollmentImportHandler.handleEnrollmentImportSummary(listOf(importSummary), enrollments, teiState)
        verify(enrollmentStore, times(1)).setSyncStateOrDelete(enrollmentUid, State.SYNCED)
        verify(eventImportHandler, times(1)).handleEventImportSummaries(eq(eventSummaries), anyList())
    }

    @Test
    fun mark_as_to_update_enrollments_not_present_in_the_response() {
        whenever(importSummary.status()).thenReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).thenReturn(enrollmentUid)

        val enrollments = listOf(enrollment, missingEnrollment)
        whenever(missingEnrollment.uid()).thenReturn("missing_enrollment_uid")

        enrollmentImportHandler.handleEnrollmentImportSummary(listOf(importSummary), enrollments, teiState)

        verify(enrollmentStore, times(1)).setSyncStateOrDelete(enrollmentUid, State.SYNCED)
        verify(enrollmentStore, times(1)).setSyncStateOrDelete("missing_enrollment_uid", State.TO_UPDATE)
    }

    @Test
    fun return_enrollments_not_present_in_the_response() {
        whenever(importSummary.status()).thenReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).thenReturn(enrollmentUid)

        val enrollments = listOf(enrollment, missingEnrollment)
        whenever(missingEnrollment.uid()).thenReturn("missing_enrollment_uid")

        val response = enrollmentImportHandler.handleEnrollmentImportSummary(
            listOf(importSummary), enrollments, teiState
        )

        assertThat(response.enrollments.ignored.size).isEqualTo(1)
        assertThat(response.enrollments.ignored.first().uid()).isEqualTo("missing_enrollment_uid")
    }
}
