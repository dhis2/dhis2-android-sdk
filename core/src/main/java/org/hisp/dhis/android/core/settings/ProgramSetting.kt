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

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.fileresource.internal.UploadQuality
import java.util.Date

@ModelBuilder
@Suppress("TooManyFunctions")
data class ProgramSetting(
    val uid: String?,
    val name: String?,
    val filters: List<ObjectWithUid>?,
    val lastUpdated: Date?,
    val teiDownload: Int?,
    val teiDBTrimming: Int?,
    val eventsDownload: Int?,
    val eventsDBTrimming: Int?,
    val updateDownload: DownloadPeriod?,
    val updateDBTrimming: DownloadPeriod?,
    val settingDownload: LimitScope?,
    val settingDBTrimming: LimitScope?,
    val enrollmentDownload: EnrollmentScope?,
    val enrollmentDBTrimming: EnrollmentScope?,
    val eventDateDownload: DownloadPeriod?,
    val eventDateDBTrimming: DownloadPeriod?,
    val enrollmentDateDownload: DownloadPeriod?,
    val enrollmentDateDBTrimming: DownloadPeriod?,
    val imageSettings: Map<String, Map<String, UploadQuality>>?,
) : CoreObject {
    fun uid(): String? = uid
    fun name(): String? = name
    fun filters(): List<ObjectWithUid>? = filters
    fun lastUpdated(): Date? = lastUpdated
    fun teiDownload(): Int? = teiDownload
    fun teiDBTrimming(): Int? = teiDBTrimming
    fun eventsDownload(): Int? = eventsDownload
    fun eventsDBTrimming(): Int? = eventsDBTrimming
    fun updateDownload(): DownloadPeriod? = updateDownload
    fun updateDBTrimming(): DownloadPeriod? = updateDBTrimming
    fun settingDownload(): LimitScope? = settingDownload
    fun settingDBTrimming(): LimitScope? = settingDBTrimming
    fun enrollmentDownload(): EnrollmentScope? = enrollmentDownload
    fun enrollmentDBTrimming(): EnrollmentScope? = enrollmentDBTrimming
    fun eventDateDownload(): DownloadPeriod? = eventDateDownload
    fun eventDateDBTrimming(): DownloadPeriod? = eventDateDBTrimming
    fun enrollmentDateDownload(): DownloadPeriod? = enrollmentDateDownload
    fun enrollmentDateDBTrimming(): DownloadPeriod? = enrollmentDateDBTrimming
    fun imageSettings(): Map<String, Map<String, UploadQuality>>? = imageSettings

    fun toBuilder(): Builder = ProgramSettingBuilder.from(this)

    class Builder : ProgramSettingBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
