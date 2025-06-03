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

import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadOnlyAnyObjectWithDownloadRepositoryImpl
import org.hisp.dhis.android.core.settings.internal.SynchronizationSettingCall
import org.hisp.dhis.android.core.settings.internal.SynchronizationSettingStore
import org.koin.core.annotation.Singleton

@Singleton(binds = [SynchronizationSettingObjectRepository::class])
class SynchronizationSettingObjectRepository internal constructor(
    private val syncStore: SynchronizationSettingStore,
    private val dataSetSettingsRepository: DataSetSettingsObjectRepository,
    private val programSettingsRepository: ProgramSettingsObjectRepository,
    synchronizationSettingCall: SynchronizationSettingCall,
) : ReadOnlyAnyObjectWithDownloadRepositoryImpl<SynchronizationSettings>(synchronizationSettingCall),
    ReadOnlyWithDownloadObjectRepository<SynchronizationSettings> {
    override suspend fun getInternal(): SynchronizationSettings? {
        val syncSettings = syncStore.selectAll()
        val dataSetSettings = dataSetSettingsRepository.blockingGet()
        val programSettings = programSettingsRepository.blockingGet()

        return if (syncSettings.isEmpty() && dataSetSettings == null && programSettings == null) {
            null
        } else {
            val builder = if (syncSettings.isEmpty()) SynchronizationSettings.builder() else syncSettings[0].toBuilder()
            builder
                .dataSetSettings(dataSetSettings)
                .programSettings(programSettings)
                .build()
        }
    }
}
