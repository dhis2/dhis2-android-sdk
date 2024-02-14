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
import org.hisp.dhis.android.core.settings.SystemSetting.SystemSettingKey
import org.hisp.dhis.android.core.settings.SystemSettings
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SystemSettingSplitterShould {
    private val settings: SystemSettings = SystemSettings(
        keyFlag = "aFlag",
        keyStyle = "aStyle",
        keyDefaultBaseMap = "aDefaultBaseMap",
        keyBingMapsApiKey = null,
    )

    private val systemSettingsSplitter = SystemSettingsSplitter()

    @Test
    fun build_flag_setting() {
        val settingList = systemSettingsSplitter.splitSettings(settings)
        settingList[0].let { flag ->
            assertThat(flag.key()).isEqualTo(SystemSettingKey.FLAG)
            assertThat(flag.value()).isEqualTo("aFlag")
        }
    }

    @Test
    fun build_style_setting() {
        val settingList = systemSettingsSplitter.splitSettings(settings)
        settingList[1].let { style ->
            assertThat(style.key()).isEqualTo(SystemSettingKey.STYLE)
            assertThat(style.value()).isEqualTo("aStyle")
        }
    }

    @Test
    fun build_default_base_map_setting() {
        val settingList = systemSettingsSplitter.splitSettings(settings)
        settingList[2].let { style ->
            assertThat(style.key()).isEqualTo(SystemSettingKey.DEFAULT_BASE_MAP)
            assertThat(style.value()).isEqualTo("aDefaultBaseMap")
        }
    }
}
