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
import org.hisp.dhis.android.core.settings.SynchronizationSettings

@Reusable
internal class SynchronizationSettingCall @Inject constructor(
    private val synchronizationSettingHandler: Handler<SynchronizationSettings>,
    private val settingAppService: SettingAppService,
    private val apiCallExecutor: RxAPICallExecutor,
    private val generalSettingCall: GeneralSettingCall,
    private val dataSetSettingCall: DataSetSettingCall,
    private val programSettingCall: ProgramSettingCall,
    private val appVersionManager: SettingsAppInfoManager
) : BaseSettingCall<SynchronizationSettings>() {

    override fun fetch(storeError: Boolean): Single<SynchronizationSettings> {
        return appVersionManager.getDataStoreVersion().flatMap { version ->
            when (version) {
                SettingsAppDataStoreVersion.V1_1 -> {
                    val generalSettings = tryOrNull(generalSettingCall.fetch(storeError, acceptCache = true))
                    val dataSetSettings = tryOrNull(dataSetSettingCall.fetch(storeError))
                    val programSettings = tryOrNull(programSettingCall.fetch(storeError))

                    if (generalSettings == null && dataSetSettings == null && programSettings == null) {
                        Single.error(
                            D2Error.builder()
                                .errorDescription("Synchronization settings not found")
                                .errorCode(D2ErrorCode.URL_NOT_FOUND)
                                .httpErrorCode(HttpURLConnection.HTTP_NOT_FOUND)
                                .build()
                        )
                    } else {
                        Single.just(
                            SynchronizationSettings.builder()
                                .dataSync(generalSettings?.dataSync())
                                .metadataSync(generalSettings?.metadataSync())
                                .dataSetSettings(dataSetSettings)
                                .programSettings(programSettings)
                                .build()
                        )
                    }
                }
                SettingsAppDataStoreVersion.V2_0 -> {
                    apiCallExecutor.wrapSingle(settingAppService.synchronizationSettings(version), storeError)
                }
            }
        }
    }

    override fun process(item: SynchronizationSettings?) {
        val syncSettingsList = listOfNotNull(item)
        synchronizationSettingHandler.handleMany(syncSettingsList)
    }

    private fun <T> tryOrNull(single: Single<T>): T? {
        return try { single.blockingGet() } catch (e: java.lang.Exception) { null }
    }
}
