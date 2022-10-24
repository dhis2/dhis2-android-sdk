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
package org.hisp.dhis.android.core.event.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface
import org.hisp.dhis.android.core.resource.internal.ResourceHandler
import org.hisp.dhis.android.core.settings.*
import org.hisp.dhis.android.core.trackedentity.internal.TrackerQueryFactoryCommonHelper
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EventQueryBundleFactoryShould {
    private val resourceHandler: ResourceHandler = mock()
    private val programStore: ProgramStoreInterface = mock()
    private val programSettingsObjectRepository: ProgramSettingsObjectRepository = mock()
    private val programSettings: ProgramSettings = mock()
    private val lastUpdatedManager: EventLastUpdatedManager = mock()
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore = mock()
    private val organisationUnitProgramLinkLinkStore: LinkStore<OrganisationUnitProgramLink> = mock()

    private val p1 = "program1"
    private val p2 = "program2"
    private val p3 = "program3"
    private val ou1 = "ou1"
    private val ou1c1 = "ou1.1"
    private val ou2 = "ou2"
    private val rootOrgUnits = listOf(ou1, ou2)
    private val captureOrgUnits = listOf(ou1, ou1c1, ou2)
    private val programList = listOf(p1, p2, p3)

    private val params = ProgramDataDownloadParams.builder().build()

    // Object to test
    private lateinit var bundleFactory: EventQueryBundleFactory

    @Before
    @Throws(Exception::class)
    fun setUp() {
        whenever(resourceHandler.getLastUpdated(any())).thenReturn(null)
        whenever(programStore.getUidsByProgramType(any())).thenReturn(programList)
        whenever(userOrganisationUnitLinkStore.queryRootCaptureOrganisationUnitUids()).thenReturn(rootOrgUnits)
        whenever(userOrganisationUnitLinkStore.queryOrganisationUnitUidsByScope(any()))
            .thenReturn(captureOrgUnits)

        whenever(programSettingsObjectRepository.blockingGet()).thenReturn(programSettings)

        val commonHelper = TrackerQueryFactoryCommonHelper(
            userOrganisationUnitLinkStore, organisationUnitProgramLinkLinkStore
        )
        bundleFactory = EventQueryBundleFactory(
            programStore,
            programSettingsObjectRepository,
            lastUpdatedManager,
            commonHelper
        )
    }

    @Test
    fun create_a_single_bundle_when_global() {
        val bundles = bundleFactory.getQueries(params)
        assertThat(bundles.size).isEqualTo(1)
        val bundle = bundles[0]
        assertThat(bundle.orgUnits()).isEqualTo(rootOrgUnits)
        assertThat(bundle.commonParams().programs).isEqualTo(programList)
        assertThat(bundle.commonParams().ouMode).isEqualTo(OrganisationUnitMode.DESCENDANTS)
    }

    @Test
    fun create_separate_bundle_for_program_if_has_specific_settings() {
        val settings = ProgramSetting.builder().uid(p1).eventsDownload(200).build()
        whenever(programSettings.specificSettings()).thenReturn(mapOf(p1 to settings))

        val bundles = bundleFactory.getQueries(params)
        assertThat(bundles.size).isEqualTo(2)
        for (bundle in bundles) {
            when (bundle.commonParams().programs.size) {
                1 -> {
                    assertThat(bundle.commonParams().programs[0]).isEqualTo(p1)
                    assertThat(bundle.commonParams().limit).isEqualTo(200)
                }
                2 -> {
                    assertThat(bundle.commonParams().programs.contains(p2)).isTrue()
                    assertThat(bundle.commonParams().programs.contains(p3)).isTrue()
                }
                else -> {
                    fail("Not a valid bundle")
                }
            }
        }
    }

    @Test
    fun get_event_date_if_defined() {
        val settings = ProgramSetting.builder().uid(p1).eventDateDownload(DownloadPeriod.LAST_3_MONTHS).build()
        whenever(programSettings.specificSettings()).thenReturn(mapOf(p1 to settings))

        val bundles = bundleFactory.getQueries(params)
        assertThat(bundles.size).isEqualTo(2)
        for (bundle in bundles) {
            if (bundle.commonParams().programs.size == 1) {
                assertThat(bundle.commonParams().programs[0]).isEqualTo(p1)
                assertThat(bundle.commonParams().startDate).isNotNull()
            }
        }
    }

    @Test
    fun apply_user_defined_limit_only_to_global_if_no_program() {
        val params = ProgramDataDownloadParams.builder().limit(5000).build()

        val settings = ProgramSetting.builder().uid(p1).eventsDownload(100).build()
        whenever(programSettings.specificSettings()).doReturn(mapOf(p1 to settings))

        val bundles = bundleFactory.getQueries(params)
        assertThat(bundles.size).isEqualTo(2)
        for (bundle in bundles) {
            if (bundle.commonParams().programs.size == 1) {
                assertThat(bundle.commonParams().programs[0]).isEqualTo(p1)
                assertThat(bundle.commonParams().limit).isEqualTo(100)
            } else {
                assertThat(bundle.commonParams().limit).isEqualTo(4800)
            }
        }
    }

    @Test
    fun should_create_different_queries_if_per_orgunit_in_specific() {
        val params = ProgramDataDownloadParams.builder().build()

        val settings = ProgramSetting.builder().uid(p1).settingDownload(LimitScope.PER_ORG_UNIT).build()
        whenever(programSettings.specificSettings()).doReturn(mapOf(p1 to settings))
        whenever(organisationUnitProgramLinkLinkStore.selectWhere(any())).doReturn(
            listOf(
                OrganisationUnitProgramLink.builder().program(p1).organisationUnit(ou1).build(),
                OrganisationUnitProgramLink.builder().program(p1).organisationUnit(ou2).build()
            )
        )

        val bundles = bundleFactory.getQueries(params)

        assertThat(bundles.size).isEqualTo(3)
        assertThat(bundles.filter { it.commonParams().program == p1 }.size).isEqualTo(2)
        assertThat(bundles.filter { it.commonParams().program == null }.size).isEqualTo(1)
    }
}
