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
package org.hisp.dhis.android.core.event.internal

import dagger.Reusable
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface
import org.hisp.dhis.android.core.settings.LimitScope
import org.hisp.dhis.android.core.settings.ProgramSettings
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository
import org.hisp.dhis.android.core.trackedentity.internal.TrackerQueryFactoryCommonHelper
import java.util.ArrayList
import javax.inject.Inject

@Reusable
internal class EventQueryBundleFactory @Inject constructor(
    private val commonHelper: TrackerQueryFactoryCommonHelper,
    private val programStore: ProgramStoreInterface,
    private val programSettingsObjectRepository: ProgramSettingsObjectRepository,
    private val lastUpdatedManager: EventLastUpdatedManager
) {
    fun getEventQueryBundles(params: ProgramDataDownloadParams): List<EventQueryBundle> {
        val programSettings = programSettingsObjectRepository.blockingGet()
        lastUpdatedManager.prepare(programSettings, params)
        val builders: MutableList<EventQueryBundle> = ArrayList()
        if (params.program() == null) {
            val eventPrograms = programStore.getUidsByProgramType(ProgramType.WITHOUT_REGISTRATION)
            if (commonHelper.hasLimitByProgram(params, programSettings)) {
                for (programUid in eventPrograms) {
                    builders.addAll(queryPerProgram(params, programSettings, programUid))
                }
            } else {
                val specificSettings = if (programSettings == null) emptyMap() else programSettings.specificSettings()
                for ((programUid) in specificSettings) {
                    if (eventPrograms.contains(programUid)) {
                        builders.addAll(queryPerProgram(params, programSettings, programUid))
                        eventPrograms.remove(programUid)
                    }
                }
                builders.addAll(queryGlobal(params, programSettings, eventPrograms))
            }
        } else {
            builders.addAll(queryPerProgram(params, programSettings, params.program()))
        }
        return builders
    }

    private fun queryPerProgram(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programUid: String?
    ): List<EventQueryBundle> {
        return queryInternal(params, programSettings, listOf(programUid!!), programUid) {
            commonHelper.getLinkedCaptureOrgUnitUids(programUid) }
    }

    private fun queryGlobal(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programList: List<String>
    ): List<EventQueryBundle> {
        return queryInternal(params, programSettings, programList, null) {
            commonHelper.getCaptureOrgUnitUids() }
    }

    private fun queryInternal(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programs: List<String>,
        programUid: String?,
        orgUnitByLimitExtractor: () -> List<String>
    ): List<EventQueryBundle> {
        val limit = commonHelper.getLimit(params, programSettings, programUid) { it?.eventsDownload() }
        if (limit == 0) {
            return emptyList()
        }
        val eventStartDate = commonHelper.getStartDate(programSettings, programUid) { it?.eventDateDownload() }
        val hasLimitByOrgUnit = commonHelper.hasLimitByOrgUnit(params, programSettings, programUid, LimitScope.ALL_ORG_UNITS)
        val (ouMode, orgUnits) = commonHelper.getOrganisationUnits(
            params, hasLimitByOrgUnit, orgUnitByLimitExtractor)

        val lastUpdated = lastUpdatedManager.getLastUpdated(programUid, orgUnits.toSet(), limit)

        val builder = EventQueryBundle.builder()
            .lastUpdatedStartDate(lastUpdated)
            .ouMode(ouMode)
            .program(programUid)
            .programList(programs)
            .limit(limit)
            .eventStartDate(eventStartDate)

        return commonHelper.divideByOrgUnits(orgUnits, hasLimitByOrgUnit) { builder.orgUnitList(it).build() }
    }
}