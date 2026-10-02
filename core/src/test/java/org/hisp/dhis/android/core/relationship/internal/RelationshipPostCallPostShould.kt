/*
 *  Copyright (c) 2004-2026, University of Oslo
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
package org.hisp.dhis.android.core.relationship.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.yield
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.ImportCount
import org.hisp.dhis.android.core.imports.internal.RelationshipImportSummaries
import org.hisp.dhis.android.core.imports.internal.RelationshipImportSummary
import org.hisp.dhis.android.core.imports.internal.RelationshipWebResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorSamples
import org.hisp.dhis.android.core.trackedentity.internal.TrackerPostStateManager
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class RelationshipPostCallPostShould {

    private val networkHandler: RelationshipNetworkHandler = mock()
    private val relationshipStore: RelationshipStore = mock()
    private val relationshipImportHandler: RelationshipImportHandler = mock()
    private val dataStatePropagator: DataStatePropagator = mock()
    private val trackerStateManager: TrackerPostStateManager = mock()

    private val r1 = RelationshipSamples.get230("relationship1", "fromTei1", "toTei1")
    private val r2 = RelationshipSamples.get230("relationship2", "fromTei2", "toTei2")
    private val r3 = RelationshipSamples.get230("relationship3", "fromTei3", "toTei3")

    private val relationships = listOf(r1)

    // object to test
    private lateinit var postCall: RelationshipPostCall

    @Before
    fun setUp() {
        postCall = RelationshipPostCall(
            networkHandler,
            relationshipStore,
            relationshipImportHandler,
            dataStatePropagator,
            trackerStateManager,
        )
    }

    @Test
    fun emit_progress_without_calling_network_when_relationship_list_is_empty() = runTest {
        val progresses = postCall.postRelationships(emptyList()).toList()

        assertThat(progresses).hasSize(1)
        assertThat(progresses.first().doneCalls()).containsExactly("Relationship")
        assertThat(progresses.first().isComplete).isFalse()
        verifyNoInteractions(
            networkHandler,
            trackerStateManager,
            relationshipImportHandler,
            dataStatePropagator,
        )
    }

    @Test
    fun set_uploading_state_before_posting_relationships() = runTest {
        givenPostSucceeds(listOf(summary("relationship1")))

        postCall.postRelationships(relationships).toList()

        verify(trackerStateManager).setPayloadStates(
            trackedEntityInstances = emptyList(),
            events = emptyList(),
            relationships = relationships,
            fileResources = emptyList(),
            forcedState = State.UPLOADING,
        )
        inOrder(trackerStateManager, networkHandler) {
            verify(trackerStateManager).setPayloadStates(
                trackedEntityInstances = emptyList(),
                events = emptyList(),
                relationships = relationships,
                fileResources = emptyList(),
                forcedState = State.UPLOADING,
            )
            verify(networkHandler).postRelationship(relationships)
        }
    }

    @Test
    fun post_relationships_and_forward_import_summaries_to_the_handler() = runTest {
        val summaries = listOf(summary("relationship1"), summary("relationship2"))
        givenPostSucceeds(summaries)

        postCall.postRelationships(relationships).toList()

        verify(relationshipImportHandler).handleRelationshipImportSummaries(
            importSummaries = summaries,
            relationships = relationships,
        )
    }

    @Test
    fun forward_null_summaries_when_response_body_is_null() = runTest {
        whenever(networkHandler.postRelationship(relationships))
            .doSuspendableAnswer {
                yield()
                Result.Success(RelationshipWebResponse.empty())
            }

        postCall.postRelationships(relationships).toList()

        verify(relationshipImportHandler).handleRelationshipImportSummaries(
            importSummaries = null,
            relationships = relationships,
        )
    }

    @Test
    fun forward_null_summaries_when_response_has_no_import_summaries() = runTest {
        givenPostSucceeds(null)

        postCall.postRelationships(relationships).toList()

        verify(relationshipImportHandler).handleRelationshipImportSummaries(
            importSummaries = null,
            relationships = relationships,
        )
    }

    @Test
    fun emit_progress_once_when_post_succeeds() = runTest {
        givenPostSucceeds(listOf(summary("relationship1")))

        val progresses = postCall.postRelationships(relationships).toList()

        assertThat(progresses).hasSize(1)
        assertThat(progresses.first().doneCalls()).containsExactly("Relationship")
        assertThat(progresses.first().isComplete).isFalse()
    }

    @Test
    fun not_restore_states_or_propagate_when_post_succeeds() = runTest {
        givenPostSucceeds(listOf(summary("relationship1")))

        postCall.postRelationships(relationships).toList()

        verify(trackerStateManager, never()).restorePayloadStates(any(), any(), any(), any())
        verify(dataStatePropagator, never()).propagateRelationshipUpdate(any())
    }

    @Test
    fun restore_states_and_propagate_and_rethrow_when_network_call_fails() = runTest {
        whenever(networkHandler.postRelationship(relationships))
            .doSuspendableAnswer {
                yield()
                Result.Failure(D2ErrorSamples.get())
            }

        val emissions = mutableListOf<D2Progress>()
        try {
            postCall.postRelationships(relationships).toList(emissions)
            fail("Should have thrown D2Error")
        } catch (e: D2Error) {
            assertThat(e.errorDescription()).isEqualTo("Error processing response")
        }

        assertThat(emissions).isEmpty()
        verifyStatesRestored()
        verify(dataStatePropagator).propagateRelationshipUpdate(r1)
    }

    @Test
    fun restore_states_and_rethrow_when_setting_uploading_state_fails() = runTest {
        whenever(
            trackerStateManager.setPayloadStates(
                trackedEntityInstances = emptyList(),
                events = emptyList(),
                relationships = relationships,
                fileResources = emptyList(),
                forcedState = State.UPLOADING,
            ),
        ).doAnswer { throw IllegalStateException("state error") }

        try {
            postCall.postRelationships(relationships).toList()
            fail("Should have thrown IllegalStateException")
        } catch (e: IllegalStateException) {
            assertThat(e.message).isEqualTo("state error")
        }

        verify(networkHandler, never()).postRelationship(any())
        verifyStatesRestored()
        verify(dataStatePropagator).propagateRelationshipUpdate(r1)
    }

    @Test
    fun restore_states_and_rethrow_when_import_handler_fails() = runTest {
        val summaries = listOf(summary("relationship1"))
        givenPostSucceeds(summaries)
        whenever(
            relationshipImportHandler.handleRelationshipImportSummaries(
                importSummaries = summaries,
                relationships = relationships,
            ),
        ).doAnswer { throw IllegalStateException("handler error") }

        val emissions = mutableListOf<D2Progress>()
        try {
            postCall.postRelationships(relationships).toList(emissions)
            fail("Should have thrown IllegalStateException")
        } catch (e: IllegalStateException) {
            assertThat(e.message).isEqualTo("handler error")
        }

        assertThat(emissions).isEmpty()
        verifyStatesRestored()
        verify(dataStatePropagator).propagateRelationshipUpdate(r1)
    }

    @Test
    fun propagate_update_for_every_relationship_when_post_fails() = runTest {
        val allRelationships = listOf(r1, r2, r3)
        whenever(networkHandler.postRelationship(allRelationships))
            .doSuspendableAnswer {
                yield()
                Result.Failure(D2ErrorSamples.get())
            }

        try {
            postCall.postRelationships(allRelationships).toList()
            fail("Should have thrown D2Error")
        } catch (e: D2Error) {
            assertThat(e).isNotNull()
        }

        verify(dataStatePropagator).propagateRelationshipUpdate(r1)
        verify(dataStatePropagator).propagateRelationshipUpdate(r2)
        verify(dataStatePropagator).propagateRelationshipUpdate(r3)
        verify(dataStatePropagator, times(3)).propagateRelationshipUpdate(any())
    }

    @Test
    fun not_touch_collaborators_until_the_flow_is_collected() = runTest {
        givenPostSucceeds(listOf(summary("relationship1")))

        val flow = postCall.postRelationships(relationships)

        assertThat(flow).isNotNull()
        verifyNoInteractions(trackerStateManager, relationshipImportHandler, dataStatePropagator)
    }

    private suspend fun givenPostSucceeds(summaries: List<RelationshipImportSummary>?) {
        whenever(networkHandler.postRelationship(relationships))
            .doSuspendableAnswer {
                yield()
                Result.Success(webResponse(summaries))
            }
    }

    private suspend fun verifyStatesRestored() {
        verify(trackerStateManager).restorePayloadStates(
            trackedEntityInstances = emptyList(),
            events = emptyList(),
            relationships = relationships,
            fileResources = emptyList(),
        )
    }

    private fun summary(reference: String, status: ImportStatus = ImportStatus.SUCCESS) =
        RelationshipImportSummary(
            importCount = ImportCount.EMPTY,
            status = status,
            responseType = "ImportSummary",
            reference = reference,
            conflicts = null,
            description = null,
        )

    private fun webResponse(summaries: List<RelationshipImportSummary>?) = RelationshipWebResponse(
        httpStatus = "OK",
        httpStatusCode = 200,
        status = "OK",
        message = "Import was successful.",
        response = RelationshipImportSummaries(
            status = ImportStatus.SUCCESS,
            responseType = "ImportSummaries",
            imported = summaries?.size ?: 0,
            updated = 0,
            deleted = 0,
            ignored = 0,
            importSummaries = summaries,
        ),
    )
}
