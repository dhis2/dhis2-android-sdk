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

package org.hisp.dhis.android.core.program.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloaderImpl
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.network.common.PayloadJson
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.same
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class ProgramEndpointCallShould : BaseCallShould() {

    private val programNetworkHandler: ProgramNetworkHandler = mock()
    private val programHandler: ProgramHandler = mock()
    private val dhisVersionManager: DHISVersionManager = mock()
    private val uidsCaptor = argumentCaptor<Set<String>>()

    private val apiCall: PayloadJson<Program> = PayloadJson(emptyList())

    private val mockedApiDownloader: APIDownloader = mock()

    private val programCallResult: List<Program> = emptyList()

    private val programUids: Set<String> = setOf("programUid")

    @Before
    override fun setUp() {
        super.setUp()
        mockedApiDownloader.stub {
            onBlocking {
                downloadPartitioned(same(programUids), any(), any<Handler<Program>>(), any())
            } doReturn programCallResult
        }
        whenever(dhisVersionManager.isGreaterThan(DHISVersion.V2_34)) doReturn true
    }

    @Test
    fun call_api_downloader() = runTest {
        ProgramCall(
            programNetworkHandler,
            programHandler,
            mockedApiDownloader,
        ).download(programUids)

        verify(mockedApiDownloader).downloadPartitioned(
            same(programUids),
            any(),
            any<Handler<Program>>(),
            any(),
        )
    }

    @Test
    fun call_network_handler_for_real_api_downloader() = runTest {
        whenever(
            programNetworkHandler.getPrograms(
                uidsCaptor.capture(),
            ),
        ).doReturn(apiCall)

        ProgramCall(
            programNetworkHandler,
            programHandler,
            APIDownloaderImpl(resourceHandler),
        ).download(programUids)

        assertThat(uidsCaptor.firstValue).isEqualTo(programUids)
    }
}
