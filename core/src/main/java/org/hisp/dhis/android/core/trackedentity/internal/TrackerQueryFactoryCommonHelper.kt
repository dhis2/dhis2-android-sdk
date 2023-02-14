/*
 *  Copyright (c) 2004-2022, University of Oslo
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

import dagger.Reusable
import java.util.*
import javax.inject.Inject
import org.apache.commons.lang3.time.DateUtils
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.settings.DownloadPeriod
import org.hisp.dhis.android.core.settings.LimitScope
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.hisp.dhis.android.core.settings.ProgramSettings
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore

@Suppress("TooManyFunctions")
@Reusable
internal class TrackerQueryFactoryCommonHelper @Inject constructor(
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    private val organisationUnitProgramLinkStore: LinkStore<OrganisationUnitProgramLink>
) {

    private fun getRootCaptureOrgUnitUids(): List<String> {
        return userOrganisationUnitLinkStore.queryRootCaptureOrganisationUnitUids()
    }

    fun getCaptureOrgUnitUids(): List<String> {
        return userOrganisationUnitLinkStore
            .queryOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
    }

    fun getLinkedCaptureOrgUnitUids(programUid: String?): List<String> {
        val ous = getCaptureOrgUnitUids()
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM, programUid)
            .appendInKeyStringValues(OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT, ous)
            .build()
        return organisationUnitProgramLinkStore.selectWhere(whereClause).map { it.organisationUnit()!! }
    }

    private fun getOrganisationUnits(
        params: ProgramDataDownloadParams,
        hasLimitByOrgUnit: Boolean,
        byLimitExtractor: () -> List<String>
    ): Pair<OrganisationUnitMode, List<String>> {
        return when {
            params.orgUnits().size > 0 ->
                Pair(OrganisationUnitMode.SELECTED, params.orgUnits())
            params.uids().size > 0 ->
                Pair(OrganisationUnitMode.ACCESSIBLE, emptyList())
            hasLimitByOrgUnit ->
                Pair(OrganisationUnitMode.SELECTED, byLimitExtractor.invoke())
            else ->
                Pair(OrganisationUnitMode.DESCENDANTS, getRootCaptureOrgUnitUids())
        }
    }

    @Suppress("ReturnCount")
    fun hasLimitByOrgUnit(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programUid: String?
    ): Boolean {
        if (params.limitByOrgunit() != null) {
            return params.limitByOrgunit()!!
        }
        if (programSettings != null) {
            val specificSetting = programSettings.specificSettings()[programUid]
            if (specificSetting != null) {
                val scope = specificSetting.settingDownload()
                if (scope != null) {
                    return scope == LimitScope.PER_ORG_UNIT
                }
            }
            if (programSettings.globalSettings() != null) {
                val scope = programSettings.globalSettings()!!.settingDownload()
                if (scope != null) {
                    return scope == LimitScope.PER_OU_AND_PROGRAM || scope == LimitScope.PER_ORG_UNIT
                }
            }
        }
        return false
    }

    fun getLimit(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programUid: String?,
        downloadExtractor: (ProgramSetting?) -> Int?
    ): Int {
        val configLimit = getConfigLimit(params, programSettings, programUid, downloadExtractor)

        return if (params.uids().isNullOrEmpty()) {
            configLimit
        } else {
            configLimit.coerceAtMost(params.uids().size)
        }
    }

    @Suppress("ReturnCount")
    private fun getConfigLimit(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programUid: String?,
        downloadExtractor: (ProgramSetting?) -> Int?
    ): Int {
        if (params.limit() != null) {
            when {
                isGlobal(params, programUid) -> {
                    val download = params.limit()!! - specificEvents(
                        params, programSettings, downloadExtractor
                    )
                    return if (download > 0) download else 0
                }
                isUserDefinedProgram(params, programUid) -> return params.limit()!!
            }
        }
        if (!isGlobal(params, programUid) && programSettings != null) {
            val specificSetting = programSettings.specificSettings()[programUid]
            val download = downloadExtractor.invoke(specificSetting)
            if (download != null) {
                return download
            }
        }
        if (params.limit() != null && (params.limitByProgram() == true || params.limitByOrgunit() == true)) {
            return params.limit()!!
        }
        if (programSettings != null) {
            val globalSetting = programSettings.globalSettings()
            val download = downloadExtractor.invoke(globalSetting)
            if (download != null) {
                return download
            }
        }
        return ProgramDataDownloadParams.DEFAULT_LIMIT
    }

    private fun specificEvents(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        downloadExtractor: (ProgramSetting?) -> Int?
    ): Int {
        return programSettings?.specificSettings()?.map { settings ->
            val scope = settings.value.settingDownload()
            val hasLimitByOrgUnit = if (scope != null) scope == LimitScope.PER_ORG_UNIT else false
            val orgUnits = getOrganisationUnits(params, hasLimitByOrgUnit) {
                getLinkedCaptureOrgUnitUids(settings.value.uid())
            }.second
            downloadExtractor.invoke(settings.value)?.times(orgUnits.size)
        }?.filterNotNull()?.sum() ?: 0
    }

    fun isGlobal(params: ProgramDataDownloadParams, programUid: String?): Boolean {
        return programUid == null && params.limitByOrgunit() != true && params.limitByProgram() != true
    }

    fun isUserDefinedProgram(params: ProgramDataDownloadParams, programUid: String?): Boolean {
        return programUid == params.program()
    }

    @Suppress("ReturnCount")
    fun hasLimitByProgram(params: ProgramDataDownloadParams, programSettings: ProgramSettings?): Boolean {
        if (params.limitByProgram() != null) {
            return params.limitByProgram()!!
        }
        if (programSettings?.globalSettings() != null) {
            val scope = programSettings.globalSettings()!!.settingDownload()
            if (scope != null) {
                return scope == LimitScope.PER_OU_AND_PROGRAM || scope == LimitScope.PER_PROGRAM
            }
        }
        return false
    }

    fun <O> divideByOrgUnits(
        orgUnits: List<String>,
        hasLimitByOrgUnit: Boolean,
        builder: (List<String>) -> O
    ): List<O> {
        return if (hasLimitByOrgUnit && orgUnits.isNotEmpty()) {
            orgUnits.map { builder.invoke(listOf(it)) }
        } else {
            listOf(builder.invoke(orgUnits))
        }
    }

    private fun getStartDate(
        programSettings: ProgramSettings?,
        programUid: String?,
        downloadPeriodAccessor: (ProgramSetting?) -> DownloadPeriod?
    ): String? {
        var period: DownloadPeriod? = null
        if (programSettings != null) {
            val specificSetting = programSettings.specificSettings()[programUid]
            val globalSetting = programSettings.globalSettings()
            if (downloadPeriodAccessor(specificSetting) != null) {
                period = downloadPeriodAccessor(specificSetting)
            } else if (downloadPeriodAccessor(globalSetting) != null) {
                period = downloadPeriodAccessor(globalSetting)
            }
        }
        return if (period == null || period == DownloadPeriod.ANY) {
            null
        } else {
            val startDate = DateUtils.addMonths(Date(), -period.months)
            org.hisp.dhis.android.core.arch.helpers.DateUtils.DATE_FORMAT.format(startDate)
        }
    }

    @Suppress("LongParameterList")
    fun getCommonParams(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programs: List<String>,
        programUid: String?,
        limit: Int,
        orgUnitByLimitExtractor: () -> List<String>,
        periodExtractor: (ProgramSetting?) -> DownloadPeriod?
    ): TrackerQueryCommonParams {
        val hasLimitByOrgUnit = hasLimitByOrgUnit(params, programSettings, programUid)
        val (ouMode, orgUnits) = getOrganisationUnits(
            params, hasLimitByOrgUnit, orgUnitByLimitExtractor
        )

        return TrackerQueryCommonParams(
            params.uids(),
            programs,
            programUid,
            getStartDate(programSettings, programUid, periodExtractor),
            hasLimitByOrgUnit,
            ouMode,
            orgUnits,
            limit
        )
    }
}
