/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.settings;

import org.hisp.dhis.android.core.data.database.SyncedDatabaseMockIntegrationShould;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class SettingsModuleMockIntegrationShould extends SyncedDatabaseMockIntegrationShould {

    @Test
    public void allow_access_to_system_setting() {
        List<SystemSetting> systemSettings = d2.systemSettingModule().systemSetting.get();
        assertThat(systemSettings.size(), is(2));
    }

    @Test
    public void allow_access_to_system_setting_filtered_by_key() {
        List<SystemSetting> systemSettingsFlag = d2.systemSettingModule().systemSetting.byKey()
                .eq(SystemSetting.SystemSettingKey.FLAG).get();
        assertThat(systemSettingsFlag.size(), is(1));
        assertThat(systemSettingsFlag.get(0).value(), is("sierra_leone"));

        List<SystemSetting> systemSettingsStyle = d2.systemSettingModule().systemSetting.byKey()
                .eq(SystemSetting.SystemSettingKey.STYLE).get();
        assertThat(systemSettingsStyle.get(0).value(), is("light_blue/light_blue.css"));
    }

    @Test
    public void allow_access_to_system_setting_filtered_by_value() {
        List<SystemSetting> systemSettingsFlag = d2.systemSettingModule().systemSetting.byValue()
                .eq("sierra_leone").get();
        assertThat(systemSettingsFlag.size(), is(1));
        assertThat(systemSettingsFlag.get(0).key(), is(SystemSetting.SystemSettingKey.FLAG));

        List<SystemSetting> systemSettingsStyle = d2.systemSettingModule().systemSetting.byValue()
                .eq("light_blue/light_blue.css").get();
        assertThat(systemSettingsStyle.get(0).key(), is(SystemSetting.SystemSettingKey.STYLE));
    }

    @Test
    public void allow_access_to_flag_settings() {
        SystemSetting systemSetting = d2.systemSettingModule().systemSetting.flag().get();
        assertThat(systemSetting.key(), is(SystemSetting.SystemSettingKey.FLAG));
        assertThat(systemSetting.value(), is("sierra_leone"));
    }

    @Test
    public void allow_access_to_style_settings() {
        SystemSetting systemSetting = d2.systemSettingModule().systemSetting.style().get();
        assertThat(systemSetting.key(), is(SystemSetting.SystemSettingKey.STYLE));
        assertThat(systemSetting.value(), is("light_blue/light_blue.css"));
    }
}