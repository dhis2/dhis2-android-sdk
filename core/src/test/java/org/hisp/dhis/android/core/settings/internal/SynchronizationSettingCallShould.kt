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
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.maintenance.D2ErrorSamples
import org.hisp.dhis.android.core.settings.SynchronizationSettings
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class SynchronizationSettingCallShould {

    private val handler: Handler<SynchronizationSettings> = mock()
    private val service: SettingAppService = mock()
    private val apiCallExecutor: RxAPICallExecutor = mock()
    private val generalSettingCall: GeneralSettingCall = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val dataSetSettingCall: DataSetSettingCall = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val programSettingCall: ProgramSettingCall = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val appVersionManager: SettingsAppInfoManager = mock()

    private val synchronizationSettings: SynchronizationSettings = mock()
    private val synchronizationSettingSingle: Single<SynchronizationSettings> = Single.just(synchronizationSettings)

    private lateinit var synchronizationSettingCall: SynchronizationSettingCall

    @Before
    fun setUp() {
        whenever(service.synchronizationSettings(any())) doReturn synchronizationSettingSingle
        whenever(appVersionManager.getDataStoreVersion()) doReturn Single.just(SettingsAppDataStoreVersion.V1_1)
        synchronizationSettingCall = SynchronizationSettingCall(
            handler, service, apiCallExecutor,
            generalSettingCall, dataSetSettingCall, programSettingCall, appVersionManager
        )
    }

    @Test
    fun call_dataSet_and_program_endpoints_if_version_1() {
        whenever(appVersionManager.getDataStoreVersion()) doReturn Single.just(SettingsAppDataStoreVersion.V1_1)

        synchronizationSettingCall.getCompletable(false).blockingAwait()

        verify(generalSettingCall.fetch(any(), any())).blockingGet()
        verify(dataSetSettingCall.fetch(any())).blockingGet()
        verify(programSettingCall.fetch(any())).blockingGet()
        verify(service, never()).synchronizationSettings(any())
    }

    @Test
    fun call_synchronization_endpoint_if_version_2() {
        whenever(apiCallExecutor.wrapSingle(synchronizationSettingSingle, false)) doReturn
            synchronizationSettingSingle
        whenever(appVersionManager.getDataStoreVersion()) doReturn Single.just(SettingsAppDataStoreVersion.V2_0)

        synchronizationSettingCall.getCompletable(false).blockingAwait()

        verify(generalSettingCall.fetch(any(), any()), never()).blockingGet()
        verify(dataSetSettingCall.fetch(any()), never()).blockingGet()
        verify(programSettingCall.fetch(any()), never()).blockingGet()
        verify(service).synchronizationSettings(any())
    }

    @Test
    fun default_to_empty_collection_if_not_found() {
        whenever(appVersionManager.getDataStoreVersion()) doReturn Single.just(SettingsAppDataStoreVersion.V2_0)
        whenever(apiCallExecutor.wrapSingle(synchronizationSettingSingle, false)) doReturn
            Single.error(D2ErrorSamples.notFound())

        synchronizationSettingCall.getCompletable(false).blockingAwait()

        verify(handler).handleMany(emptyList())
        verifyNoMoreInteractions(handler)
    }
}
