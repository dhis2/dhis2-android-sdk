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
package org.hisp.dhis.android.core.settings

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.settings.SystemSetting.SystemSettingKey
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class SettingsModuleMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun allow_access_to_system_setting() {
        var systemSettings: List<SystemSetting?> = d2.settingModule().systemSetting().blockingGet()
        Truth.assertThat(systemSettings.size).isEqualTo(3)
    }

    @Test
    fun allow_access_to_system_setting_filtered_by_key() {
        var systemSettingsFlag = d2.settingModule().systemSetting().byKey()
            .eq(SystemSettingKey.FLAG).blockingGet()
        Truth.assertThat(systemSettingsFlag.size).isEqualTo(1)
        Truth.assertThat(systemSettingsFlag.get(0).value()).isEqualTo("sierra_leone")

        var systemSettingsStyle = d2.settingModule().systemSetting().byKey()
            .eq(SystemSettingKey.STYLE).blockingGet()
        Truth.assertThat(systemSettingsStyle.get(0).value()).isEqualTo("light_blue/light_blue.css")

        var systemSettingsDefaultBaseMap = d2.settingModule().systemSetting().byKey()
            .eq(SystemSettingKey.DEFAULT_BASE_MAP).blockingGet()
        Truth.assertThat(systemSettingsDefaultBaseMap.get(0).value()).isEqualTo("keyDefaultBaseMap")
    }

    @Test
    fun allow_access_to_system_setting_filtered_by_value() {
        var systemSettingsFlag = d2.settingModule().systemSetting().byValue()
            .eq("sierra_leone").blockingGet()
        Truth.assertThat(systemSettingsFlag.size).isEqualTo(1)
        Truth.assertThat(systemSettingsFlag.get(0).key()).isEqualTo(SystemSettingKey.FLAG)

        var systemSettingsStyle = d2.settingModule().systemSetting().byValue()
            .eq("light_blue/light_blue.css").blockingGet()
        Truth.assertThat(systemSettingsStyle.get(0).key()).isEqualTo(SystemSettingKey.STYLE)

        var systemSettingsDefaultBaseMap = d2.settingModule().systemSetting().byValue()
            .eq("keyDefaultBaseMap").blockingGet()
        Truth.assertThat(systemSettingsDefaultBaseMap.get(0).key())
            .isEqualTo(SystemSettingKey.DEFAULT_BASE_MAP)
    }

    @Test
    fun allow_access_to_flag_settings() {
        var systemSetting = d2.settingModule().systemSetting().flag().blockingGet()
        Truth.assertThat(systemSetting!!.key()).isEqualTo(SystemSettingKey.FLAG)
        Truth.assertThat(systemSetting.value()).isEqualTo("sierra_leone")
    }

    @Test
    fun allow_access_to_style_settings() {
        var systemSetting = d2.settingModule().systemSetting().style().blockingGet()
        Truth.assertThat(systemSetting!!.key()).isEqualTo(SystemSettingKey.STYLE)
        Truth.assertThat(systemSetting.value()).isEqualTo("light_blue/light_blue.css")
    }

    @Test
    fun allow_access_to_default_base_map_settings() {
        var systemSetting = d2.settingModule().systemSetting().defaultBaseMap().blockingGet()
        Truth.assertThat(systemSetting!!.key()).isEqualTo(SystemSettingKey.DEFAULT_BASE_MAP)
        Truth.assertThat(systemSetting.value()).isEqualTo("keyDefaultBaseMap")
    }
}
