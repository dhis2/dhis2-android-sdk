package org.hisp.dhis.android.core.settings.internal

import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.call.internal.CompletableProvider
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.settings.AppearanceSettings
import org.hisp.dhis.android.core.settings.FilterConfig
import java.net.HttpURLConnection
import javax.inject.Inject

@Reusable
internal class AppearanceSettingCall @Inject constructor(
    private val appearanceSettingHandler: Handler<FilterConfig>,
    private val settingAppService: SettingAppService,
    private val apiCallExecutor: RxAPICallExecutor,
    private val appVersionManager: SettingsAppVersionManager
) : CompletableProvider {

    override fun getCompletable(storeError: Boolean): Completable {
        return Completable
            .fromSingle(download(storeError))
            .onErrorComplete()
    }

    private fun download(storeError: Boolean): Single<AppearanceSettings> {
        return fetch(storeError)
            .doOnSuccess { appearanceSettings: AppearanceSettings -> process(appearanceSettings) }
            .doOnError { throwable: Throwable ->
                if (throwable is D2Error && throwable.httpErrorCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    process(null)
                }
            }
    }

    fun fetch(storeError: Boolean): Single<AppearanceSettings> {
        val version = appVersionManager.getVersion()
        return apiCallExecutor.wrapSingle(settingAppService.appearanceSettings(version), storeError)
    }

    fun process(item: AppearanceSettings?) {
        val appearanceSettingsList = item?.let {
            SettingsAppHelper.getAppearanceSettings(it)
        } ?: emptyList()
        appearanceSettingHandler.handleMany(appearanceSettingsList)
    }

    //TODO check version
}