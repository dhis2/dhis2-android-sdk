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
import org.hisp.dhis.android.core.settings.CompletionSpinner
import org.hisp.dhis.android.core.settings.FilterSetting

@Reusable
internal class AppearanceSettingCall @Inject constructor(
    private val filterSettingHandler: Handler<FilterSetting>,
    private val completionSpinnerHandler: Handler<CompletionSpinner>,
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

        val completionSpinnerSettings = item?.let {
            SettingsAppHelper.getCompletionSpinnerList(it)
        } ?: emptyList()
        completionSpinnerHandler.handleMany(completionSpinnerSettings)
    }
}
