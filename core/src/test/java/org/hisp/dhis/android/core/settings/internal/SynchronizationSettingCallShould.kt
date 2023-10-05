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
package org.hisp.dhis.android.core.settings.internal

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.maintenance.D2ErrorSamples
import org.hisp.dhis.android.core.settings.SynchronizationSettings
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.stubbing.Answer

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class SynchronizationSettingCallShould {

    private val handler: SynchronizationSettingHandler = mock()
    private val service: SettingAppService = mock()
    private val coroutineAPICallExecutor: CoroutineAPICallExecutorMock = CoroutineAPICallExecutorMock()
    private val generalSettingCall: GeneralSettingCall = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val dataSetSettingCall: DataSetSettingCall = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val programSettingCall: ProgramSettingCall = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val appVersionManager: SettingsAppInfoManager = mock()

    private val synchronizationSettings: SynchronizationSettings = mock()

    private lateinit var synchronizationSettingCall: SynchronizationSettingCall

    @Before
    fun setUp() {
        whenAPICall { synchronizationSettings }
        appVersionManager.stub {
            onBlocking { getDataStoreVersion() } doReturn SettingsAppDataStoreVersion.V1_1
        }
        synchronizationSettingCall = SynchronizationSettingCall(
            handler,
            service,
            coroutineAPICallExecutor,
            generalSettingCall,
            dataSetSettingCall,
            programSettingCall,
            appVersionManager,
        )
    }

    private fun whenAPICall(answer: Answer<SynchronizationSettings>) {
        service.stub {
            onBlocking { synchronizationSettings(any()) }.doAnswer(answer)
        }
    }

    @Test
    fun call_dataSet_and_program_endpoints_if_version_1() = runTest {
        whenever(appVersionManager.getDataStoreVersion()) doReturn SettingsAppDataStoreVersion.V1_1

        synchronizationSettingCall.download(false)

        verify(generalSettingCall).fetch(any(), any())
        verify(dataSetSettingCall).fetch(any())
        verify(programSettingCall).fetch(any())
        verify(service, never()).synchronizationSettings(any())
    }

    @Test
    fun call_synchronization_endpoint_if_version_2() = runTest {
        whenever(service.synchronizationSettings(any())) doReturn synchronizationSettings

        whenever(appVersionManager.getDataStoreVersion()) doReturn SettingsAppDataStoreVersion.V2_0

        synchronizationSettingCall.download(false)

        verify(generalSettingCall, never()).fetch(any(), any())

        verify(dataSetSettingCall, never()).fetch(any())

        verify(programSettingCall, never()).fetch(any())

        verify(service).synchronizationSettings(any())
    }

    @Test
    fun default_to_empty_collection_if_not_found() = runTest {
        whenever(appVersionManager.getDataStoreVersion()) doReturn SettingsAppDataStoreVersion.V2_0
        whenever(service.synchronizationSettings(any())) doAnswer { throw D2ErrorSamples.notFound() }

        synchronizationSettingCall.download(false)

        verify(handler).handleMany(emptyList())
        verifyNoMoreInteractions(handler)
    }
}
