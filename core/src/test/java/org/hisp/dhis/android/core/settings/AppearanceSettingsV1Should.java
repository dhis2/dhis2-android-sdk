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

package org.hisp.dhis.android.core.settings;

import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class AppearanceSettingsV1Should extends BaseObjectShould implements ObjectShould {

    public AppearanceSettingsV1Should() {
        super("settings/appearance_settings_v1.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {

        AppearanceSettings appearanceSettings = objectMapper.readValue(jsonStream, AppearanceSettings.class);

        FilterSorting filterSorting = appearanceSettings.filterSorting();

        Map<HomeFilter, FilterSetting> homeFilters = filterSorting.home();
        FilterSetting homeDateFilter = homeFilters.get(HomeFilter.DATE);

        assertThat(homeDateFilter.scope()).isNull();
        assertThat(homeDateFilter.filterType()).isNull();
        assertThat(homeDateFilter.uid()).isNull();
        assertThat(homeDateFilter.sort()).isEqualTo(true);
        assertThat(homeDateFilter.filter()).isEqualTo(true);

        DataSetFilters dataSetFilters = filterSorting.dataSetSettings();
        Map<DataSetFilter, FilterSetting> dataSetGlobalFilters = dataSetFilters.globalSettings();
        FilterSetting dataSetPeriodFilter = dataSetGlobalFilters.get(DataSetFilter.PERIOD);
        assertThat(dataSetPeriodFilter.scope()).isNull();
        assertThat(dataSetPeriodFilter.filterType()).isNull();
        assertThat(dataSetPeriodFilter.uid()).isNull();
        assertThat(dataSetPeriodFilter.sort()).isEqualTo(true);
        assertThat(dataSetPeriodFilter.filter()).isEqualTo(true);

        ProgramFilters programFilters = filterSorting.programSettings();
        Map<ProgramFilter, FilterSetting> programGlobalFilters = programFilters.globalSettings();
        FilterSetting programEventDateFilter = programGlobalFilters.get(ProgramFilter.EVENT_DATE);
        assertThat(programEventDateFilter.scope()).isNull();
        assertThat(programEventDateFilter.filterType()).isNull();
        assertThat(programEventDateFilter.uid()).isNull();
        assertThat(programEventDateFilter.sort()).isEqualTo(true);
        assertThat(programEventDateFilter.filter()).isEqualTo(true);

        CompletionSpinnerSetting completionSpinnerSetting = appearanceSettings.completionSpinner();
        assertThat(completionSpinnerSetting.globalSettings().uid()).isNull();

        Map<String, CompletionSpinner> specificCompletionSpinnerList = completionSpinnerSetting.specificSettings();
        CompletionSpinner specificCompletionSpinner = specificCompletionSpinnerList.get("IpHINAT79UW");
        assertThat(specificCompletionSpinner.uid()).isNull();
        assertThat(specificCompletionSpinner.visible()).isEqualTo(true);

        // Compatibility forwards
        ProgramConfigurationSettings programConfiguration = appearanceSettings.programConfiguration();
        assertThat(programConfiguration.globalSettings().uid()).isNull();

        Map<String, ProgramConfigurationSetting> speficicProgramConfiguration = programConfiguration.specificSettings();
        ProgramConfigurationSetting specificProgramConfiguration = speficicProgramConfiguration.get("IpHINAT79UW");
        assertThat(specificProgramConfiguration.uid()).isNull();
        assertThat(specificProgramConfiguration.completionSpinner()).isEqualTo(true);
        assertThat(specificProgramConfiguration.optionalSearch()).isNull();
    }
}
