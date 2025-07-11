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

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.maintenance.D2ErrorSamples
import org.hisp.dhis.android.core.settings.AppearanceSettings
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.stubbing.Answer

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class AppearanceSettingsCallShould {

    private val filterSettingHandler: FilterSettingHandler = mock()
    private val programConfigurationHandler: ProgramConfigurationSettingHandler = mock()
    private val dataSetConfigurationHandler: DataSetConfigurationSettingHandler = mock()
    private val service: SettingAppService = mock()
    private val appVersionManager: SettingsAppInfoManager = mock()

    private val appearanceSettings: AppearanceSettings = mock()

    private lateinit var appearanceSettingsCall: AppearanceSettingCall

    private val coroutineAPICallExecutor: CoroutineAPICallExecutorMock = CoroutineAPICallExecutorMock()

    @Before
    fun setUp() {
        whenAPICall { appearanceSettings }
        appearanceSettingsCall = AppearanceSettingCall(
            filterSettingHandler,
            programConfigurationHandler,
            dataSetConfigurationHandler,
            service,
            coroutineAPICallExecutor,
            appVersionManager,
        )
    }

    private fun whenAPICall(answer: Answer<AppearanceSettings>) {
        service.stub {
            onBlocking { appearanceSettings(any()) }.doAnswer(answer)
        }
    }

    @Test
    fun call_appearances_endpoint_if_version_1() = runTest {
        whenever(appVersionManager.getDataStoreVersion()) doReturn SettingsAppDataStoreVersion.V1_1

        appearanceSettingsCall.download(false)

        verify(service, never()).appearanceSettings(any())
    }

    @Test
    fun call_appearances_endpoint_if_version_2() = runTest {
        whenever(service.appearanceSettings(any())) doAnswer { appearanceSettings }

        whenever(appVersionManager.getDataStoreVersion()) doReturn SettingsAppDataStoreVersion.V2_0

        appearanceSettingsCall.download(false)

        verify(service).appearanceSettings(any())
    }

    @Test
    fun default_to_empty_collection_if_not_found() = runTest {
        whenever(service.appearanceSettings(any())) doAnswer { throw D2ErrorSamples.notFound() }

        whenever(appVersionManager.getDataStoreVersion()) doReturn SettingsAppDataStoreVersion.V2_0

        appearanceSettingsCall.download(false)

        verify(filterSettingHandler).handleMany(emptyList())
        verifyNoMoreInteractions(filterSettingHandler)
        verify(programConfigurationHandler).handleMany(emptyList())
        verifyNoMoreInteractions(programConfigurationHandler)
        verify(dataSetConfigurationHandler).handleMany(emptyList())
        verifyNoMoreInteractions(dataSetConfigurationHandler)
    }
}
