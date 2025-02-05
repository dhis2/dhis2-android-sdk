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
package org.hisp.dhis.android.core.settings

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.network.settings.AppearanceSettingsDTO
import org.junit.Test
import java.io.IOException
import java.text.ParseException

class AppearanceSettingsV2Should : BaseObjectKotlinxShould("settings/appearance_settings_v2.json"), ObjectShould {
    @Test
    @Suppress("LongMethod")
    @Throws(IOException::class, ParseException::class)
    override fun map_from_json_string() {
        val appearanceSettingsDTO = deserialize(AppearanceSettingsDTO.serializer())
        val appearanceSettings = appearanceSettingsDTO.toDomain()

        val filterSorting = appearanceSettings.filterSorting()
        val homeFilters = filterSorting!!.home()
        val homeDateFilter = homeFilters[HomeFilter.DATE]!!
        assertThat(homeDateFilter.scope()).isNull()
        assertThat(homeDateFilter.filterType()).isNull()
        assertThat(homeDateFilter.uid()).isNull()
        assertThat(homeDateFilter.sort()).isEqualTo(true)
        assertThat(homeDateFilter.filter()).isEqualTo(true)

        val dataSetFilters = filterSorting.dataSetSettings()
        val dataSetGlobalFilters = dataSetFilters.globalSettings()
        val dataSetPeriodFilter = dataSetGlobalFilters[DataSetFilter.PERIOD]!!
        assertThat(dataSetPeriodFilter.scope()).isNull()
        assertThat(dataSetPeriodFilter.filterType()).isNull()
        assertThat(dataSetPeriodFilter.uid()).isNull()
        assertThat(dataSetPeriodFilter.sort()).isEqualTo(true)
        assertThat(dataSetPeriodFilter.filter()).isEqualTo(true)

        val programFilters = filterSorting.programSettings()
        val programGlobalFilters = programFilters.globalSettings()
        val programEventDateFilter = programGlobalFilters[ProgramFilter.EVENT_DATE]!!
        assertThat(programEventDateFilter.scope()).isNull()
        assertThat(programEventDateFilter.filterType()).isNull()
        assertThat(programEventDateFilter.uid()).isNull()
        assertThat(programEventDateFilter.sort()).isEqualTo(true)
        assertThat(programEventDateFilter.filter()).isEqualTo(true)

        val programConfiguration = appearanceSettings.programConfiguration()!!
        assertThat(programConfiguration.globalSettings()!!.uid()).isNull()
        assertThat(programConfiguration.globalSettings()!!.completionSpinner()).isEqualTo(true)
        assertThat(programConfiguration.globalSettings()!!.disableReferrals()).isEqualTo(true)
        assertThat(programConfiguration.globalSettings()!!.disableCollapsibleSections()).isEqualTo(false)
        assertThat(programConfiguration.globalSettings()!!.minimumLocationAccuracy()).isEqualTo(7)
        assertThat(programConfiguration.globalSettings()!!.disableManualLocation()).isEqualTo(false)

        val specificProgramConfigurations = programConfiguration.specificSettings()
        val specificProgramConfiguration = specificProgramConfigurations!!["IpHINAT79UW"]!!
        assertThat(specificProgramConfiguration.uid()).isNull()
        assertThat(specificProgramConfiguration.completionSpinner()).isEqualTo(true)
        assertThat(specificProgramConfiguration.optionalSearch()).isEqualTo(true)
        assertThat(specificProgramConfiguration.disableReferrals()).isEqualTo(true)
        assertThat(specificProgramConfiguration.disableCollapsibleSections()).isEqualTo(false)
        assertThat(specificProgramConfiguration.itemHeader()!!.programIndicator()).isEqualTo("kALwOyvVvdT")
        assertThat(specificProgramConfiguration.minimumLocationAccuracy()).isEqualTo(5)
        assertThat(specificProgramConfiguration.disableManualLocation()).isEqualTo(true)
        assertThat(specificProgramConfiguration.quickActions()?.size).isEqualTo(2)
        assertThat(specificProgramConfiguration.quickActions()?.get(1)?.actionId()).isEqualTo("MORE_ENROLLMENTS")

        val dataSetConfiguration = appearanceSettings.dataSetConfiguration()!!
        assertThat(dataSetConfiguration.globalSettings()!!.uid()).isNull()
        assertThat(dataSetConfiguration.globalSettings()!!.minimumLocationAccuracy()).isEqualTo(7)
        assertThat(dataSetConfiguration.globalSettings()!!.disableManualLocation()).isEqualTo(false)
        val specificDataSetConfigurations = dataSetConfiguration.specificSettings()
        val specificDataSetConfiguration = specificDataSetConfigurations!!["lyLU2wR22tC"]!!
        assertThat(specificDataSetConfiguration.uid()).isNull()
        assertThat(specificDataSetConfiguration.minimumLocationAccuracy()).isEqualTo(8)
        assertThat(specificDataSetConfiguration.disableManualLocation()).isEqualTo(true)

        // Compatibility backwards
        val completionSpinnerSetting = appearanceSettings.completionSpinner()
        assertThat(completionSpinnerSetting!!.globalSettings()!!.uid()).isNull()

        val specificCompletionSpinnerList = completionSpinnerSetting.specificSettings()
        val specificCompletionSpinner = specificCompletionSpinnerList!!["IpHINAT79UW"]!!
        assertThat(specificCompletionSpinner.uid()).isNull()
        assertThat(specificCompletionSpinner.visible()).isEqualTo(true)
    }
}
