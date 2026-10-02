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
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.ImportCount
import org.hisp.dhis.android.core.imports.internal.RelationshipDeleteWebResponse
import org.hisp.dhis.android.core.imports.internal.RelationshipImportSummary
import org.hisp.dhis.android.core.maintenance.D2ErrorSamples
import org.hisp.dhis.android.core.trackedentity.internal.TrackerPostStateManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.net.HttpURLConnection.HTTP_CONFLICT
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.net.HttpURLConnection.HTTP_OK

@RunWith(JUnit4::class)
class RelationshipPostCallDeleteShould {

    private val networkHandler: RelationshipNetworkHandler = mock()
    private val relationshipStore: RelationshipStore = mock()
    private val relationshipImportHandler: RelationshipImportHandler = mock()
    private val dataStatePropagator: DataStatePropagator = mock()
    private val trackerStateManager: TrackerPostStateManager = mock()

    private val r1 = RelationshipSamples.get230("relationship1", "fromTei1", "toTei1")
    private val r2 = RelationshipSamples.get230("relationship2", "fromTei2", "toTei2")
    private val r3 = RelationshipSamples.get230("relationship3", "fromTei3", "toTei3")

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
    fun emit_single_progress_and_do_nothing_when_relationship_list_is_empty() = runTest {
        val progresses = postCall.deleteRelationships(emptyList()).toList()

        assertThat(progresses).hasSize(1)
        assertThat(progresses.first().doneCalls()).containsExactly("Relationship")
        assertThat(progresses.first().isComplete).isFalse()
        verifyNoInteractions(networkHandler, relationshipStore, dataStatePropagator)
    }

    @Test
    fun delete_relationship_from_db_when_server_returns_200_and_success() = runTest {
        givenDeleteSucceeds("relationship1", HTTP_OK, ImportStatus.SUCCESS)

        postCall.deleteRelationships(listOf(r1)).toList()

        verify(relationshipStore).deleteByEntity(r1)
        verify(relationshipStore, never()).setSyncState(any<String>(), any())
    }

    @Test
    fun delete_relationship_from_db_when_server_returns_404_with_null_response_body() = runTest {
        givenDeleteSucceeds("relationship1", HTTP_NOT_FOUND, null)

        postCall.deleteRelationships(listOf(r1)).toList()

        verify(relationshipStore).deleteByEntity(r1)
        verify(relationshipStore, never()).setSyncState(any<String>(), any())
    }

    @Test
    fun delete_relationship_from_db_when_server_returns_404_with_error_summary() = runTest {
        givenDeleteSucceeds("relationship1", HTTP_NOT_FOUND, ImportStatus.ERROR)

        postCall.deleteRelationships(listOf(r1)).toList()

        verify(relationshipStore).deleteByEntity(r1)
        verify(relationshipStore, never()).setSyncState(any<String>(), any())
    }

    @Test
    fun set_error_state_when_server_returns_200_and_error_status() = runTest {
        givenDeleteSucceeds("relationship1", HTTP_OK, ImportStatus.ERROR)

        postCall.deleteRelationships(listOf(r1)).toList()

        verify(relationshipStore).setSyncState("relationship1", State.ERROR)
        verify(relationshipStore, never()).deleteByEntity(any())
    }

    @Test
    fun set_error_state_when_server_returns_200_and_null_response() = runTest {
        givenDeleteSucceeds("relationship1", HTTP_OK, null)

        postCall.deleteRelationships(listOf(r1)).toList()

        verify(relationshipStore).setSyncState("relationship1", State.ERROR)
        verify(relationshipStore, never()).deleteByEntity(any())
    }

    @Test
    fun set_error_state_when_server_returns_unexpected_http_code() = runTest {
        givenDeleteSucceeds("relationship1", HTTP_CONFLICT, ImportStatus.SUCCESS)

        postCall.deleteRelationships(listOf(r1)).toList()

        verify(relationshipStore).setSyncState("relationship1", State.ERROR)
        verify(relationshipStore, never()).deleteByEntity(any())
    }

