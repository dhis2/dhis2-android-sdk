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
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.settings.LimitScope
import org.hisp.dhis.android.core.settings.ProgramSettings
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore

@Reusable
internal class TrackedEntityInstanceQueryCommonHelper @Inject constructor(
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    private val organisationUnitProgramLinkStore: LinkStore<OrganisationUnitProgramLink>
) {

    fun getRootCaptureOrgUnitUids(): List<String> {
        return userOrganisationUnitLinkStore.queryRootCaptureOrganisationUnitUids()
    }

    fun getCaptureOrgUnitUids(): List<String> {
        return userOrganisationUnitLinkStore
            .queryOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
    }

    fun getBuilderFor(
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

    fun getLinkedCaptureOrgUnitUids(programUid: String?): List<String> {
        val ous = getCaptureOrgUnitUids()
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

    @Suppress("ReturnCount")
    fun getLimit(
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

    fun isGlobalOrUserDefinedProgram(params: ProgramDataDownloadParams, programUid: String?): Boolean {
        return programUid == null || programUid == params.program()
    }
}