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

import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.mock
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface
import org.hisp.dhis.android.core.settings.DownloadPeriod
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.hisp.dhis.android.core.settings.ProgramSettings
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.HashMap

@RunWith(JUnit4::class)
class TrackedEntityInstanceQueryFactoryShould {

    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore = mock()
    private val organisationUnitProgramLinkLinkStore: LinkStore<OrganisationUnitProgramLink> = mock()
    private val programStore: ProgramStoreInterface = mock()
    private val programSettingsObjectRepository: ProgramSettingsObjectRepository = mock()
    private val programSettings: ProgramSettings = mock()
    private val lastUpdatedManager: TrackedEntityInstanceLastUpdatedManager = mock()

    private val p1 = "program1"
    private val p2 = "program2"
    private val p3 = "program3"
    private val ou1 = "ou1"
    private val ou1c1 = "ou1.1"
    private val ou2 = "ou2"
    private val rootOrgUnits = listOf(ou1, ou2)
    private val captureOrgUnits = listOf(ou1, ou1c1, ou2)
    private val links = listOf(
        OrganisationUnitProgramLink.builder().organisationUnit(ou1c1).program(p1).build(),
        OrganisationUnitProgramLink.builder().organisationUnit(ou1c1).program(p2).build(),
        OrganisationUnitProgramLink.builder().organisationUnit(ou2).program(p2).build()
    )

    // Object to test
    private var queryFactory: TrackedEntityInstanceQueryFactory? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(userOrganisationUnitLinkStore.queryRootCaptureOrganisationUnitUids()).thenReturn(rootOrgUnits)
        Mockito.`when`(userOrganisationUnitLinkStore.queryOrganisationUnitUidsByScope(ArgumentMatchers.any()))
            .thenReturn(captureOrgUnits)
        Mockito.`when`(organisationUnitProgramLinkLinkStore.selectWhere(ArgumentMatchers.anyString()))
            .thenReturn(links)
        Mockito.`when`(programStore.getUidsByProgramType(ArgumentMatchers.any())).thenReturn(
            listOf(p1, p2, p3)
        )
        Mockito.`when`(programSettingsObjectRepository.blockingGet()).thenReturn(programSettings)

        val commonHelper = TrackerQueryFactoryCommonHelper(
            userOrganisationUnitLinkStore, organisationUnitProgramLinkLinkStore
        )
        val internalFactory = TrackedEntityInstanceQueryInternalFactory(commonHelper)
        queryFactory = TrackedEntityInstanceQueryFactory(
            programStore, programSettingsObjectRepository, lastUpdatedManager, commonHelper, internalFactory
        )
    }

    @Test
    fun create_a_single_bundle_when_global() {
        val params = ProgramDataDownloadParams.builder().build()
        val queries = queryFactory!!.getQueries(params)
        Truth.assertThat(queries.size).isEqualTo(1)
        val query = queries[0]
        Truth.assertThat(query.orgUnits()).isEqualTo(rootOrgUnits)
        Truth.assertThat(query.commonParams().ouMode).isEqualTo(OrganisationUnitMode.DESCENDANTS)
        Truth.assertThat(query.commonParams().program).isNull()
    }

    @Test
    fun get_enrollment_date_value_if_defined() {
        val params = ProgramDataDownloadParams.builder().build()
        val specificSettings: MutableMap<String, ProgramSetting> = HashMap()
        specificSettings[p1] = ProgramSetting.builder()
            .uid(p1).enrollmentDateDownload(DownloadPeriod.LAST_3_MONTHS).build()
        Mockito.`when`(programSettings.specificSettings()).thenReturn(specificSettings)
        val queries = queryFactory!!.getQueries(params)
        Truth.assertThat(queries.size).isEqualTo(2)
        for (query in queries) {
            if (query.commonParams().program != null) {
                Truth.assertThat(query.commonParams().program).isEqualTo(p1)
                Truth.assertThat(query.commonParams().startDate).isNotNull()
            }
        }
    }

    @Test
    fun single_query_if_program_provided_by_user() {
        val params = ProgramDataDownloadParams.builder().limit(5000).program(p1).build()
        val queries = queryFactory!!.getQueries(params)
        Truth.assertThat(queries.size).isEqualTo(1)
        for (query in queries) {
            Truth.assertThat(query.commonParams().program).isEqualTo(p1)
            Truth.assertThat(query.commonParams().limit).isEqualTo(5000)
        }
    }

    @Test
    fun apply_user_defined_limit_only_to_global_if_no_program() {
        val params = ProgramDataDownloadParams.builder().limit(5000).build()
        val specificSettings: MutableMap<String, ProgramSetting> = HashMap()
        specificSettings[p1] = ProgramSetting.builder().uid(p1).teiDownload(100).build()
        Mockito.`when`(programSettings.specificSettings()).thenReturn(specificSettings)
        val queries = queryFactory!!.getQueries(params)
        Truth.assertThat(queries.size).isEqualTo(2)
        for (query in queries) {
            if (query.commonParams().program != null) {
                Truth.assertThat(query.commonParams().program).isEqualTo(p1)
                Truth.assertThat(query.commonParams().limit).isEqualTo(100)
            } else {
                Truth.assertThat(query.commonParams().limit).isEqualTo(5000)
            }
        }
    }
}
