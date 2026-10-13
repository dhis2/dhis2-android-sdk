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

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerPostCall
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class TrackerImporterProgramOwnerPostCallShould {

    private val programOwnerPostCall: ProgramOwnerPostCall = mock()
    private val programOwnerStore: ProgramOwnerStore = mock()
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore = mock()

    private val owner1 = programOwner("tei1", "program1", "orgUnit1")
    private val owner2 = programOwner("tei1", "program2", "orgUnit2")

    private lateinit var postCall: TrackerImporterProgramOwnerPostCall

    @Before
    fun setUp() {
        postCall = TrackerImporterProgramOwnerPostCall(
            programOwnerPostCall,
            programOwnerStore,
            trackedEntityInstanceStore,
        )
    }

    @Test
    fun emit_a_single_progress_when_there_are_no_program_owners() = runTest {
        val progresses = postCall.uploadProgramOwners(emptyMap()).toList()

        assertThat(progresses).hasSize(1)
        assertThat(progresses.first().doneCalls()).containsExactly("ProgramOwner")
        assertThat(progresses.first().isComplete).isFalse()
        verifyNoInteractions(programOwnerPostCall, programOwnerStore, trackedEntityInstanceStore)
    }

    @Test
    fun upload_program_owners_not_stored_yet() = runTest {
        givenTei("tei1", State.TO_UPDATE)

        val progresses = postCall.uploadProgramOwners(mapOf("tei1" to listOf(owner1, owner2))).toList()

        verify(programOwnerPostCall).uploadProgramOwner(owner1)
        verify(programOwnerPostCall).uploadProgramOwner(owner2)
        assertThat(progresses).hasSize(1)
    }

    @Test
    fun upload_program_owners_stored_with_a_not_synced_state() = runTest {
        givenTei("tei1", State.SYNCED)
        givenStoredOwner(owner1.toBuilder().syncState(State.TO_UPDATE).build())

        postCall.uploadProgramOwners(mapOf("tei1" to listOf(owner1))).toList()

        verify(programOwnerPostCall).uploadProgramOwner(owner1)
    }

    @Test
    fun skip_program_owners_already_synced() = runTest {
        givenTei("tei1", State.TO_UPDATE)
        givenStoredOwner(owner1.toBuilder().syncState(State.SYNCED).build())

        val progresses = postCall.uploadProgramOwners(mapOf("tei1" to listOf(owner1))).toList()

        verify(programOwnerPostCall, never()).uploadProgramOwner(any())
        assertThat(progresses).hasSize(1)
    }

    @Test
    fun look_up_stored_program_owner_by_program_tei_and_owner_org_unit() = runTest {
        givenTei("tei1", State.TO_UPDATE)

        postCall.uploadProgramOwners(mapOf("tei1" to listOf(owner1))).toList()

        verify(programOwnerStore).selectOneWhere(
            argThat { contains("'program1'") && contains("'tei1'") && contains("'orgUnit1'") },
        )
    }

    @Test
    fun skip_program_owners_of_teis_not_found_in_the_database() = runTest {
        postCall.uploadProgramOwners(mapOf("tei1" to listOf(owner1))).toList()

        verify(trackedEntityInstanceStore).selectByUid("tei1")
        verifyNoInteractions(programOwnerPostCall, programOwnerStore)
    }

    @Test
    fun skip_program_owners_of_teis_with_error_state() = runTest {
        givenTei("tei1", State.ERROR)

        postCall.uploadProgramOwners(mapOf("tei1" to listOf(owner1))).toList()

        verifyNoInteractions(programOwnerPostCall, programOwnerStore)
    }

    @Test
    fun skip_program_owners_of_teis_with_warning_state() = runTest {
        givenTei("tei1", State.WARNING)

        postCall.uploadProgramOwners(mapOf("tei1" to listOf(owner1))).toList()

        verifyNoInteractions(programOwnerPostCall, programOwnerStore)
    }

    @Test
    fun skip_program_owners_of_teis_without_aggregated_sync_state() = runTest {
        givenTei("tei1", null)

        postCall.uploadProgramOwners(mapOf("tei1" to listOf(owner1))).toList()

        verifyNoInteractions(programOwnerPostCall, programOwnerStore)
    }

    @Test
    fun evaluate_each_tei_independently() = runTest {
        val owner3 = programOwner("tei2", "program1", "orgUnit1")
        givenTei("tei1", State.ERROR)
        givenTei("tei2", State.TO_POST)

        val progresses = postCall.uploadProgramOwners(
            mapOf("tei1" to listOf(owner1, owner2), "tei2" to listOf(owner3)),
        ).toList()

        verify(programOwnerPostCall, never()).uploadProgramOwner(owner1)
        verify(programOwnerPostCall, never()).uploadProgramOwner(owner2)
        verify(programOwnerPostCall).uploadProgramOwner(owner3)
        assertThat(progresses).hasSize(1)
    }

    private suspend fun givenTei(uid: String, aggregatedSyncState: State?) {
        whenever(trackedEntityInstanceStore.selectByUid(uid)).doReturn(
            TrackedEntityInstance.builder()
                .uid(uid)
                .aggregatedSyncState(aggregatedSyncState)
                .build(),
        )
    }

    private suspend fun givenStoredOwner(owner: ProgramOwner) {
        whenever(programOwnerStore.selectOneWhere(any())).doReturn(owner)
    }

    private fun programOwner(tei: String, program: String, orgUnit: String) = ProgramOwner.builder()
        .trackedEntityInstance(tei)
        .program(program)
        .ownerOrgUnit(orgUnit)
        .syncState(State.TO_UPDATE)
        .build()
}
