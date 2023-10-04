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
package org.hisp.dhis.android.core.settings.internal

import dagger.Reusable
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.settings.AnalyticsDhisVisualization
import org.hisp.dhis.android.core.settings.AnalyticsSettings
import java.net.HttpURLConnection
import javax.inject.Inject

@Reusable
internal class AnalyticsSettingCall @Inject constructor(
    private val analyticsTeiSettingHandler: AnalyticsTeiSettingHandler,
    private val analyticsDhisVisualizationsSettingHandler: AnalyticsDhisVisualizationSettingHandler,
    private val settingAppService: SettingAppService,
    coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val appVersionManager: SettingsAppInfoManager,
) : BaseSettingCall<AnalyticsSettings>(coroutineAPICallExecutor) {

    override suspend fun fetch(storeError: Boolean): Result<AnalyticsSettings, D2Error> {
        return when (val version = appVersionManager.getDataStoreVersion()) {
            SettingsAppDataStoreVersion.V1_1 -> {
                Result.Failure(
                    D2Error.builder()
                        .errorDescription("Analytics settings not found")
                        .errorCode(D2ErrorCode.URL_NOT_FOUND)
                        .httpErrorCode(HttpURLConnection.HTTP_NOT_FOUND)
                        .build()
                )
            }

            else -> {
                coroutineAPICallExecutor.wrap(storeError = storeError) {
                    settingAppService.analyticsSettings(version)
                }
            }
        }
    }

    override fun process(item: AnalyticsSettings?) {
        val analyticsTeiSettingList = item?.tei() ?: emptyList()
        analyticsTeiSettingHandler.handleMany(analyticsTeiSettingList)

        val analyticsDhisVisualizations: List<AnalyticsDhisVisualization> = item?.let {
            SettingsAppHelper.getAnalyticsDhisVisualizations(it)
        } ?: emptyList()

        analyticsDhisVisualizationsSettingHandler.handleMany(analyticsDhisVisualizations)
    }
}
