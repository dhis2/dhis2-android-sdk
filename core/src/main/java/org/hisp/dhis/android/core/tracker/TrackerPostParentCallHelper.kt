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

package org.hisp.dhis.android.core.tracker

import org.hisp.dhis.android.core.settings.internal.SynchronizationSettingStore
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.internal.DHISVersionManagerImpl
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackerPostParentCallHelper(
    private val dhisVersionManager: DHISVersionManagerImpl,
    private val synchronizationSettingStore: SynchronizationSettingStore,
) {

    suspend fun useNewTrackerImporter(): Boolean {
        val explicitTrackerVersion = synchronizationSettingStore.selectFirst()?.trackerImporterVersion()
        return when {
            !dhisVersionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_38) -> false
            dhisVersionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_42) -> true
            explicitTrackerVersion == TrackerImporterVersion.V2 -> true
            explicitTrackerVersion == null && dhisVersionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_40) -> true
            else -> false
        }
    }

    suspend fun useNewTrackerExporter(): Boolean {
        val explicitTrackerVersion = synchronizationSettingStore.selectFirst()?.trackerExporterVersion()
        return when {
            !dhisVersionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_40) -> false
            dhisVersionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_42) -> true
            explicitTrackerVersion == TrackerExporterVersion.V2 -> true
            explicitTrackerVersion == null -> true
            else -> false
        }
    }
}
