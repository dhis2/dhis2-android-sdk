/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.network.settings

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.settings.DataSyncPeriod
import org.hisp.dhis.android.core.settings.MetadataSyncPeriod
import org.hisp.dhis.android.core.settings.SynchronizationSettings
import org.hisp.dhis.android.core.tracker.TrackerExporterVersion
import org.hisp.dhis.android.core.tracker.TrackerImporterVersion

@Serializable
internal data class SynchronizationSettingsDTO(
    val dataSync: String?,
    val metadataSync: String?,
    val trackerImporterVersion: String?,
    val trackerExporterVersion: String?,
    val dataSetSettings: DataSetSettingsDTO?,
    val programSettings: ProgramSettingsDTO?,
    val fileMaxLengthBytes: Int?,
) {
    fun toDomain(): SynchronizationSettings {
        return SynchronizationSettings.builder()
            .dataSync(dataSync?.let { DataSyncPeriod.from(it) })
            .metadataSync(metadataSync?.let { MetadataSyncPeriod.from(it) })
            .trackerImporterVersion(
                trackerImporterVersion?.let { TrackerImporterVersion.valueOf(it) }
                    ?: TrackerImporterVersion.V1,
            )
            .trackerExporterVersion(
                trackerExporterVersion?.let { TrackerExporterVersion.valueOf(it) }
                    ?: TrackerExporterVersion.V1,
            )
            .dataSetSettings(dataSetSettings?.toDomain())
            .programSettings(programSettings?.toDomain())
            .fileMaxLengthBytes(fileMaxLengthBytes)
            .build()
    }
}
