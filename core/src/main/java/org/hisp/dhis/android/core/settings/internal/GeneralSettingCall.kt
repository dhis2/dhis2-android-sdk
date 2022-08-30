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
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.settings.GeneralSettings

@Reusable
internal class GeneralSettingCall @Inject constructor(
    private val generalSettingHandler: Handler<GeneralSettings>,
    private val settingAppService: SettingAppService,
    private val apiCallExecutor: RxAPICallExecutor,
    private val appVersionManager: SettingsAppInfoManager
) : BaseSettingCall<GeneralSettings>() {

    private var cachedValue: GeneralSettings? = null

    override fun fetch(storeError: Boolean): Single<GeneralSettings> {
        return appVersionManager.getDataStoreVersion().flatMap { version ->
            apiCallExecutor.wrapSingle(settingAppService.generalSettings(version), storeError = storeError)
        }
    }

    fun fetch(storeError: Boolean, acceptCache: Boolean = false): Single<GeneralSettings> {
        return when {
            cachedValue != null && acceptCache -> Single.just(cachedValue)
            else -> fetch(storeError)
        }
    }

    override fun process(item: GeneralSettings?) {
        cachedValue = item
        val generalSettingsList = listOfNotNull(item)
        generalSettingHandler.handleMany(generalSettingsList)
    }

    fun isDatabaseEncrypted(): Single<Boolean> {
        // TODO Should we decrypt the database if the settings app is uninstalled?
        return appVersionManager.updateAppVersion()
            .flatMap { appVersionManager.getDataStoreVersion() }
            .flatMap { version ->
                apiCallExecutor.wrapSingle(settingAppService.generalSettings(version), storeError = false)
            }
            .map { obj: GeneralSettings -> obj.encryptDB() }
    }
}
