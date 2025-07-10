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
package org.hisp.dhis.android.core.trackedentity.ownership

import com.google.common.truth.Truth.assertThat
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.period.clock.internal.ClockProvider
import org.hisp.dhis.android.core.period.clock.internal.FixedClockProvider
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class OwnershipManagerShould {

    private val coroutineAPICallExecutor: CoroutineAPICallExecutor = CoroutineAPICallExecutorMock()
    private val ownershipNetworkHandler: OwnershipNetworkHandler = mock()
    private val dataStatePropagator: DataStatePropagator = mock()
    private val programTempOwnerStore: ProgramTempOwnerStore = mock()
    private val programOwnerStore: ProgramOwnerStore = mock()
    private val fixedDate = LocalDateTime(2020, 2, 5, 0, 0)
    private val clockProvider: ClockProvider = FixedClockProvider(fixedDate)

    private val httpResponse: HttpMessageResponse = mock()

    private lateinit var ownershipManager: OwnershipManagerImpl

    @Before
    fun setUp() = runTest {
        whenever(ownershipNetworkHandler.breakGlass(any(), any(), any())).doReturn(httpResponse)
        whenever(programTempOwnerStore.selectWhere(any(), any(), any())).doReturn(emptyList())

        ownershipManager = OwnershipManagerImpl(
            coroutineAPICallExecutor,
            ownershipNetworkHandler,
            dataStatePropagator,
            programTempOwnerStore,
            programOwnerStore,
            clockProvider,
        )
    }

    @Test
    fun persist_program_temp_owner_record_if_success() = runTest {
        whenever(httpResponse.httpStatusCode()).doReturn(HttpStatusCode.OK.value)
        whenever(programTempOwnerStore.insert(any<ProgramTempOwner>())).doReturn(1L)

        ownershipManager.breakGlass("tei_uid", "program", "reason").blockingAwait()

        verify(programTempOwnerStore, times(1)).updateOrInsertWhere(any<ProgramTempOwner>())
    }

    @Test
    fun do_not_persist_program_temp_owner_record_if_error() {
        whenever(httpResponse.httpStatusCode()).doReturn(HttpStatusCode.Unauthorized.value)
        whenever(httpResponse.message()).doReturn("Error in break the glass")

        try {
            ownershipManager.blockingBreakGlass("tei_uid", "program", "reason")
            fail("Should throw an error")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(D2Error::class.java)
        }

        verifyNoMoreInteractions(programTempOwnerStore)
    }

    @Test
    fun do_not_persist_program_temp_owner_on_fake_break_glass() = runTest {
        ownershipManager.fakeBreakGlass("tei_uid", "program_uid")

        verify(programTempOwnerStore).selectWhere(any(), any(), any())
        verifyNoMoreInteractions(programTempOwnerStore)
    }

    @Test
    fun propagate_program_ownership_update() = runTest {
        ownershipManager.blockingTransfer("tei_uid", "program_uid", "orgunit")

        verify(programOwnerStore).updateOrInsertWhere(any())
        verify(dataStatePropagator).refreshTrackedEntityInstanceAggregatedSyncState(any())
    }
}
