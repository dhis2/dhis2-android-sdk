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

package org.hisp.dhis.android.core.settings;

import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class SettingsModuleMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void allow_access_to_system_setting() {
        List<SystemSetting> systemSettings = d2.settingModule().systemSetting().blockingGet();
        assertThat(systemSettings.size()).isEqualTo(2);
    }

    @Test
    public void allow_access_to_system_setting_filtered_by_key() {
        List<SystemSetting> systemSettingsFlag = d2.settingModule().systemSetting().byKey()
                .eq(SystemSetting.SystemSettingKey.FLAG).blockingGet();
        assertThat(systemSettingsFlag.size()).isEqualTo(1);
        assertThat(systemSettingsFlag.get(0).value()).isEqualTo("sierra_leone");

        List<SystemSetting> systemSettingsStyle = d2.settingModule().systemSetting().byKey()
                .eq(SystemSetting.SystemSettingKey.STYLE).blockingGet();
        assertThat(systemSettingsStyle.get(0).value()).isEqualTo("light_blue/light_blue.css");
    }

    @Test
    public void allow_access_to_system_setting_filtered_by_value() {
        List<SystemSetting> systemSettingsFlag = d2.settingModule().systemSetting().byValue()
                .eq("sierra_leone").blockingGet();
        assertThat(systemSettingsFlag.size()).isEqualTo(1);
        assertThat(systemSettingsFlag.get(0).key()).isEqualTo(SystemSetting.SystemSettingKey.FLAG);

        List<SystemSetting> systemSettingsStyle = d2.settingModule().systemSetting().byValue()
                .eq("light_blue/light_blue.css").blockingGet();
        assertThat(systemSettingsStyle.get(0).key()).isEqualTo(SystemSetting.SystemSettingKey.STYLE);
    }

    @Test
    public void allow_access_to_flag_settings() {
        SystemSetting systemSetting = d2.settingModule().systemSetting().flag().blockingGet();
        assertThat(systemSetting.key()).isEqualTo(SystemSetting.SystemSettingKey.FLAG);
        assertThat(systemSetting.value()).isEqualTo("sierra_leone");
    }

    @Test
    public void allow_access_to_style_settings() {
        SystemSetting systemSetting = d2.settingModule().systemSetting().style().blockingGet();
        assertThat(systemSetting.key()).isEqualTo(SystemSetting.SystemSettingKey.STYLE);
        assertThat(systemSetting.value()).isEqualTo("light_blue/light_blue.css");
    }
}