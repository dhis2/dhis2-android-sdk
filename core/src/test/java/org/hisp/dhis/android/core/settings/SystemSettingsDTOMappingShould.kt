/*
 *  Copyright (c) 2004-2025, University of Oslo
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
import org.hisp.dhis.android.core.settings.SystemSetting.SystemSettingKey
import org.hisp.dhis.android.network.systemsettings.SystemSettingsDTO
import org.junit.Test

class SystemSettingsDTOMappingShould {

    private fun buildDTO(
        keyStyle: String? = null,
        keyCustomColorMobile: String? = null,
    ) = SystemSettingsDTO(
        keyFlag = null,
        keyStyle = keyStyle,
        keyCustomColorMobile = keyCustomColorMobile,
        keyDefaultBaseMap = null,
        keyBingMapsApiKey = null,
        keyAzureMapsApiKey = null,
        analyticsFinancialYearStart = null,
        analyticsWeeklyStart = null,
    )

    private fun List<SystemSetting>.customColor(): SystemSetting =
        first { it.key() == SystemSettingKey.CUSTOM_COLOR }

    @Suppress("DEPRECATION")
    private fun List<SystemSetting>.style(): SystemSetting =
        first { it.key() == SystemSettingKey.STYLE }

    @Test
    fun fallback_to_style_mapping_when_custom_color_is_null() {
        val settings = buildDTO("green/green.css").toDomainSplitted()
        assertThat(settings.customColor().value()).isEqualTo("#218C51")
        assertThat(settings.style().value()).isEqualTo("green/green.css")
    }

    @Test
    fun return_null_when_both_are_null() {
        val settings = buildDTO().toDomainSplitted()
        assertThat(settings.customColor().value()).isNull()
        assertThat(settings.style().value()).isNull()
    }

    @Test
    fun return_null_color_and_default_style_when_custom_color_is_empty() {
        val settings = buildDTO("green/green.css", "").toDomainSplitted()
        assertThat(settings.customColor().value()).isNull()
        assertThat(settings.style().value()).isEqualTo("light_blue/light_blue.css")
    }

    @Test
    fun prefer_custom_color_over_style_mapping() {
        val settings = buildDTO("green/green.css", "#FF0000").toDomainSplitted()
        assertThat(settings.customColor().value()).isEqualTo("#FF0000")
        assertThat(settings.style().value()).isEqualTo("light_blue/light_blue.css")
    }

    @Test
    fun map_known_color_to_corresponding_style() {
        val settings = buildDTO("myanmar/myanmar.css", "#218C51").toDomainSplitted()
        assertThat(settings.customColor().value()).isEqualTo("#218C51")
        assertThat(settings.style().value()).isEqualTo("green/green.css")
    }

    @Test
    fun map_unknown_color_to_default_style() {
        val settings = buildDTO(keyCustomColorMobile = "#FF0000").toDomainSplitted()
        assertThat(settings.customColor().value()).isEqualTo("#FF0000")
        assertThat(settings.style().value()).isEqualTo("light_blue/light_blue.css")
    }
}
