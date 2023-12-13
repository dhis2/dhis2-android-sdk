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
package org.hisp.dhis.android.core.settings

import io.reactivex.Single
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadOnlyAnyObjectWithDownloadRepositoryImpl
import org.hisp.dhis.android.core.settings.internal.GeneralSettingCall
import org.hisp.dhis.android.core.settings.internal.GeneralSettingStore
import org.hisp.dhis.android.core.settings.internal.SynchronizationSettingStore
import org.koin.core.annotation.Singleton

@Singleton(binds = [GeneralSettingObjectRepository::class])
class GeneralSettingObjectRepository internal constructor(
    private val store: GeneralSettingStore,
    private val syncStore: SynchronizationSettingStore,
    generalSettingCall: GeneralSettingCall,
) : ReadOnlyAnyObjectWithDownloadRepositoryImpl<GeneralSettings>(generalSettingCall),
    ReadOnlyWithDownloadObjectRepository<GeneralSettings> {
    override fun blockingGet(): GeneralSettings? {
        val generalSettings = store.selectAll()
        val syncSettings = syncStore.selectAll()
        return if (generalSettings.isEmpty() && syncSettings.isEmpty()) {
            null
        } else {
            val generalSetting =
                if (generalSettings.isEmpty()) {
                    GeneralSettings.builder().build()
                } else {
                    generalSettings[0]
                }
            val syncSetting =
                if (syncSettings.isEmpty()) {
                    SynchronizationSettings.builder().build()
                } else {
                    syncSettings[0]
                }

            generalSetting.toBuilder()
                .dataSync(syncSetting.dataSync())
                .metadataSync(syncSetting.metadataSync())
                .build()
        }
    }

    fun blockingHasExperimentalFeature(featureName: String): Boolean {
        return blockingGet()?.experimentalFeatures()?.contains(featureName) ?: false
    }

    fun hasExperimentalFeature(featureName: String): Single<Boolean> {
        return Single.fromCallable { blockingHasExperimentalFeature(featureName) }
    }

    fun blockingHasExperimentalFeature(feature: ExperimentalFeature): Boolean {
        return blockingGet()?.experimentalFeatures()?.contains(feature.jsonName) ?: false
    }

    fun hasExperimentalFeature(feature: ExperimentalFeature): Single<Boolean> {
        return Single.fromCallable { blockingHasExperimentalFeature(feature) }
    }
}
