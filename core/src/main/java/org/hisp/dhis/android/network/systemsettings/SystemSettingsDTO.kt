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

package org.hisp.dhis.android.network.systemsettings

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.settings.SystemSetting
import org.hisp.dhis.android.core.settings.SystemSetting.SystemSettingKey

@Serializable
internal data class SystemSettingsDTO(
    val keyFlag: String?,
    val keyStyle: String?,
    val keyCustomColorMobile: String?,
    val keyDefaultBaseMap: String?,
    val keyBingMapsApiKey: String?,
    val keyAzureMapsApiKey: String?,
    val analyticsFinancialYearStart: String?,
    val analyticsWeeklyStart: String?,
) {

    @Suppress("DEPRECATION")
    fun toDomainSplitted(): List<SystemSetting> = listOf(
        buildSetting(SystemSettingKey.FLAG, keyFlag),
        buildSetting(SystemSettingKey.STYLE, resolveStyle()),
        buildSetting(SystemSettingKey.CUSTOM_COLOR, resolveCustomColor()),
        buildSetting(SystemSettingKey.DEFAULT_BASE_MAP, keyDefaultBaseMap),
        buildSetting(SystemSettingKey.ANALYTICS_FINANCIAL_YEAR_START, analyticsFinancialYearStart),
        buildSetting(SystemSettingKey.ANALYTICS_WEEK_START, analyticsWeeklyStart),
    )

    fun toDomainBingMapsApiKey(): SystemSetting = buildSetting(SystemSettingKey.BING_BASE_MAP, keyBingMapsApiKey)

    fun toDomainAzureMapsApiKey(): SystemSetting = buildSetting(SystemSettingKey.BING_BASE_MAP, keyAzureMapsApiKey)

    private fun resolveStyle(): String? = when {
        keyCustomColorMobile == null -> keyStyle
        keyCustomColorMobile.isEmpty() -> DEFAULT_STYLE
        else -> colorToStyle(keyCustomColorMobile)
    }

    private fun resolveCustomColor(): String? = when {
        keyCustomColorMobile == null -> styleToColor(keyStyle)
        keyCustomColorMobile.isEmpty() -> null
        else -> keyCustomColorMobile
    }

    internal enum class Theme(val keyword: String, val style: String, val color: String) {
        GREEN("green", "green/green.css", "#218C51"),
        INDIA("india", "india/india.css", "#EA5911"),
        MYANMAR("myanmar", "myanmar/myanmar.css", "#8C2121"),
    }

    companion object {
        private const val DEFAULT_STYLE = "light_blue/light_blue.css"
        private const val DEFAULT_COLOR = "#007DEB"

        internal fun styleToColor(style: String?): String? {
            if (style.isNullOrEmpty()) return null
            return Theme.entries.firstOrNull { style.contains(it.keyword) }?.color ?: DEFAULT_COLOR
        }

        internal fun colorToStyle(color: String): String =
            Theme.entries.firstOrNull { it.color.equals(color, ignoreCase = true) }?.style ?: DEFAULT_STYLE

        private fun buildSetting(key: SystemSettingKey, value: String?): SystemSetting =
            SystemSetting.builder().key(key).value(value).build()
    }
}
