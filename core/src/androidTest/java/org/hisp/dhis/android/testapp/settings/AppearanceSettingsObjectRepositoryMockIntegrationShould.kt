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
package org.hisp.dhis.android.testapp.settings

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.settings.DataSetFilter
import org.hisp.dhis.android.core.settings.HomeFilter
import org.hisp.dhis.android.core.settings.ProgramFilter
import org.hisp.dhis.android.core.settings.QuickAction
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class AppearanceSettingsObjectRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_appearance_settings() {
        val appearanceSettings = d2.settingModule().appearanceSettings().blockingGet()!!
        assertThat(appearanceSettings.filterSorting()).isNotNull()
        assertThat(appearanceSettings.completionSpinner()).isNotNull()
    }

    @Test
    fun should_return_only_homeFilters() {
        val homeFilters = d2.settingModule().appearanceSettings().getHomeFilters()
        assertThat(homeFilters?.size).isEqualTo(4)
        assertThat(homeFilters!![HomeFilter.DATE]!!.filter()).isEqualTo(true)
    }

    @Test
    fun should_return_only_dataSetFilters_for_specific_uid() {
        val specificFilters =
            d2.settingModule().appearanceSettings().getDataSetFiltersByUid(program3)
        assertThat(specificFilters?.size).isEqualTo(5)
        assertThat(specificFilters!![DataSetFilter.SYNC_STATUS]!!.uid()).isEqualTo(program3)
    }

    @Test
    fun should_return_only_programFilters_for_specific_uid() {
        val specificFilters =
            d2.settingModule().appearanceSettings().getProgramFiltersByUid(program1)
        assertThat(specificFilters?.size).isEqualTo(7)
        assertThat(specificFilters!![ProgramFilter.ENROLLMENT_STATUS]!!.uid()).isEqualTo(program1)
    }

    @Test
    fun should_return_global_program_configuration_setting() {
        val setting = d2.settingModule().appearanceSettings().getGlobalProgramConfigurationSetting()
        assertThat(setting?.uid()).isNull()
        assertThat(setting?.completionSpinner()).isEqualTo(true)
        assertThat(setting?.optionalSearch()).isNull()
    }

    @Test
    fun should_return_global_completion_spinner_settings() {
        val completionSpinner = d2.settingModule().appearanceSettings().getGlobalCompletionSpinner()
        assertThat(completionSpinner?.uid()).isNull()
        assertThat(completionSpinner?.visible()).isEqualTo(true)
    }

    @Test
    fun should_return_program_configuration_setting_for_specific_uid() {
        val setting = d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program1)
        assertThat(setting?.uid()).isEqualTo(program1)
        assertThat(setting?.completionSpinner()).isEqualTo(true)
        assertThat(setting?.optionalSearch()).isEqualTo(true)
    }

    @Test
    fun should_return_completion_spinner_settings_for_specific_uid() {
        val completionSpinner =
            d2.settingModule().appearanceSettings().getCompletionSpinnerByUid(program1)
        assertThat(completionSpinner?.uid()).isEqualTo(program1)
        assertThat(completionSpinner?.visible()).isEqualTo(true)
    }

    @Test
    fun should_return_disable_referrals_settings() {
        val setting = d2.settingModule().appearanceSettings().getGlobalProgramConfigurationSetting()
        assertThat(setting?.disableReferrals()).isEqualTo(true)

        val program1Setting =
            d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program1)
        assertThat(program1Setting?.disableReferrals()).isEqualTo(true)

        val program2Setting =
            d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program2)
        assertThat(program2Setting?.disableReferrals()).isEqualTo(false)
    }

    @Test
    fun should_return_collapsibleSections_settings() {
        val setting = d2.settingModule().appearanceSettings().getGlobalProgramConfigurationSetting()
        assertThat(setting?.disableCollapsibleSections()).isEqualTo(false)

        val program1Setting =
            d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program1)
        assertThat(program1Setting?.disableCollapsibleSections()).isEqualTo(false)

        val program2Setting =
            d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program2)
        assertThat(program2Setting?.disableCollapsibleSections()).isEqualTo(true)
    }

    @Test
    fun should_return_program_indicator_header_settings() {
        val setting = d2.settingModule().appearanceSettings().getGlobalProgramConfigurationSetting()
        assertThat(setting?.itemHeader()?.programIndicator()).isNull()

        val program1Setting =
            d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program1)
        assertThat(program1Setting?.itemHeader()?.programIndicator()).isEqualTo("kALwOyvVvdT")

        val program2Setting =
            d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program2)
        assertThat(program2Setting?.itemHeader()?.programIndicator()).isNull()
    }

    @Test
    fun should_return_minimumLocationAccuracy_program_settings() {
        val setting = d2.settingModule().appearanceSettings().getGlobalProgramConfigurationSetting()
        assertThat(setting?.minimumLocationAccuracy()).isEqualTo(7)

        val program1Setting =
            d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program1)
        assertThat(program1Setting?.minimumLocationAccuracy()).isEqualTo(5)

        val program2Setting =
            d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program2)
        assertThat(program2Setting?.minimumLocationAccuracy()).isEqualTo(null)
    }

    @Test
    fun should_return_disableManualLocation_program_settings() {
        val setting = d2.settingModule().appearanceSettings().getGlobalProgramConfigurationSetting()
        assertThat(setting?.disableManualLocation()).isEqualTo(false)

        val program1Setting =
            d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program1)
        assertThat(program1Setting?.disableManualLocation()).isEqualTo(true)

        val program2Setting =
            d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program2)
        assertThat(program2Setting?.disableManualLocation()).isEqualTo(false)
    }

    @Test
    fun should_return_minimumLocationAccuracy_dataSet_settings() {
        val setting = d2.settingModule().appearanceSettings().getGlobalDataSetConfigurationSetting()
        assertThat(setting?.minimumLocationAccuracy()).isEqualTo(7)

        val dataSet1Setting =
            d2.settingModule().appearanceSettings().getDataSetConfigurationByUid(dataSet1)
        assertThat(dataSet1Setting?.minimumLocationAccuracy()).isEqualTo(8)

    }

    @Test
    fun should_return_disableManualLocation_dataSet_settings() {
        val setting = d2.settingModule().appearanceSettings().getGlobalDataSetConfigurationSetting()
        assertThat(setting?.disableManualLocation()).isEqualTo(false)

        val dataSet1Setting =
            d2.settingModule().appearanceSettings().getDataSetConfigurationByUid(dataSet1)
        assertThat(dataSet1Setting?.disableManualLocation()).isEqualTo(true)

    }

    @Test
    fun should_return_quickAction_settings() {
        val setting = d2.settingModule().appearanceSettings().getGlobalProgramConfigurationSetting()
        assertThat(setting?.quickActions()?.size).isEqualTo(0)

        val program1Setting =
            d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program1)
        assertThat(program1Setting?.quickActions()?.size).isEqualTo(2)
        assertThat(program1Setting?.quickActions()?.first()).isInstanceOf(QuickAction::class.java)

        val program2Setting =
            d2.settingModule().appearanceSettings().getProgramConfigurationByUid(program2)
        assertThat(program2Setting?.quickActions()?.size).isEqualTo(3)
    }

    companion object {
        const val program1 = "IpHINAT79UW"
        const val program2 = "IpHINAT79UQ"
        const val program3 = "lyLU2wR22tC"
        const val dataSet1 = "lyLU2wR22tC"
    }
}
