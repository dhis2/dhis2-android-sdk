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

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SettingsAppInfoManagerShould {
    private val settingsAppInfoCall: SettingsAppInfoCall = mock()

    private val settingsAppInfo = SettingsAppVersion.Valid(SettingsAppDataStoreVersion.V1_1, "unknown")
    private val settingAppInfoSingle: Single<SettingsAppVersion> = Single.just(settingsAppInfo)

    private lateinit var manager: SettingsAppInfoManager

    @Before
    fun setUp() {
        whenever(settingsAppInfoCall.fetch(any())) doReturn settingAppInfoSingle
        manager = SettingsAppInfoManagerImpl(settingsAppInfoCall)
    }

    @Test
    fun call_setting_info_only_if_version_is_null() {
        val version = manager.getDataStoreVersion().blockingGet()!!
        verify(settingsAppInfoCall).fetch(any())
        assertThat(version).isEquivalentAccordingToCompareTo(settingsAppInfo.dataStore)

        val cached = manager.getDataStoreVersion().blockingGet()
        verifyNoMoreInteractions(settingsAppInfoCall)
        assertThat(cached).isEquivalentAccordingToCompareTo(settingsAppInfo.dataStore)
    }
}
