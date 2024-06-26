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

import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.settings.GeneralSettings
import org.hisp.dhis.android.core.systeminfo.internal.DHISVersionManagerImpl
import org.koin.core.annotation.Singleton

@Singleton
internal class GeneralSettingCall(
    private val generalSettingHandler: GeneralSettingHandler,
    private val settingAppService: SettingAppService,
    private val appVersionManager: SettingsAppInfoManager,
    coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val versionManager: DHISVersionManagerImpl,
) : BaseSettingCall<GeneralSettings>(coroutineAPICallExecutor) {

    private var cachedValue: GeneralSettings? = null

    override suspend fun tryFetch(storeError: Boolean): Result<GeneralSettings, D2Error> {
        return coroutineAPICallExecutor.wrap(storeError = storeError) {
            settingAppService.generalSettings(appVersionManager.getDataStoreVersion())
        }
    }

    suspend fun fetch(storeError: Boolean, acceptCache: Boolean = false): Result<GeneralSettings, D2Error> {
        return when {
            cachedValue != null && acceptCache -> Result.Success(cachedValue!!)
            else -> fetch(storeError)
        }
    }

    override fun process(item: GeneralSettings?) {
        cachedValue = item
        val generalSettingsList = listOfNotNull(item)
        versionManager.setBypassVersion(item?.bypassDHIS2VersionCheck())
        generalSettingHandler.handleMany(generalSettingsList)
    }

    suspend fun isDatabaseEncrypted(): Boolean {
        // TODO Should we decrypt the database if the settings app is uninstalled?
        appVersionManager.updateAppVersion()
        return coroutineAPICallExecutor.wrap(storeError = false) {
            settingAppService.generalSettings(appVersionManager.getDataStoreVersion())
        }.getOrThrow().also { versionManager.setBypassVersion(it.bypassDHIS2VersionCheck()) }.encryptDB()
    }
}
