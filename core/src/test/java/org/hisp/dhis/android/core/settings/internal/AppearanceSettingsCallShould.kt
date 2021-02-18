package org.hisp.dhis.android.core.settings.internal

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.data.maintenance.D2ErrorSamples
import org.hisp.dhis.android.core.settings.AppearanceSettings
import org.hisp.dhis.android.core.settings.CompletionSpinner
import org.hisp.dhis.android.core.settings.FilterSetting
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AppearanceSettingsCallShould {

    private val filterSettingHandler: Handler<FilterSetting> = mock()
    private val completionSpinnerHandler: Handler<CompletionSpinner> = mock()
    private val service: SettingAppService = mock()
    private val apiCallExecutor: RxAPICallExecutor = mock()
    private val appVersionManager: SettingsAppInfoManager = mock()

    private val appearanceSettings: AppearanceSettings = mock()
    private val appearanceSettingsSingle: Single<AppearanceSettings> = Single.just(appearanceSettings)

    private lateinit var appearanceSettingsCall: AppearanceSettingCall

    @Before
    fun setUp() {
        whenever(service.appearanceSettings(any())) doReturn appearanceSettingsSingle

        appearanceSettingsCall = AppearanceSettingCall(
            filterSettingHandler,
            completionSpinnerHandler,
            service,
            apiCallExecutor,
            appVersionManager
        )
    }

    @Test
    fun call_appearances_endpoint_if_version_1() {
        whenever(appVersionManager.getDataStoreVersion()) doReturn Single.just(SettingsAppDataStoreVersion.V1_1)

        appearanceSettingsCall.getCompletable(false).blockingAwait()

        verify(service, never()).appearanceSettings(any())
    }

    @Test
    fun call_appearances_endpoint_if_version_2() {
        whenever(apiCallExecutor.wrapSingle(appearanceSettingsSingle, false)) doReturn appearanceSettingsSingle
        whenever(appVersionManager.getDataStoreVersion()) doReturn Single.just(SettingsAppDataStoreVersion.V2_0)

        appearanceSettingsCall.getCompletable(false).blockingAwait()

        verify(service).appearanceSettings(any())
    }

    @Test
    fun default_to_empty_collection_if_not_found() {
        whenever(appVersionManager.getDataStoreVersion()) doReturn Single.just(SettingsAppDataStoreVersion.V2_0)
        whenever(apiCallExecutor.wrapSingle(appearanceSettingsSingle, false)) doReturn
            Single.error(D2ErrorSamples.notFound())

        appearanceSettingsCall.getCompletable(false).blockingAwait()

        verify(filterSettingHandler).handleMany(emptyList())
        verifyNoMoreInteractions(filterSettingHandler)
        verify(completionSpinnerHandler).handleMany(emptyList())
        verifyNoMoreInteractions(completionSpinnerHandler)
    }
}
