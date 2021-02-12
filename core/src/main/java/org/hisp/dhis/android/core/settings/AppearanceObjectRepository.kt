package org.hisp.dhis.android.core.settings

import dagger.Reusable
import org.hisp.dhis.android.core.arch.call.internal.CompletableProvider
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadOnlyAnyObjectWithDownloadRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository
import org.hisp.dhis.android.core.settings.internal.AppearanceSettingCall

@Reusable
class AppearanceObjectRepository(
    val appearanceSettingCall: AppearanceSettingCall,
    val store: ObjectWithoutUidStore<FilterConfig>
) : ReadOnlyAnyObjectWithDownloadRepositoryImpl<AppearanceSettings>(
    appearanceSettingCall),
    ReadOnlyWithDownloadObjectRepository<AppearanceSettings> {


    override fun blockingGet(): AppearanceSettings {
        TODO("Not yet implemented")
    }

}
