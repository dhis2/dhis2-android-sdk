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
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.network.settings.AppearanceSettingsDTO
import org.hisp.dhis.android.network.settings.FilterSettingDTO.Companion.FILTERSETTING_GLOBAL_ID
import org.junit.Test

class AppearanceSettingsV1Should : CoreObjectShould("settings/appearance_settings_v1.json") {
    @Test
    override fun map_from_json_string() {
        val appearanceSettingsDTO = deserialize(AppearanceSettingsDTO.serializer())
        val appearanceSettings = appearanceSettingsDTO.toDomain()

        val filterSorting = appearanceSettings.filterSorting()
        val homeFilters = filterSorting!!.home()
        val homeDateFilter = homeFilters[HomeFilter.DATE]!!
        assertThat(homeDateFilter.scope()).isNull()
        assertThat(homeDateFilter.filterType()).isNull()
        assertThat(homeDateFilter.uid()).isEqualTo(null)
        assertThat(homeDateFilter.sort()).isEqualTo(true)
        assertThat(homeDateFilter.filter()).isEqualTo(true)

        val dataSetFilters = filterSorting.dataSetSettings()
        val dataSetGlobalFilters = dataSetFilters.globalSettings()
        val dataSetPeriodFilter = dataSetGlobalFilters[DataSetFilter.PERIOD]!!
        assertThat(dataSetPeriodFilter.scope()).isNull()
        assertThat(dataSetPeriodFilter.filterType()).isNull()
        assertThat(dataSetPeriodFilter.uid()).isEqualTo(FILTERSETTING_GLOBAL_ID)
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

        val completionSpinnerSetting = appearanceSettings.completionSpinner()
        assertThat(completionSpinnerSetting!!.globalSettings()!!.uid()).isNull()

        val specificCompletionSpinnerList = completionSpinnerSetting.specificSettings()
        val specificCompletionSpinner = specificCompletionSpinnerList!!["IpHINAT79UW"]!!
        assertThat(specificCompletionSpinner.uid()).isNull()
        assertThat(specificCompletionSpinner.visible()).isEqualTo(true)

        // Compatibility forwards
        val programConfiguration = appearanceSettings.programConfiguration()
        assertThat(programConfiguration!!.globalSettings()!!.uid()).isNull()

        val speficicProgramConfiguration = programConfiguration.specificSettings()
        val specificProgramConfiguration = speficicProgramConfiguration!!["IpHINAT79UW"]!!
        assertThat(specificProgramConfiguration.uid()).isNull()
        assertThat(specificProgramConfiguration.completionSpinner()).isEqualTo(true)
        assertThat(specificProgramConfiguration.optionalSearch()).isNull()
    }
}
