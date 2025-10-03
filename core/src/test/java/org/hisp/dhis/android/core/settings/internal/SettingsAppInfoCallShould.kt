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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorSamples
import org.hisp.dhis.android.core.settings.GeneralSettings
import org.hisp.dhis.android.core.settings.SettingsAppInfo
import org.junit.Assert.assertThrows
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class SettingsAppInfoCallShould {
    private val networkHandler: SettingsNetworkHandler = mock()
    private val settingsAppInfo = SettingsAppInfo.builder()
        .dataStoreVersion(SettingsAppDataStoreVersion.V2_0)
        .build()

    private val generalSettings = GeneralSettings.builder()
        .encryptDB(true)
        .build()

    private lateinit var dataSetSettingCall: SettingsAppInfoCall

    @Before
    fun setUp() {
        networkHandler.stub {
            onBlocking { settingsAppInfo() } doAnswer { settingsAppInfo }
        }

        networkHandler.stub {
            onBlocking { generalSettings(SettingsAppDataStoreVersion.V1_1) } doAnswer {
                Result.Success(generalSettings)
            }
        }

        dataSetSettingCall = SettingsAppInfoCall(networkHandler)
    }

    @Test
    fun default_to_version_2_if_info_found() = runTest {
        when (val version = dataSetSettingCall.fetch()) {
            is SettingsAppVersion.Valid -> {
                assertThat(version.dataStore).isEquivalentAccordingToCompareTo(SettingsAppDataStoreVersion.V2_0)
                assertThat(version.app).isNotEmpty()
            }

            else -> fail("Unexpected version")
        }
    }

    @Test
    fun default_to_version_1_if_info_not_found() = runTest {
        whenever(networkHandler.settingsAppInfo()) doAnswer { throw D2ErrorSamples.notFound() }

        when (val version = dataSetSettingCall.fetch()) {
            is SettingsAppVersion.Valid -> {
                assertThat(version.dataStore).isEquivalentAccordingToCompareTo(SettingsAppDataStoreVersion.V1_1)
                assertThat(version.app).isNotEmpty()
            }

            else -> fail("Unexpected version")
        }
    }

    @Test
    fun return_data_store_empty_if_cannot_found_anything() = runTest {
        whenever(networkHandler.settingsAppInfo()) doAnswer { throw D2ErrorSamples.notFound() }
        whenever(networkHandler.generalSettings(any())) doReturn Result.Failure(D2ErrorSamples.notFound())

        val version = dataSetSettingCall.fetch()

        assertThat(version is SettingsAppVersion.DataStoreEmpty).isTrue()
    }

    @Test
    fun throws_D2_exception_if_other_error_than_not_found_in_general_settings() = runTest {
        whenever(networkHandler.settingsAppInfo()) doAnswer { throw D2ErrorSamples.notFound() }
        whenever(networkHandler.generalSettings(any())) doReturn Result.Failure(D2ErrorSamples.get())

        assertThrows(D2Error::class.java) {
            runBlocking {
                dataSetSettingCall.fetch()
            }
        }
    }

    @Test
    fun throws_D2_exception_if_other_error_than_not_found_in_info() = runTest {
        whenever(networkHandler.settingsAppInfo()) doAnswer { throw D2ErrorSamples.get() }

        assertThrows(D2Error::class.java) {
            runBlocking {
                dataSetSettingCall.fetch()
            }
        }
    }
}
