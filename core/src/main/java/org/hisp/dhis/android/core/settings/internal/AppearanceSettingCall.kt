package org.hisp.dhis.android.core.settings.internal

import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.call.internal.CompletableProvider
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.settings.AppearanceSettings
import org.hisp.dhis.android.core.settings.FilterConfig
import javax.inject.Inject

@Reusable
class AppearanceSettingCall @Inject constructor(
    private val databaseAdapter: DatabaseAdapter,
    private val appearanceSettingHandler: Handler<FilterConfig>,
    private val androidSettingService: SettingService,
    private val apiCallExecutor: RxAPICallExecutor
) : CompletableProvider {
    override fun getCompletable(storeError: Boolean): Completable {
        return Completable
            .fromSingle(downloadAndPersist(storeError))
            .onErrorComplete()
    }

    private fun downloadAndPersist(storeError: Boolean): Single<AppearanceSettings> {
        TODO("Not yet implemented")
    }
}