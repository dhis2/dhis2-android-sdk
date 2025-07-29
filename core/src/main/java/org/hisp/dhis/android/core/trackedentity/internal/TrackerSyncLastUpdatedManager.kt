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
package org.hisp.dhis.android.core.trackedentity.internal

import org.hisp.dhis.android.core.arch.db.stores.internal.TrackerBaseSyncStore
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toJavaDate
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toKtxInstant
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.settings.DownloadPeriod
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.hisp.dhis.android.core.settings.ProgramSettings
import java.util.Date

internal open class TrackerSyncLastUpdatedManager<S : TrackerBaseSync>(private val store: TrackerBaseSyncStore<S>) {
    private lateinit var syncMap: Map<Pair<String?, Int>, S>
    private var programSettings: ProgramSettings? = null
    private lateinit var params: ProgramDataDownloadParams

    suspend fun prepare(programSettings: ProgramSettings?, params: ProgramDataDownloadParams) {
        this.programSettings = programSettings
        this.params = params
        this.syncMap = store.selectAll().associateBy { Pair(it.program(), it.organisationUnitIdsHash()) }
    }

    fun getLastUpdatedStr(commonParams: TrackerQueryCommonParams): String? {
        return getLastUpdated(commonParams)?.let { BaseIdentifiableObject.dateToDateStr(it) }
    }

    private fun getLastUpdated(commonParams: TrackerQueryCommonParams): Date? {
        return getLastUpdated(commonParams.program, commonParams.orgUnitsBeforeDivision.toSet(), commonParams.limit)
    }

    private fun getLastUpdated(programId: String?, organisationUnits: Set<String>, limit: Int): Date? {
        val orgUnitHashCode = organisationUnits.toSet().hashCode()
        return if (params.uids().isEmpty()) {
            val programSync = syncMap[Pair(programId, orgUnitHashCode)]
            val globalSync = syncMap[Pair(null, orgUnitHashCode)]

            return getLastUpdatedIfValid(programSync, limit)
                ?: getLastUpdatedIfValid(globalSync, limit)
                ?: getDefaultLastUpdated(programId)
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
            DateUtils.addMonths(Date().toKtxInstant(), -period.months).toJavaDate()
        }
    }

    private fun hasUpdateDownload(programSetting: ProgramSetting?): Boolean {
        return programSetting?.updateDownload() != null
    }

    suspend fun update(sync: S) {
        sync.program()?.let {
            store.deleteByProgram(it, sync.organisationUnitIdsHash())
        } ?: run {
            store.deleteByNullProgram(sync.organisationUnitIdsHash())
        }
        store.insert(sync)
    }
}