    @Test
    fun set_error_state_when_network_call_returns_failure() = runTest {
        givenDeleteFails("relationship1")

        postCall.deleteRelationships(listOf(r1)).toList()

        verify(relationshipStore).setSyncState("relationship1", State.ERROR)
        verify(relationshipStore, never()).deleteByEntity(any())
        verify(dataStatePropagator).propagateRelationshipUpdate(r1)
    }

    @Test
    fun not_rethrow_and_still_emit_progress_when_network_call_returns_failure() = runTest {
        givenDeleteFails("relationship1")

        val progresses = postCall.deleteRelationships(listOf(r1)).toList()

        assertThat(progresses).hasSize(1)
    }

    @Test
    fun process_every_relationship_when_outcomes_are_mixed() = runTest {
        givenDeleteSucceeds("relationship1", HTTP_OK, ImportStatus.SUCCESS)
        givenDeleteSucceeds("relationship2", HTTP_NOT_FOUND, null)
        givenDeleteFails("relationship3")

        postCall.deleteRelationships(listOf(r1, r2, r3)).toList()

        verify(networkHandler, times(3)).deleteRelationship(any())
        verify(relationshipStore).deleteByEntity(r1)
        verify(relationshipStore).deleteByEntity(r2)
        verify(relationshipStore).setSyncState("relationship3", State.ERROR)
    }

    @Test
    fun propagate_update_for_every_relationship_even_when_delete_fails() = runTest {
        givenDeleteFails("relationship1")
        givenDeleteFails("relationship2")
        givenDeleteFails("relationship3")

        postCall.deleteRelationships(listOf(r1, r2, r3)).toList()

        verify(dataStatePropagator).propagateRelationshipUpdate(r1)
        verify(dataStatePropagator).propagateRelationshipUpdate(r2)
        verify(dataStatePropagator).propagateRelationshipUpdate(r3)
        verify(dataStatePropagator, times(3)).propagateRelationshipUpdate(any())
    }

    @Test
    fun emit_progress_only_once_for_multiple_relationships() = runTest {
        givenDeleteSucceeds("relationship1", HTTP_OK, ImportStatus.SUCCESS)
        givenDeleteSucceeds("relationship2", HTTP_OK, ImportStatus.SUCCESS)
        givenDeleteSucceeds("relationship3", HTTP_OK, ImportStatus.SUCCESS)

        val progresses = postCall.deleteRelationships(listOf(r1, r2, r3)).toList()

        assertThat(progresses).hasSize(1)
        assertThat(progresses.first().doneCalls()).containsExactly("Relationship")
        assertThat(progresses.first().totalCalls()).isNull()
    }

    @Test
    fun not_touch_collaborators_until_the_flow_is_collected() = runTest {
        givenDeleteSucceeds("relationship1", HTTP_OK, ImportStatus.SUCCESS)

        val flow = postCall.deleteRelationships(listOf(r1))

        assertThat(flow).isNotNull()
        verifyNoInteractions(relationshipStore, dataStatePropagator)
    }

    private suspend fun givenDeleteSucceeds(uid: String, httpStatusCode: Int, status: ImportStatus?) {
        whenever(networkHandler.deleteRelationship(uid))
            .doSuspendableAnswer {
                yield()
                Result.Success(deleteResponse(httpStatusCode, status))
            }
    }

    private suspend fun givenDeleteFails(uid: String) {
        whenever(networkHandler.deleteRelationship(uid))
            .doSuspendableAnswer {
                yield()
                Result.Failure(D2ErrorSamples.get())
            }
    }

    private fun deleteResponse(httpStatusCode: Int, status: ImportStatus?) =
        RelationshipDeleteWebResponse(
            httpStatus = "OK",
            httpStatusCode = httpStatusCode,
            status = "OK",
            message = "message",
            response = status?.let {
                RelationshipImportSummary(
                    importCount = ImportCount.EMPTY,
                    status = it,
                    responseType = "ImportSummary",
                    reference = null,
                    conflicts = null,
                    description = null,
                )
            },
        )
}
