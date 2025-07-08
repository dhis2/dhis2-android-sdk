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
package org.hisp.dhis.android.core.settings

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.network.systemsettings.SystemSettingsDTO
import org.junit.Test

class SystemSettingsShould : BaseObjectKotlinxShould("settings/system_settings.json"), ObjectShould {
    @Test
    override fun map_from_json_string() {
        val settingsDTO = deserialize(SystemSettingsDTO.serializer())
        val settingsSplitted = settingsDTO.toDomainSplitted()
        assertThat(settingsSplitted.get(0).key()).isEqualTo(SystemSetting.SystemSettingKey.FLAG)
        assertThat(settingsSplitted.get(0).value()).isEqualTo("sierra_leone")
        assertThat(settingsSplitted.get(1).key()).isEqualTo(SystemSetting.SystemSettingKey.STYLE)
        assertThat(settingsSplitted.get(1).value()).isEqualTo("light_blue/light_blue.css")
        assertThat(settingsSplitted.get(2).key()).isEqualTo(SystemSetting.SystemSettingKey.DEFAULT_BASE_MAP)
        assertThat(settingsSplitted.get(2).value()).isEqualTo("keyDefaultBaseMap")

        val settingsBingMaps = settingsDTO.toDomainBingMapsApiKey()
        assertThat(settingsBingMaps.key()).isEqualTo(SystemSetting.SystemSettingKey.BING_BASE_MAP)
        assertThat(settingsBingMaps.value()).isEqualTo("keyBingMapsApiKey")
    }
}
