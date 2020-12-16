/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.trackedentity.internal

import org.apache.commons.lang3.time.DateUtils
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.settings.DownloadPeriod
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.hisp.dhis.android.core.settings.ProgramSettings
import java.util.Date

internal open class TrackerSyncLastUpdatedManager<S : TrackerBaseSync>(private val store: ObjectWithoutUidStore<S>) {
    private var byProgram: Map<String?, S>? = null
    private var programSettings: ProgramSettings? = null
    private var params: ProgramDataDownloadParams? = null

    fun prepare(programSettings: ProgramSettings?, params: ProgramDataDownloadParams?) {
        this.programSettings = programSettings
        this.params = params
        byProgram = store.selectAll()
            .map { it.program() to it }
            .toMap()
    }

    fun getLastUpdated(programId: String?, limit: Int): Date? {
        return if (params!!.uids().isEmpty()) {
            if (programId != null) {
                val programSync = byProgram!![programId]
                val programLastUpdated = getLastUpdatedIfValid(programSync, limit)
                if (programLastUpdated != null) {
                    return programLastUpdated
                }
            }
            val programSync = byProgram!![null]
            val generalLastUpdated = getLastUpdatedIfValid(programSync, limit)
            generalLastUpdated ?: getDefaultLastUpdated(programId)
        } else {
            null
        }
    }

    private fun getLastUpdatedIfValid(sync: S?, limit: Int): Date? {
        return if (sync == null || sync.downloadLimit() < limit) {
            null
        } else {
            sync.lastUpdated()
        }
    }

    private fun getDefaultLastUpdated(programUid: String?): Date? {
        var period: DownloadPeriod? = null
        if (programSettings != null) {
            val specificSetting = programSettings!!.specificSettings()[programUid]
            val globalSetting = programSettings!!.globalSettings()
            if (hasUpdateDownload(specificSetting)) {
                period = specificSetting!!.updateDownload()
            } else if (hasUpdateDownload(globalSetting)) {
                period = globalSetting!!.updateDownload()
            }
        }
        return if (period == null || period == DownloadPeriod.ANY) {
            null
        } else {
            DateUtils.addMonths(Date(), -period.months)
        }
    }

    private fun hasUpdateDownload(programSetting: ProgramSetting?): Boolean {
        return programSetting?.updateDownload() != null
    }

    fun update(sync: S) {
        val whereClause = if (sync.program() == null) " IS NULL" else "='" + sync.program() + "'"
        store.deleteWhere(TrackedEntityInstanceSyncTableInfo.Columns.PROGRAM + whereClause)
        store.insert(sync)
    }
}