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
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.hisp.dhis.android.core.settings.ProgramSettings
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProgramSettingCallShould {
    private val handler: Handler<ProgramSetting> = mock()
    private val service: SettingAppService = mock()
    private val programSettingSingle: Single<ProgramSettings> = mock()
    private val apiCallExecutor: RxAPICallExecutor = mock()
    private val appVersionManager: SettingsAppInfoManager = mock()

    private lateinit var programSettingCall: ProgramSettingCall

    @Before
    fun setUp() {
        whenever(appVersionManager.getDataStoreVersion()) doReturn Single.just(SettingsAppDataStoreVersion.V1_1)
        whenever(service.programSettings(any())) doReturn programSettingSingle
        programSettingCall = ProgramSettingCall(handler, service, apiCallExecutor, appVersionManager)
    }

    @Test
    fun default_to_empty_collection_if_not_found() {
        whenever(apiCallExecutor.wrapSingle(programSettingSingle, false)) doReturn
            Single.error(D2ErrorSamples.notFound())

        programSettingCall.getCompletable(false).blockingAwait()

        verify(handler).handleMany(emptyList())
        verifyNoMoreInteractions(handler)
    }
}
