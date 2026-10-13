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

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventImportHandler
import org.hisp.dhis.android.core.event.internal.EventNetworkHandler
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.EventImportSummaries
import org.hisp.dhis.android.core.imports.internal.EventImportSummary
import org.hisp.dhis.android.core.imports.internal.EventWebResponse
import org.hisp.dhis.android.core.imports.internal.ImportCount
import org.hisp.dhis.android.core.imports.internal.ItemsWithFileResources
import org.hisp.dhis.android.core.imports.internal.TEIWebResponse
import org.hisp.dhis.android.core.imports.internal.TEIWebResponseHandler
import org.hisp.dhis.android.core.imports.internal.TEIWebResponseHandlerSummary
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorSamples
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.internal.RelationshipPostCall
import org.hisp.dhis.android.core.relationship.internal.RelationshipSamples
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterBreakTheGlassHelper
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterProgramOwnerPostCall
import org.hisp.dhis.android.network.trackedentityinstance.TrackedEntityInstanceService
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class OldTrackerImporterPostCallShould {

    private val payloadGenerator: OldTrackerImporterPayloadGenerator = mock()
    private val trackerStateManager: TrackerPostStateManager = mock()
    private val networkHandler: TrackedEntityInstanceNetworkHandler = mock()
    private val eventNetworkHandler: EventNetworkHandler = mock()
    private val teiWebResponseHandler: TEIWebResponseHandler = mock()
    private val eventImportHandler: EventImportHandler = mock()
    private val relationshipPostCall: RelationshipPostCall = mock()
    private val fileResourcePostCall: OldTrackerImporterFileResourcesPostCall = mock()
    private val programOwnerPostCall: TrackerImporterProgramOwnerPostCall = mock()
    private val breakTheGlassHelper: TrackerImporterBreakTheGlassHelper = mock()

    private val tei1 = tei("tei1")
    private val tei2 = tei("tei2")
    private val event1 = Event.builder().uid("event1").syncState(State.TO_POST).build()
    private val event2 = Event.builder().uid("event2").syncState(State.TO_POST).build()

    private val postedRelationship = RelationshipSamples.get230("relationship1", "from1", "to1")
    private val deletedRelationship = RelationshipSamples.get230("relationship2", "from2", "to2")
        .toBuilder().deleted(true).build()

    private val programOwners = mapOf(
        "tei1" to listOf(
            ProgramOwner.builder()
                .trackedEntityInstance("tei1")
                .program("program")
                .ownerOrgUnit("orgUnit")
                .build(),
        ),
    )

    private lateinit var postCall: OldTrackerImporterPostCall

    @Before
    fun setUp() = runTest {
        postCall = OldTrackerImporterPostCall(
            payloadGenerator,
            trackerStateManager,
            networkHandler,
            eventNetworkHandler,
            teiWebResponseHandler,
            eventImportHandler,
            relationshipPostCall,
            fileResourcePostCall,
            programOwnerPostCall,
            breakTheGlassHelper,
        )

        whenever(relationshipPostCall.deleteRelationships(any())).doReturn(emptyFlow())
        whenever(relationshipPostCall.postRelationships(any())).doReturn(emptyFlow())
        whenever(programOwnerPostCall.uploadProgramOwners(any())).doReturn(emptyFlow())

        whenever(fileResourcePostCall.uploadTrackedEntityFileResources(any())).doAnswer {
            ItemsWithFileResources(it.getArgument<List<TrackedEntityInstance>>(0), emptyList())
        }
        whenever(fileResourcePostCall.uploadEventsFileResources(any())).doAnswer {
            ItemsWithFileResources(it.getArgument<List<Event>>(0), emptyList())
        }

        whenever(networkHandler.postTrackedEntityInstances(any(), anyOrNull()))
            .doReturn(Result.Success(TEIWebResponse.empty()))
        whenever(teiWebResponseHandler.handleWebResponse(anyOrNull(), any()))
            .doAnswer { TEIWebResponseHandlerSummary() }
        whenever(breakTheGlassHelper.getGlassErrors(any<TEIWebResponseHandlerSummary>(), any()))
            .doReturn(emptyList())

        whenever(eventNetworkHandler.postEvents(any(), any()))
            .doReturn(Result.Success(EventWebResponse.empty()))
        whenever(eventImportHandler.handleEventImportSummaries(anyOrNull(), any()))
            .doReturn(TEIWebResponseHandlerSummary())
    }

    @Test
    fun not_touch_collaborators_until_the_flow_is_collected() = runTest {
        postCall.uploadTrackedEntityInstances(listOf(tei1))
        postCall.uploadEvents(listOf(event1))

        verifyNoInteractions(payloadGenerator, networkHandler, eventNetworkHandler, relationshipPostCall)
    }

    @Test
    fun upload_tei_payload_in_the_expected_order() = runTest {
        givenTeiPayload(
            OldTrackerImporterPayload(
                trackedEntityInstances = listOf(tei1),
                relationships = listOf(postedRelationship, deletedRelationship),
                programOwners = programOwners,
            ),
        )

        postCall.uploadTrackedEntityInstances(listOf(tei1)).toList()

        inOrder(relationshipPostCall, networkHandler, programOwnerPostCall) {
            verify(relationshipPostCall).deleteRelationships(listOf(deletedRelationship))
            verify(networkHandler).postTrackedEntityInstances(listOf(tei1), "SYNC")
            verify(relationshipPostCall).postRelationships(listOf(postedRelationship))
            verify(programOwnerPostCall).uploadProgramOwners(programOwners)
        }
        verifyNoInteractions(eventNetworkHandler)
    }

    @Test
    fun forward_progress_of_every_step() = runTest {
        val relationshipProgress = D2ProgressManager(null).increaseProgress(Relationship::class.java, false)
        val ownerProgress = D2ProgressManager(null).increaseProgress(ProgramOwner::class.java, false)
        whenever(relationshipPostCall.deleteRelationships(any())).doReturn(flowOf(relationshipProgress))
        whenever(relationshipPostCall.postRelationships(any())).doReturn(flowOf(relationshipProgress))
        whenever(programOwnerPostCall.uploadProgramOwners(any())).doReturn(flowOf(ownerProgress))
        givenTeiPayload(OldTrackerImporterPayload(trackedEntityInstances = listOf(tei1)))

        val progresses = postCall.uploadTrackedEntityInstances(listOf(tei1)).toList()

        assertThat(progresses.map { it.doneCalls().last() }).containsExactly(
            "Relationship",
            "TrackedEntityInstance",
            "Event",
            "Relationship",
            "ProgramOwner",
        ).inOrder()
    }

    @Test
    fun set_uploading_state_before_posting_teis() = runTest {
        givenTeiPayload(OldTrackerImporterPayload(trackedEntityInstances = listOf(tei1, tei2)))

        postCall.uploadTrackedEntityInstances(listOf(tei1, tei2)).toList()

        inOrder(trackerStateManager, networkHandler, teiWebResponseHandler) {
            verify(trackerStateManager).setPayloadStates(
                trackedEntityInstances = listOf(tei1, tei2),
                forcedState = State.UPLOADING,
            )
            verify(networkHandler).postTrackedEntityInstances(listOf(tei1, tei2), "SYNC")
            verify(teiWebResponseHandler).handleWebResponse(TEIWebResponse.empty(), listOf(tei1, tei2))
        }
    }

    @Test
    fun post_teis_in_partitions_of_default_page_size() = runTest {
        val teis = (1..TrackedEntityInstanceService.DEFAULT_PAGE_SIZE + 1).map { tei("tei$it") }
        val firstPartition = teis.take(TrackedEntityInstanceService.DEFAULT_PAGE_SIZE)
        val secondPartition = teis.drop(TrackedEntityInstanceService.DEFAULT_PAGE_SIZE)
        givenTeiPayload(OldTrackerImporterPayload(trackedEntityInstances = teis))

        val progresses = postCall.uploadTrackedEntityInstances(teis).toList()

        verify(fileResourcePostCall).uploadTrackedEntityFileResources(firstPartition)
        verify(fileResourcePostCall).uploadTrackedEntityFileResources(secondPartition)
        verify(networkHandler).postTrackedEntityInstances(firstPartition, "SYNC")
        verify(networkHandler).postTrackedEntityInstances(secondPartition, "SYNC")
        assertThat(progresses.filter { it.doneCalls().last() == "TrackedEntityInstance" }).hasSize(2)
    }

    @Test
    fun skip_partitions_whose_file_resources_could_not_be_uploaded() = runTest {
        whenever(fileResourcePostCall.uploadTrackedEntityFileResources(any()))
            .doReturn(ItemsWithFileResources(emptyList(), emptyList()))
        givenTeiPayload(OldTrackerImporterPayload(trackedEntityInstances = listOf(tei1)))

        val progresses = postCall.uploadTrackedEntityInstances(listOf(tei1)).toList()

        verifyNoInteractions(networkHandler, teiWebResponseHandler, breakTheGlassHelper)
        assertThat(progresses.map { it.doneCalls().last() }).doesNotContain("TrackedEntityInstance")
    }

    @Test
    fun only_post_teis_whose_file_resources_were_uploaded() = runTest {
        whenever(fileResourcePostCall.uploadTrackedEntityFileResources(any()))
            .doReturn(ItemsWithFileResources(listOf(tei2), listOf("fileResource1")))
        givenTeiPayload(OldTrackerImporterPayload(trackedEntityInstances = listOf(tei1, tei2)))

        postCall.uploadTrackedEntityInstances(listOf(tei1, tei2)).toList()

        verify(networkHandler).postTrackedEntityInstances(listOf(tei2), "SYNC")
        verify(fileResourcePostCall).updateFileResourceStates(listOf("fileResource1"))
    }

    @Test
    fun not_break_the_glass_when_there_are_no_glass_errors() = runTest {
        givenTeiPayload(OldTrackerImporterPayload(trackedEntityInstances = listOf(tei1)))

        postCall.uploadTrackedEntityInstances(listOf(tei1)).toList()

        verify(breakTheGlassHelper, never()).fakeBreakGlass(any<List<TrackedEntityInstance>>())
        verify(networkHandler, times(1)).postTrackedEntityInstances(any(), anyOrNull())
    }

    @Test
    fun break_the_glass_and_repost_glass_errors() = runTest {
        val enrollment = Enrollment.builder().uid("enrollment1").attributeOptionCombo("aoc").build()
        val glassTei = tei1.toBuilder().enrollments(listOf(enrollment)).build()
        val summary = TEIWebResponseHandlerSummary().apply { enrollments.ignored.add(enrollment) }
        val breakGlassSummary = TEIWebResponseHandlerSummary().apply { enrollments.success.add(enrollment) }

        whenever(teiWebResponseHandler.handleWebResponse(anyOrNull(), any())).doReturn(summary, breakGlassSummary)
        whenever(breakTheGlassHelper.getGlassErrors(summary, listOf(tei1))).doReturn(listOf(glassTei))
        givenTeiPayload(OldTrackerImporterPayload(trackedEntityInstances = listOf(tei1)))

        val progresses = postCall.uploadTrackedEntityInstances(listOf(tei1)).toList()

        inOrder(breakTheGlassHelper, networkHandler) {
            verify(networkHandler).postTrackedEntityInstances(listOf(tei1), "SYNC")
            verify(breakTheGlassHelper).fakeBreakGlass(listOf(glassTei))
            verify(networkHandler).postTrackedEntityInstances(listOf(glassTei), "SYNC")
        }
        verify(trackerStateManager).setPayloadStates(
            trackedEntityInstances = listOf(glassTei),
            forcedState = State.UPLOADING,
        )
        assertThat(summary.enrollments.ignored).isEmpty()
        assertThat(summary.enrollments.success).containsExactly(enrollment)
        assertThat(progresses.filter { it.doneCalls().last() == "TrackedEntityInstance" }).hasSize(1)
    }

    @Test
    fun restore_states_and_continue_when_a_partition_fails_with_a_non_offline_error() = runTest {
        val teis = (1..TrackedEntityInstanceService.DEFAULT_PAGE_SIZE + 1).map { tei("tei$it") }
        val firstPartition = teis.take(TrackedEntityInstanceService.DEFAULT_PAGE_SIZE)
        val secondPartition = teis.drop(TrackedEntityInstanceService.DEFAULT_PAGE_SIZE)
        whenever(fileResourcePostCall.uploadTrackedEntityFileResources(firstPartition))
            .doReturn(ItemsWithFileResources(firstPartition, listOf("fileResource1")))
        whenever(networkHandler.postTrackedEntityInstances(firstPartition, "SYNC"))
            .doReturn(Result.Failure(D2ErrorSamples.get()))
        givenTeiPayload(
            OldTrackerImporterPayload(trackedEntityInstances = teis, relationships = listOf(postedRelationship)),
        )

        val progresses = postCall.uploadTrackedEntityInstances(teis).toList()

        verify(trackerStateManager).restorePayloadStates(
            trackedEntityInstances = firstPartition,
            fileResources = listOf("fileResource1"),
        )
        verify(fileResourcePostCall, never()).updateFileResourceStates(listOf("fileResource1"))
        verify(networkHandler).postTrackedEntityInstances(secondPartition, "SYNC")
        verify(relationshipPostCall).postRelationships(listOf(postedRelationship))
        assertThat(progresses.filter { it.doneCalls().last() == "TrackedEntityInstance" }).hasSize(2)
    }

    @Test
    fun restore_states_and_rethrow_when_a_partition_fails_with_an_offline_error() = runTest {
        val offlineError = D2ErrorSamples.get().toBuilder().errorCode(D2ErrorCode.UNKNOWN_HOST).build()
        whenever(networkHandler.postTrackedEntityInstances(any(), anyOrNull()))
            .doReturn(Result.Failure(offlineError))
        givenTeiPayload(
            OldTrackerImporterPayload(
                trackedEntityInstances = listOf(tei1),
                events = listOf(event1),
                relationships = listOf(postedRelationship),
            ),
        )

        val emissions = mutableListOf<D2Progress>()
        try {
            postCall.uploadTrackedEntityInstances(listOf(tei1)).toList(emissions)
            fail("Should have thrown D2Error")
        } catch (e: D2Error) {
            assertThat(e.errorCode()).isEqualTo(D2ErrorCode.UNKNOWN_HOST)
        }

        verify(trackerStateManager).restorePayloadStates(
            trackedEntityInstances = listOf(tei1),
            fileResources = emptyList(),
        )
        assertThat(emissions.map { it.doneCalls().last() }).doesNotContain("TrackedEntityInstance")
        verifyNoInteractions(eventNetworkHandler)
        verify(relationshipPostCall, never()).postRelationships(any())
    }

    @Test
    fun emit_complete_event_progress_without_posting_when_there_are_no_events() = runTest {
        givenTeiPayload(OldTrackerImporterPayload(trackedEntityInstances = listOf(tei1)))

        val progresses = postCall.uploadTrackedEntityInstances(listOf(tei1)).toList()

        val eventProgress = progresses.single { it.doneCalls().last() == "Event" }
        assertThat(eventProgress.isComplete).isTrue()
        verify(fileResourcePostCall, never()).uploadEventsFileResources(any())
        verifyNoInteractions(eventNetworkHandler, eventImportHandler)
    }

    @Test
    fun upload_event_payload_in_the_expected_order() = runTest {
        givenEventPayload(
            OldTrackerImporterPayload(
                events = listOf(event1, event2),
                relationships = listOf(postedRelationship, deletedRelationship),
            ),
        )

        val progresses = postCall.uploadEvents(listOf(event1, event2)).toList()

        inOrder(relationshipPostCall, trackerStateManager, eventNetworkHandler, eventImportHandler) {
            verify(relationshipPostCall).deleteRelationships(listOf(deletedRelationship))
            verify(trackerStateManager).setPayloadStates(
                events = listOf(event1, event2),
                forcedState = State.UPLOADING,
            )
            verify(eventNetworkHandler).postEvents(listOf(event1, event2), "SYNC")
            verify(eventImportHandler).handleEventImportSummaries(null, listOf(event1, event2))
            verify(relationshipPostCall).postRelationships(listOf(postedRelationship))
        }
        verifyNoInteractions(networkHandler)

        val eventProgress = progresses.single { it.doneCalls().last() == "Event" }
        assertThat(eventProgress.isComplete).isTrue()
    }

    @Test
    fun forward_event_import_summaries_to_the_import_handler() = runTest {
        val summaries = listOf(
            EventImportSummary(
                importCount = ImportCount.EMPTY,
                status = ImportStatus.SUCCESS,
                responseType = "ImportSummary",
                reference = "event1",
                conflicts = null,
                description = null,
            ),
        )
        whenever(eventNetworkHandler.postEvents(any(), any())).doReturn(
            Result.Success(
                EventWebResponse.empty().copy(
                    response = EventImportSummaries(
                        status = ImportStatus.SUCCESS,
                        responseType = "ImportSummaries",
                        imported = 1,
                        updated = 0,
                        deleted = 0,
                        ignored = 0,
                        importSummaries = summaries,
                    ),
                ),
            ),
        )
        givenEventPayload(OldTrackerImporterPayload(events = listOf(event1)))

        postCall.uploadEvents(listOf(event1)).toList()

        verify(eventImportHandler).handleEventImportSummaries(summaries, listOf(event1))
    }

    @Test
    fun only_post_events_whose_file_resources_were_uploaded() = runTest {
        whenever(fileResourcePostCall.uploadEventsFileResources(any()))
            .doReturn(ItemsWithFileResources(listOf(event2), listOf("fileResource1")))
        givenEventPayload(OldTrackerImporterPayload(events = listOf(event1, event2)))

        postCall.uploadEvents(listOf(event1, event2)).toList()

        verify(eventNetworkHandler).postEvents(listOf(event2), "SYNC")
        verify(fileResourcePostCall).updateFileResourceStates(listOf("fileResource1"))
    }

    @Test
    fun restore_states_and_rethrow_when_posting_events_fails() = runTest {
        whenever(fileResourcePostCall.uploadEventsFileResources(any()))
            .doReturn(ItemsWithFileResources(listOf(event1), listOf("fileResource1")))
        whenever(eventNetworkHandler.postEvents(any(), any())).doReturn(Result.Failure(D2ErrorSamples.get()))
        givenEventPayload(
            OldTrackerImporterPayload(
                events = listOf(event1),
                relationships = listOf(postedRelationship),
                programOwners = programOwners,
            ),
        )

        val emissions = mutableListOf<D2Progress>()
        try {
            postCall.uploadEvents(listOf(event1)).toList(emissions)
            fail("Should have thrown D2Error")
        } catch (e: D2Error) {
            assertThat(e.errorDescription()).isEqualTo("Error processing response")
        }

        verify(trackerStateManager).restorePayloadStates(
            events = listOf(event1),
            fileResources = listOf("fileResource1"),
        )
        verify(fileResourcePostCall, never()).updateFileResourceStates(any())
        verifyNoInteractions(eventImportHandler)
        verify(relationshipPostCall, never()).postRelationships(any())
        verify(programOwnerPostCall, never()).uploadProgramOwners(any())
        assertThat(emissions.map { it.doneCalls().last() }).doesNotContain("Event")
    }

    private suspend fun givenTeiPayload(payload: OldTrackerImporterPayload) {
        whenever(payloadGenerator.getTrackedEntityInstancePayload(any())).doReturn(payload)
    }

    private suspend fun givenEventPayload(payload: OldTrackerImporterPayload) {
        whenever(payloadGenerator.getEventPayload(any())).doReturn(payload)
    }

    private fun tei(uid: String) = TrackedEntityInstance.builder()
        .uid(uid)
        .syncState(State.TO_POST)
        .build()
}
