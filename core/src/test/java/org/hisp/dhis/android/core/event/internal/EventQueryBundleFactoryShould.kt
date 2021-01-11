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

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface
import org.hisp.dhis.android.core.resource.internal.ResourceHandler
import org.hisp.dhis.android.core.settings.DownloadPeriod
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.hisp.dhis.android.core.settings.ProgramSettings
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository
import org.hisp.dhis.android.core.trackedentity.internal.TrackerQueryFactoryCommonHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.HashMap

@RunWith(JUnit4::class)
class EventQueryBundleFactoryShould {
    @Mock
    private val resourceHandler: ResourceHandler? = null

    @Mock
    private val commonHelper: TrackerQueryFactoryCommonHelper? = null

    @Mock
    private val programStore: ProgramStoreInterface? = null

    @Mock
    private val programSettingsObjectRepository: ProgramSettingsObjectRepository? = null

    @Mock
    private val programSettings: ProgramSettings? = null

    @Mock
    private val lastUpdatedManager: EventLastUpdatedManager? = null
    private val p1 = "program1"
    private val p2 = "program2"
    private val p3 = "program3"
    private val ou1 = "ou1"
    private val ou1c1 = "ou1.1"
    private val ou2 = "ou2"
    private val rootOrgUnits = listOf(ou1, ou2)
    private val captureOrgUnits = listOf(ou1, ou1c1, ou2)
    private val programList = listOf(p1, p2, p3)

    // Object to test
    private var bundleFactory: EventQueryBundleFactory? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(resourceHandler!!.getLastUpdated(ArgumentMatchers.any())).thenReturn(null)
        Mockito.`when`(commonHelper!!.getRootCaptureOrgUnitUids()).thenReturn(rootOrgUnits)
        Mockito.`when`(commonHelper.getCaptureOrgUnitUids()).thenReturn(captureOrgUnits)
        Mockito.`when`(
            commonHelper.getLimit(
                ArgumentMatchers.any(),
                ArgumentMatchers.any<ProgramSettings>(),
                ArgumentMatchers.any<String>(),
                ArgumentMatchers.any<Function1<ProgramSetting?, Int>>()
            )
        ).thenReturn(100).thenReturn(5000)
        Mockito.`when`(programStore!!.getUidsByProgramType(ArgumentMatchers.any())).thenReturn(
            programList
        )
        Mockito.`when`(programSettingsObjectRepository!!.blockingGet()).thenReturn(programSettings)
        bundleFactory = EventQueryBundleFactory(
            programStore,
            programSettingsObjectRepository,
            lastUpdatedManager!!,
            commonHelper
        )
    }

    @Test
    fun create_a_single_bundle_when_global() {
        val params = ProgramDataDownloadParams.builder().build()
        val bundles = bundleFactory!!.getQueries(params)
        Truth.assertThat(bundles.size).isEqualTo(1)
        val bundle = bundles[0]
        Truth.assertThat(bundle.orgUnits()).isEqualTo(rootOrgUnits)
        Truth.assertThat(bundle.commonParams().programs).isEqualTo(programList)
        Truth.assertThat(bundle.commonParams().ouMode).isEqualTo(OrganisationUnitMode.DESCENDANTS)
    }

    // TODO refactor tests
    @Test
    fun create_separate_bundle_for_program_if_has_specific_settings() {
        val params = ProgramDataDownloadParams.builder().build()
        val specifics: MutableMap<String, ProgramSetting> = HashMap()
        specifics[p1] = ProgramSetting.builder().uid(p1).eventsDownload(200).build()
        Mockito.`when`(programSettings!!.specificSettings()).thenReturn(specifics)
        val bundles = bundleFactory!!.getQueries(params)
        Truth.assertThat(bundles.size).isEqualTo(2)
        for (bundle in bundles) {
            if (bundle.commonParams().programs.size == 1) {
                Truth.assertThat(bundle.commonParams().programs[0]).isEqualTo(p1)
                Truth.assertThat(bundle.commonParams().limit).isEqualTo(200)
            } else if (bundle.commonParams().programs.size == 2) {
                Truth.assertThat(bundle.commonParams().programs.contains(p2)).isTrue()
                Truth.assertThat(bundle.commonParams().programs.contains(p3)).isTrue()
            } else {
                throw RuntimeException("Not a valid bundle")
            }
        }
    }

    @Test
    fun get_event_date_if_defined() {
        val params = ProgramDataDownloadParams.builder().build()
        val specifics: MutableMap<String, ProgramSetting> = HashMap()
        specifics[p1] = ProgramSetting.builder().uid(p1).eventDateDownload(DownloadPeriod.LAST_3_MONTHS).build()
        Mockito.`when`(programSettings!!.specificSettings()).thenReturn(specifics)
        val bundles = bundleFactory!!.getQueries(params)
        Truth.assertThat(bundles.size).isEqualTo(2)
        for (bundle in bundles) {
            if (bundle.commonParams().programs.size == 1) {
                Truth.assertThat(bundle.commonParams().programs[0]).isEqualTo(p1)
                Truth.assertThat(bundle.commonParams().startDate).isNotNull()
            }
        }
    }

    @Test
    fun apply_user_defined_limit_only_to_global_if_no_program() {
        val params = ProgramDataDownloadParams.builder().limit(5000).build()
        val specificSettings: MutableMap<String, ProgramSetting> = HashMap()
        specificSettings[p1] = ProgramSetting.builder().uid(p1).eventsDownload(100).build()
        Mockito.`when`(programSettings!!.specificSettings()).thenReturn(specificSettings)
        val bundles = bundleFactory!!.getQueries(params)
        Truth.assertThat(bundles.size).isEqualTo(2)
        for (bundle in bundles) {
            if (bundle.commonParams().programs.size == 1) {
                Truth.assertThat(bundle.commonParams().programs[0]).isEqualTo(p1)
                Truth.assertThat(bundle.commonParams().limit).isEqualTo(100)
            } else {
                Truth.assertThat(bundle.commonParams().limit).isEqualTo(5000)
            }
        }
    }
}