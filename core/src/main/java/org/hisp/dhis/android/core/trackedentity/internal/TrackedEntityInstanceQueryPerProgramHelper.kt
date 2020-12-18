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

import dagger.Reusable
import java.util.ArrayList
import java.util.Date
import javax.inject.Inject
import org.apache.commons.lang3.time.DateUtils
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.settings.DownloadPeriod
import org.hisp.dhis.android.core.settings.EnrollmentScope
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.hisp.dhis.android.core.settings.ProgramSettings

@Reusable
internal class TrackedEntityInstanceQueryPerProgramHelper @Inject constructor(
    private val lastUpdatedManager: TrackedEntityInstanceLastUpdatedManager,
    private val commonHelper: TrackedEntityInstanceQueryCommonHelper
) {

    fun queryPerProgram(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programUid: String?
    ): List<TeiQuery.Builder> {
        val limit = commonHelper.getLimit(params, programSettings, programUid)
        if (limit == 0) {
            return emptyList()
        }
        val programStatus = getProgramStatus(params, programSettings, programUid)
        val programStartDate = getProgramStartDate(programSettings, programUid)
        val lastUpdated = lastUpdatedManager.getLastUpdated(programUid, limit)
        val ouMode: OrganisationUnitMode
        val orgUnits: List<String>
        val hasLimitByOrgUnit = commonHelper.hasLimitByOrgUnit(params, programSettings, programUid)
        when {
            params.orgUnits().size > 0 -> {
                ouMode = OrganisationUnitMode.SELECTED
                orgUnits = params.orgUnits()
            }
            hasLimitByOrgUnit -> {
                ouMode = OrganisationUnitMode.SELECTED
                orgUnits = commonHelper.getLinkedCaptureOrgUnitUids(programUid)
            }
            else -> {
                ouMode = OrganisationUnitMode.DESCENDANTS
                orgUnits = commonHelper.getRootCaptureOrgUnitUids()
            }
        }
        val builders: MutableList<TeiQuery.Builder> = ArrayList()
        if (hasLimitByOrgUnit) {
            for (orgUnitUid in orgUnits) {
                builders.add(
                    commonHelper.getBuilderFor(lastUpdated, listOf(orgUnitUid), ouMode, params, limit)
                        .program(programUid).programStatus(programStatus).programStartDate(programStartDate)
                )
            }
        } else {
            builders.add(
                commonHelper.getBuilderFor(lastUpdated, orgUnits, ouMode, params, limit)
                    .program(programUid).programStatus(programStatus).programStartDate(programStartDate)
            )
        }
        return builders
    }

    @Suppress("ReturnCount")
    private fun getProgramStatus(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programUid: String?
    ): EnrollmentStatus? {
        if (params.programStatus() != null && commonHelper.isGlobalOrUserDefinedProgram(params, programUid)) {
            return enrollmentScopeToProgramStatus(params.programStatus())
        }
        if (programUid != null && programSettings != null) {
            val specificSetting = programSettings.specificSettings()[programUid]
            if (specificSetting?.enrollmentDownload() != null) {
                return enrollmentScopeToProgramStatus(specificSetting.enrollmentDownload())
            }
        }
        if (params.programStatus() != null && params.limitByProgram() != null && params.limitByProgram()!!) {
            return enrollmentScopeToProgramStatus(params.programStatus())
        }
        if (programSettings != null) {
            val globalSetting = programSettings.globalSettings()
            if (globalSetting?.enrollmentDownload() != null) {
                return enrollmentScopeToProgramStatus(globalSetting.enrollmentDownload())
            }
        }
        return null
    }

    private fun getProgramStartDate(programSettings: ProgramSettings?, programUid: String?): String? {
        var period: DownloadPeriod? = null
        if (programSettings != null) {
            val specificSetting = programSettings.specificSettings()[programUid]
            val globalSetting = programSettings.globalSettings()
            if (hasEnrollmentDateDownload(specificSetting)) {
                period = specificSetting!!.enrollmentDateDownload()
            } else if (hasEnrollmentDateDownload(globalSetting)) {
                period = globalSetting!!.enrollmentDateDownload()
            }
        }
        return if (period == null || period == DownloadPeriod.ANY) {
            null
        } else {
            val programStartDate = DateUtils.addMonths(Date(), -period.months)
            BaseIdentifiableObject.dateToSpaceDateStr(programStartDate)
        }
    }

    private fun enrollmentScopeToProgramStatus(enrollmentScope: EnrollmentScope?): EnrollmentStatus? {
        return if (enrollmentScope != null && enrollmentScope == EnrollmentScope.ONLY_ACTIVE) {
            EnrollmentStatus.ACTIVE
        } else {
            null
        }
    }

    private fun hasEnrollmentDateDownload(programSetting: ProgramSetting?): Boolean {
        return programSetting?.enrollmentDateDownload() != null
    }
}
