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
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.settings.DownloadPeriod
import org.hisp.dhis.android.core.settings.EnrollmentScope
import org.hisp.dhis.android.core.settings.LimitScope
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.hisp.dhis.android.network.common.dto.ObjectWithUidDTO

@Serializable
internal data class ProgramSettingDTO(
    val id: String?,
    val name: String?,
    val filters: List<ObjectWithUidDTO>?,
    val lastUpdated: String?,
    val teiDownload: Int?,
    val teiDBTrimming: Int?,
    val eventsDownload: Int?,
    val eventsDBTrimming: Int?,
    val updateDownload: String?,
    val updateDBTrimming: String?,
    val settingDownload: String?,
    val settingDBTrimming: String?,
    val enrollmentDownload: String?,
    val enrollmentDBTrimming: String?,
    val eventDateDownload: String?,
    val eventDateDBTrimming: String?,
    val enrollmentDateDownload: String?,
    val enrollmentDateDBTrimming: String?,
) {
    fun toDomain(): ProgramSetting {
        return ProgramSetting.builder()
            .uid(id)
            .name(name)
            .filters(filters?.map { it.toDomain() })
            .lastUpdated(lastUpdated?.let { DateUtils.DATE_FORMAT.parse(it) })
            .teiDownload(teiDownload)
            .teiDBTrimming(teiDBTrimming)
            .eventsDownload(eventsDownload)
            .eventsDBTrimming(eventsDBTrimming)
            .updateDownload(updateDownload?.let { DownloadPeriod.valueOf(it) })
            .updateDBTrimming(updateDBTrimming?.let { DownloadPeriod.valueOf(it) })
            .settingDownload(settingDownload?.let { LimitScope.valueOf(it) })
            .settingDBTrimming(settingDBTrimming?.let { LimitScope.valueOf(it) })
            .enrollmentDownload(enrollmentDownload?.let { EnrollmentScope.valueOf(it) })
            .enrollmentDBTrimming(enrollmentDBTrimming?.let { EnrollmentScope.valueOf(it) })
            .eventDateDownload(eventDateDownload?.let { DownloadPeriod.valueOf(it) })
            .eventDateDBTrimming(eventDateDBTrimming?.let { DownloadPeriod.valueOf(it) })
            .enrollmentDateDownload(enrollmentDateDownload?.let { DownloadPeriod.valueOf(it) })
            .enrollmentDateDBTrimming(enrollmentDateDBTrimming?.let { DownloadPeriod.valueOf(it) })
            .build()
    }
}
