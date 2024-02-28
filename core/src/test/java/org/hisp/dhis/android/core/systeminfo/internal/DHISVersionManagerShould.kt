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
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.systeminfo.DHISPatchVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.systeminfo.SMSVersion
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.junit.Before
import org.junit.Test

class DHISVersionManagerShould {
    private val systemInfoStore: SystemInfoStore = mock()
    private val systemInfo: SystemInfo = mock()

    // Object to test
    private lateinit var dhisVersionManager: DHISVersionManager

    @Before
    fun setUp() {
        whenever(systemInfoStore.selectFirst()).thenReturn(systemInfo)
        dhisVersionManager = DHISVersionManagerImpl(systemInfoStore)
    }

    @Test
    fun compare_version_when_not_null() {
        whenever(systemInfo.version()).thenReturn("2.31.2")

        assertThat(dhisVersionManager.isVersion(DHISVersion.V2_30)).isFalse()
        assertThat(dhisVersionManager.isVersion(DHISVersion.V2_31)).isTrue()
        assertThat(dhisVersionManager.isVersion(DHISVersion.V2_32)).isFalse()
        assertThat(dhisVersionManager.isVersion(DHISVersion.V2_33)).isFalse()
        assertThat(dhisVersionManager.isVersion(DHISVersion.UNKNOWN)).isFalse()

        assertThat(dhisVersionManager.isGreaterThan(DHISVersion.V2_30)).isTrue()
        assertThat(dhisVersionManager.isGreaterThan(DHISVersion.V2_31)).isFalse()
        assertThat(dhisVersionManager.isGreaterThan(DHISVersion.V2_32)).isFalse()
        assertThat(dhisVersionManager.isGreaterThan(DHISVersion.V2_33)).isFalse()
        assertThat(dhisVersionManager.isGreaterThan(DHISVersion.UNKNOWN)).isFalse()

        assertThat(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_30)).isTrue()
        assertThat(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_31)).isTrue()
        assertThat(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_32)).isFalse()
        assertThat(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_33)).isFalse()
        assertThat(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.UNKNOWN)).isFalse()
    }

    @Test
    fun compare_version_when_unknown() {
        dhisVersionManager.setBypassVersion(true)
        whenever(systemInfo.version()).thenReturn(DHISVersion.UNKNOWN.name)

        assertThat(dhisVersionManager.isVersion(DHISVersion.V2_33)).isFalse()
        assertThat(dhisVersionManager.isVersion(DHISVersion.UNKNOWN)).isTrue()

        assertThat(dhisVersionManager.isGreaterThan(DHISVersion.V2_32)).isTrue()
        assertThat(dhisVersionManager.isGreaterThan(DHISVersion.V2_33)).isTrue()
        assertThat(dhisVersionManager.isGreaterThan(DHISVersion.UNKNOWN)).isFalse()

        assertThat(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_32)).isTrue()
        assertThat(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_33)).isTrue()
        assertThat(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.UNKNOWN)).isTrue()
    }

    @Test(expected = D2Error::class)
    fun throw_invalid_version() {
        whenever(systemInfo.version()).thenReturn("Invalid_version")
        dhisVersionManager.getVersion()
    }

    @Test
    fun should_get_patch_version() {
        whenever(systemInfo.version()).thenReturn("2.40.0")
        assertThat(dhisVersionManager.getPatchVersion()).isEqualTo(DHISPatchVersion.V2_40_0)
    }

    @Test
    fun return_null_if_unknown_patch_version() {
        whenever(systemInfo.version()).thenReturn("2.39.5.1")
        assertThat(dhisVersionManager.getPatchVersion()).isNull()
    }

    @Test
    fun return_unknown_if_unknown_patch_version_and_bypass_dhis2_version_is_true() {
        dhisVersionManager.setBypassVersion(true)
        whenever(systemInfo.version()).thenReturn("2.47.59")
        assertThat(dhisVersionManager.getPatchVersion()).isEqualTo(DHISPatchVersion.UNKNOWN)
    }

    @Test
    fun should_return_sms_version() {
        whenever(systemInfo.version()).thenReturn("2.39.5.1")
        assertThat(dhisVersionManager.getSmsVersion()).isEqualTo(SMSVersion.V2)
    }

    @Test
    fun return_null_if_none_sms_version() {
        whenever(systemInfo.version()).thenReturn("2.31.7")
        assertThat(dhisVersionManager.getSmsVersion()).isNull()
    }
}
