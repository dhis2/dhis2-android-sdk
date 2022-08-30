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

import dagger.Reusable
import io.reactivex.Single
import java.net.HttpURLConnection
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.settings.AppearanceSettings
import org.hisp.dhis.android.core.settings.FilterSetting
import org.hisp.dhis.android.core.settings.ProgramConfigurationSetting

@Reusable
internal class AppearanceSettingCall @Inject constructor(
    private val filterSettingHandler: Handler<FilterSetting>,
    private val programConfigurationSettingHandler: Handler<ProgramConfigurationSetting>,
    private val settingAppService: SettingAppService,
    private val apiCallExecutor: RxAPICallExecutor,
    private val appVersionManager: SettingsAppInfoManager
) : BaseSettingCall<AppearanceSettings>() {

    override fun fetch(storeError: Boolean): Single<AppearanceSettings> {
        return appVersionManager.getDataStoreVersion().flatMap { version ->
            when (version) {
                SettingsAppDataStoreVersion.V1_1 -> {
                    Single.error(
                        D2Error.builder()
                            .errorDescription("Appearance settings not found")
                            .errorCode(D2ErrorCode.URL_NOT_FOUND)
                            .httpErrorCode(HttpURLConnection.HTTP_NOT_FOUND)
                            .build()
                    )
                }
                SettingsAppDataStoreVersion.V2_0 -> {
                    apiCallExecutor.wrapSingle(settingAppService.appearanceSettings(version), storeError)
                }
            }
        }
    }

    override fun process(item: AppearanceSettings?) {

        val filterSettingsList = item?.let {
            SettingsAppHelper.getFilterSettingsList(it)
        } ?: emptyList()
        filterSettingHandler.handleMany(filterSettingsList)

        val programConfigurationSettings = item?.let {
            SettingsAppHelper.getProgramConfigurationSettingList(it)
        } ?: emptyList()
        programConfigurationSettingHandler.handleMany(programConfigurationSettings)
    }
}
