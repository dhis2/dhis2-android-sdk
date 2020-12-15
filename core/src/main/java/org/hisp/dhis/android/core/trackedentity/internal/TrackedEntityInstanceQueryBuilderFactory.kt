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
import org.apache.commons.lang3.time.DateUtils
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface
import org.hisp.dhis.android.core.settings.*
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import java.util.ArrayList
import java.util.Date
import javax.inject.Inject

@Reusable
internal class TrackedEntityInstanceQueryBuilderFactory @Inject constructor(
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    private val organisationUnitProgramLinkStore: LinkStore<OrganisationUnitProgramLink>,
    private val programStore: ProgramStoreInterface,
    private val programSettingsObjectRepository: ProgramSettingsObjectRepository,
    private val lastUpdatedManager: TrackedEntityInstanceLastUpdatedManager
) {

    private val rootCaptureOrgUnitUids: List<String>
        get() = userOrganisationUnitLinkStore.queryRootCaptureOrganisationUnitUids()
    private val captureOrgUnitUids: List<String>
        get() = userOrganisationUnitLinkStore
            .queryOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)

    fun getTeiQueryBuilders(params: ProgramDataDownloadParams): List<TeiQuery.Builder> {
        val programSettings = programSettingsObjectRepository.blockingGet()
        lastUpdatedManager.prepare(programSettings, params)
        val builders: MutableList<TeiQuery.Builder> = ArrayList()
        if (params.program() == null) {
            val trackerPrograms = programStore.getUidsByProgramType(ProgramType.WITH_REGISTRATION)
            if (hasLimitByProgram(params, programSettings)) {
                for (programUid in trackerPrograms) {
                    builders.addAll(queryPerProgram(params, programSettings, programUid))
                }
            } else {
                val specificSettings = if (programSettings == null) emptyMap() else programSettings.specificSettings()
                for ((programUid) in specificSettings) {
                    if (trackerPrograms.contains(programUid)) {
                        builders.addAll(queryPerProgram(params, programSettings, programUid))
                    }
                }
                builders.addAll(queryGlobal(params, programSettings))
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
    ): List<TeiQuery.Builder> {
        val limit = getLimit(params, programSettings, programUid)
        if (limit == 0) {
            return emptyList()
        }
        val programStatus = getProgramStatus(params, programSettings, programUid)
        val programStartDate = getProgramStartDate(programSettings, programUid)
        val lastUpdated = lastUpdatedManager.getLastUpdated(programUid, limit)
        val ouMode: OrganisationUnitMode
        val orgUnits: List<String>
        val hasLimitByOrgUnit = hasLimitByOrgUnit(params, programSettings, programUid)
        when {
            params.orgUnits().size > 0 -> {
                ouMode = OrganisationUnitMode.SELECTED
                orgUnits = params.orgUnits()
            }
            hasLimitByOrgUnit -> {
                ouMode = OrganisationUnitMode.SELECTED
                orgUnits = getLinkedCaptureOrgUnitUids(programUid)
            }
            else -> {
                ouMode = OrganisationUnitMode.DESCENDANTS
                orgUnits = rootCaptureOrgUnitUids
            }
        }
        val builders: MutableList<TeiQuery.Builder> = ArrayList()
        if (hasLimitByOrgUnit) {
            for (orgUnitUid in orgUnits) {
                builders.add(
                    getBuilderFor(lastUpdated, listOf(orgUnitUid), ouMode, params, limit)
                        .program(programUid).programStatus(programStatus).programStartDate(programStartDate)
                )
            }
        } else {
            builders.add(
                getBuilderFor(lastUpdated, orgUnits, ouMode, params, limit)
                    .program(programUid).programStatus(programStatus).programStartDate(programStartDate)
            )
        }
        return builders
    }

    private fun queryGlobal(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?
    ): List<TeiQuery.Builder> {
        val limit = getLimit(params, programSettings, null)
        if (limit == 0) {
            return emptyList()
        }
        val lastUpdated = lastUpdatedManager.getLastUpdated(null, limit)
        val ouMode: OrganisationUnitMode
        val orgUnits: List<String>
        val hasLimitByOrgUnit = hasLimitByOrgUnit(params, programSettings, null)
        when {
            params.orgUnits().size > 0 -> {
                ouMode = OrganisationUnitMode.SELECTED
                orgUnits = params.orgUnits()
            }
            hasLimitByOrgUnit -> {
                ouMode = OrganisationUnitMode.SELECTED
                orgUnits = captureOrgUnitUids
            }
            else -> {
                ouMode = OrganisationUnitMode.DESCENDANTS
                orgUnits = rootCaptureOrgUnitUids
            }
        }
        val builders: MutableList<TeiQuery.Builder> = ArrayList()
        if (hasLimitByOrgUnit) {
            for (orgUnitUid in orgUnits) {
                builders.add(getBuilderFor(lastUpdated, listOf(orgUnitUid), ouMode, params, limit))
            }
        } else {
            builders.add(getBuilderFor(lastUpdated, orgUnits, ouMode, params, limit))
        }
        return builders
    }

    private fun getBuilderFor(
        lastUpdated: Date?,
        organisationUnits: List<String>,
        organisationUnitMode: OrganisationUnitMode,
        params: ProgramDataDownloadParams,
        limit: Int
    ): TeiQuery.Builder {
        return TeiQuery.builder()
            .lastUpdatedStartDate(lastUpdated)
            .orgUnits(organisationUnits)
            .ouMode(organisationUnitMode)
            .uids(params.uids())
            .limit(limit)
    }

    private fun getLinkedCaptureOrgUnitUids(programUid: String?): List<String> {
        val ous = captureOrgUnitUids
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM, programUid)
            .appendInKeyStringValues(OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT, ous)
            .build()
        val linkedOrgunits: MutableList<String> = ArrayList()
        for (link in organisationUnitProgramLinkStore.selectWhere(whereClause)) {
            linkedOrgunits.add(link.organisationUnit()!!)
        }
        return linkedOrgunits
    }

    private fun hasLimitByProgram(params: ProgramDataDownloadParams, programSettings: ProgramSettings?): Boolean {
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

    private fun hasLimitByOrgUnit(
        params: ProgramDataDownloadParams, programSettings: ProgramSettings?,
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

    private fun getLimit(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programUid: String?
    ): Int {
        val uidsCount = params.uids().size
        if (uidsCount > 0) {
            return uidsCount
        }
        if (params.limit() != null && isGlobalOrUserDefinedProgram(params, programUid)) {
            return params.limit()!!
        }
        if (programUid != null && programSettings != null) {
            val specificSetting = programSettings.specificSettings()[programUid]
            if (specificSetting?.teiDownload() != null) {
                return specificSetting.teiDownload()!!
            }
        }
        if (params.limit() != null && params.limitByProgram() != null && params.limitByProgram()!!) {
            return params.limit()!!
        }
        if (programSettings != null) {
            val globalSetting = programSettings.globalSettings()
            if (globalSetting?.teiDownload() != null) {
                return globalSetting.teiDownload()!!
            }
        }
        return ProgramDataDownloadParams.DEFAULT_LIMIT
    }

    private fun getProgramStatus(
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?,
        programUid: String?
    ): EnrollmentStatus? {
        if (params.programStatus() != null && isGlobalOrUserDefinedProgram(params, programUid)) {
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

    private fun isGlobalOrUserDefinedProgram(params: ProgramDataDownloadParams, programUid: String?): Boolean {
        return programUid == null || programUid == params.program()
    }
}