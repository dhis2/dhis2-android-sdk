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

package org.hisp.dhis.android.testapp.settings;

import org.hisp.dhis.android.core.settings.AppearanceSettings;
import org.hisp.dhis.android.core.settings.CompletionSpinner;
import org.hisp.dhis.android.core.settings.DataSetFilter;
import org.hisp.dhis.android.core.settings.FilterSetting;
import org.hisp.dhis.android.core.settings.HomeFilter;
import org.hisp.dhis.android.core.settings.ProgramConfigurationSetting;
import org.hisp.dhis.android.core.settings.ProgramFilter;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class AppearanceSettingsObjectRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_appearance_settings() {
        AppearanceSettings appearanceSettings = d2.settingModule().appearanceSettings().blockingGet();

        assertThat(appearanceSettings.filterSorting()).isNotNull();
        assertThat(appearanceSettings.completionSpinner()).isNotNull();
    }

    @Test
    public void should_return_only_homeFilters() {
        Map<HomeFilter, FilterSetting> homeFilters = d2.settingModule().appearanceSettings().getHomeFilters();
        assertThat(homeFilters.size()).isEqualTo(4);
        assertThat(homeFilters.get(HomeFilter.DATE).filter()).isEqualTo(true);
    }

    @Test
    public void should_return_only_dataSetFilters_for_specific_uid() {
        String UID = "lyLU2wR22tC";
        Map<DataSetFilter, FilterSetting> specificFilters = d2.settingModule().appearanceSettings().getDataSetFiltersByUid(UID);
        assertThat(specificFilters.size()).isEqualTo(5);
        assertThat(specificFilters.get(DataSetFilter.SYNC_STATUS).uid()).isEqualTo(UID);
    }

    @Test
    public void should_return_only_programFilters_for_specific_uid() {
        String UID = "IpHINAT79UW";
        Map<ProgramFilter, FilterSetting> specificFilters = d2.settingModule().appearanceSettings().getProgramFiltersByUid(UID);
        assertThat(specificFilters.size()).isEqualTo(7);
        assertThat(specificFilters.get(ProgramFilter.ENROLLMENT_STATUS).uid()).isEqualTo(UID);
    }

    @Test
    public void should_return_global_program_configuration_setting() {
        ProgramConfigurationSetting setting = d2.settingModule().appearanceSettings().getGlobalProgramConfigurationSetting();
        assertThat(setting.uid()).isNull();
        assertThat(setting.completionSpinner()).isEqualTo(true);
        assertThat(setting.optionalSearch()).isNull();
    }

    @Test
    public void should_return_global_completion_spinner_settings() {
        CompletionSpinner completionSpinner = d2.settingModule().appearanceSettings().getGlobalCompletionSpinner();
        assertThat(completionSpinner.uid()).isNull();
        assertThat(completionSpinner.visible()).isEqualTo(true);
    }

    @Test
    public void should_return_program_configuration_setting_for_specific_uid() {
        String UID = "IpHINAT79UW";
        ProgramConfigurationSetting setting = d2.settingModule().appearanceSettings().getProgramConfigurationByUid(UID);
        assertThat(setting.uid()).isEqualTo(UID);
        assertThat(setting.completionSpinner()).isEqualTo(true);
        assertThat(setting.optionalSearch()).isEqualTo(true);
    }

    @Test
    public void should_return_completion_spinner_settings_for_specific_uid() {
        String UID = "IpHINAT79UW";
        CompletionSpinner completionSpinner = d2.settingModule().appearanceSettings().getCompletionSpinnerByUid(UID);
        assertThat(completionSpinner.uid()).isEqualTo(UID);
        assertThat(completionSpinner.visible()).isEqualTo(true);
    }
}
