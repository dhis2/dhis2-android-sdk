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

import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.settings.*
import org.hisp.dhis.android.core.settings.AppearanceSettings

internal class SettingAppService @Inject constructor(
    private val settingService: SettingService
) {

    fun appMetadata(): Single<List<AppMetadata>> {
        return settingService.appMetadata(SETTINGS_APP_FILTER)
    }

    fun info(): Single<SettingsAppInfo> {
        return settingService.settingsAppInfo("$ANDROID_APP_NAMESPACE_V2/info")
    }

    fun generalSettings(version: SettingsAppDataStoreVersion): Single<GeneralSettings> {
        val key = when (version) {
            SettingsAppDataStoreVersion.V1_1 -> "general_settings"
            else -> "generalSettings"
        }

        return settingService.generalSettings("${getNamespace(version)}/$key")
    }

    fun dataSetSettings(version: SettingsAppDataStoreVersion): Single<DataSetSettings> {
        return settingService.dataSetSettings("${getNamespace(version)}/dataSet_settings")
    }

    fun programSettings(version: SettingsAppDataStoreVersion): Single<ProgramSettings> {
        return settingService.programSettings("${getNamespace(version)}/program_settings")
    }

    fun synchronizationSettings(version: SettingsAppDataStoreVersion): Single<SynchronizationSettings> {
        return settingService.synchronizationSettings("${getNamespace(version)}/synchronization")
    }

    fun appearanceSettings(version: SettingsAppDataStoreVersion): Single<AppearanceSettings> {
        return settingService.appearanceSettings("${getNamespace(version)}/appearance")
    }

    fun analyticsSettings(version: SettingsAppDataStoreVersion): Single<AnalyticsSettings> {
        return settingService.analyticsSettings("${getNamespace(version)}/analytics")
    }

    private fun getNamespace(version: SettingsAppDataStoreVersion): String {
        return when (version) {
            SettingsAppDataStoreVersion.V1_1 -> ANDROID_APP_NAMESPACE_V1
            else -> ANDROID_APP_NAMESPACE_V2
        }
    }

    companion object {
        const val ANDROID_APP_NAMESPACE_V1 = "dataStore/ANDROID_SETTING_APP"
        const val ANDROID_APP_NAMESPACE_V2 = "dataStore/ANDROID_SETTINGS_APP"
        const val SETTINGS_APP_NAME = "Android Settings"
        const val SETTINGS_APP_FILTER = "name:eq:$SETTINGS_APP_NAME"
    }
}
