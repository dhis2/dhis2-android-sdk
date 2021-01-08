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
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.settings.ProgramSettings
import org.hisp.dhis.android.core.trackedentity.internal.TrackerQueryCommonParams
import org.hisp.dhis.android.core.trackedentity.internal.TrackerQueryFactoryCommonHelper
import org.hisp.dhis.android.core.trackedentity.internal.TrackerQueryInternalFactory
import javax.inject.Inject

@Reusable
internal class EventQueryBundleInternalFactory @Inject constructor(
    private val commonHelper: TrackerQueryFactoryCommonHelper,
    private val lastUpdatedManager: EventLastUpdatedManager
) : TrackerQueryInternalFactory<EventQueryBundle> {

    override fun queryPerProgram(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programUid: String?
    ): List<EventQueryBundle> {
        return queryInternal(params, programSettings, listOf(programUid!!), programUid) {
            commonHelper.getLinkedCaptureOrgUnitUids(programUid) }
    }

    override fun queryGlobal(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programs: List<String>
    ): List<EventQueryBundle> {
        return queryInternal(params, programSettings, programs, null) {
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
        val commonParams: TrackerQueryCommonParams = commonHelper.getCommonParams(params, programSettings, programs, programUid, limit, orgUnitByLimitExtractor) { it?.eventDateDownload() }
        val lastUpdated = lastUpdatedManager.getLastUpdated(programUid, commonParams.orgUnitsBeforeDivision.toSet(), limit)

        val builder = EventQueryBundle.builder()
            .commonParams(commonParams)
            .lastUpdatedStartDate(lastUpdated)

        return commonHelper.divideByOrgUnits(commonParams.orgUnitsBeforeDivision, commonParams.hasLimitByOrgUnit) { builder.orgUnitList(it).build() }
    }
}