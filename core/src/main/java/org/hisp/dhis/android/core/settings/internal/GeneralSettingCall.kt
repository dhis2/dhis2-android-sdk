/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.settings.internal

import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.call.internal.CompletableProvider
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.settings.GeneralSettings
import java.net.HttpURLConnection
import javax.inject.Inject

@Reusable
internal class GeneralSettingCall @Inject constructor(
    private val generalSettingHandler: Handler<GeneralSettings>,
    private val settingAppService: SettingAppService,
    private val apiCallExecutor: RxAPICallExecutor,
    private val appVersionManager: SettingsAppVersionManager
) : CompletableProvider {

    private var cachedValue: GeneralSettings? = null

    override fun getCompletable(storeError: Boolean): Completable {
        return Completable
            .fromSingle(download(storeError))
            .onErrorComplete()
    }

    fun download(storeError: Boolean): Single<GeneralSettings> {
        return fetch(storeError)
            .doOnSuccess { generalSettings: GeneralSettings -> process(generalSettings) }
            .doOnError { throwable: Throwable ->
                if (throwable is D2Error && throwable.httpErrorCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    process(null)
                } else {
                    throw throwable
                }
            }
    }

    fun fetch(storeError: Boolean, acceptCache: Boolean = false): Single<GeneralSettings> {
        val version = appVersionManager.getVersion()
        return cachedValue?.let {
            if (acceptCache) Single.just(it) else null
        } ?: apiCallExecutor.wrapSingle(settingAppService.generalSettings(version), storeError)
    }

    fun process(item: GeneralSettings?) {
        cachedValue = item
        val generalSettingsList = listOfNotNull(item)
        generalSettingHandler.handleMany(generalSettingsList)
    }

    fun isDatabaseEncrypted(): Single<Boolean> {
        val version = appVersionManager.getVersion()
        return apiCallExecutor.wrapSingle(settingAppService.generalSettings(version), false)
            .map { obj: GeneralSettings -> obj.encryptDB() }
    }
}
