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
package org.hisp.dhis.android.core.systeminfo.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.Transaction
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.resource.internal.Resource
import org.hisp.dhis.android.core.resource.internal.ResourceHandler
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.hisp.dhis.android.network.common.fields.Fields
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SystemInfoCallShould {
    private val systemInfoService: SystemInfoService = mock()
    private val databaseAdapter: DatabaseAdapter = mock()
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor = CoroutineAPICallExecutorMock()
    private val d2Error: D2Error = mock()
    private val systemInfoHandler: SystemInfoHandler = mock()
    private val resourceHandler: ResourceHandler = mock()
    private val transaction: Transaction = mock()

    private val filterCaptor: KArgumentCaptor<Fields<SystemInfo>> = argumentCaptor()

    private val systemInfo: SystemInfo = mock()
    private val versionManager: DHISVersionManagerImpl = mock()
    private val serverDate: Date = mock()

    private lateinit var systemInfoSyncCall: SystemInfoCall

    @Before
    fun setUp() {
        systemInfoSyncCall = SystemInfoCall(
            systemInfoHandler,
            systemInfoService,
            resourceHandler,
            versionManager,
            coroutineAPICallExecutor,
        )

        whenever(systemInfo.version()).thenReturn("2.30")
        whenever(systemInfo.serverDate()).thenReturn(serverDate)
        whenever(databaseAdapter.beginNewTransaction()).thenReturn(transaction)
        systemInfoService.stub {
            onBlocking { getSystemInfo(filterCaptor.capture()) }.doReturn(systemInfo)
        }
    }

    @Test
    fun pass_correct_fields_to_service() = runTest {
        systemInfoSyncCall.download(true)

        assertThat(filterCaptor.firstValue).isEqualTo(SystemInfoFields.allFields)
    }

    @Test
    fun emit_d2_error_when_api_call_executor_returns_error() = runTest {
        systemInfoService.stub {
            onBlocking { getSystemInfo(any()) }.doAnswer { throw d2Error }
        }

        try {
            systemInfoSyncCall.download(true)
            fail("It should throw exception")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(D2Error::class.java)
        }
    }

    @Test
    fun never_invoke_handlers_on_call_exception() = runTest {
        systemInfoService.stub {
            onBlocking { getSystemInfo(filterCaptor.capture()) }.doAnswer { throw d2Error }
        }

        verifyThrowD2Error(
            block = { systemInfoSyncCall.download(true) },
        )

        verifyNoMoreInteractions(systemInfoHandler)
        verifyNoMoreInteractions(resourceHandler)
    }

    @Test
    fun invoke_handler_after_successful_call() = runTest {
        systemInfoSyncCall.download(true)

        verify(systemInfoHandler).handle(systemInfo)
        verify(resourceHandler).handleResource(eq(Resource.Type.SYSTEM_INFO))
    }

    @Test
    fun invoke_set_version_and_check_bypass_Version_after_successful_call() = runTest {
        systemInfoSyncCall.download(true)

        verify(versionManager).getBypassVersion()
        verify(versionManager).setVersion(any())
    }

    @Test
    fun throw_d2_call_exception_when_system_version_not_supported() = runTest {
        whenever(systemInfo.version()).thenReturn("2.28")

        verifyThrowD2Error(
            block = { systemInfoSyncCall.download(true) },
        )
    }

    @Test
    fun not_call_handler_when_system_version_not_supported() = runTest {
        whenever(systemInfo.version()).thenReturn("2.28")

        verifyThrowD2Error(
            block = { systemInfoSyncCall.download(true) },
            code = D2ErrorCode.INVALID_DHIS_VERSION,
        )

        verify(systemInfoHandler, never()).handle(systemInfo)
        verify(resourceHandler, never()).handleResource(eq(Resource.Type.SYSTEM_INFO))
    }

    private suspend fun verifyThrowD2Error(block: suspend () -> Unit, code: D2ErrorCode? = null) {
        try {
            block.invoke()
            fail("It should not get here")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(D2Error::class.java)

            if (code != null) {
                assertThat((e as D2Error).errorCode()).isEquivalentAccordingToCompareTo(code)
            }
        }
    }
}
