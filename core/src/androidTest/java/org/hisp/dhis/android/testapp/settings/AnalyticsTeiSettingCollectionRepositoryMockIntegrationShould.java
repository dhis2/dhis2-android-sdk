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

import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.settings.AnalyticsTeiSetting;
import org.hisp.dhis.android.core.settings.ChartType;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class AnalyticsTeiSettingCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_analytics_tei_settings() {
        List<AnalyticsTeiSetting> teiSettings = d2.settingModule().analyticsSetting().teis().blockingGet();

        assertThat(teiSettings.size()).isEqualTo(3);
    }

    @Test
    public void filter_by_name() {
        List<AnalyticsTeiSetting> teiSettings = d2.settingModule().analyticsSetting().teis()
                .byName().eq("Height evolution")
                .blockingGet();
        assertThat(teiSettings.size()).isEqualTo(1);

        AnalyticsTeiSetting teiSetting = teiSettings.get(0);
        assertThat(teiSetting.data().dataElements().size()).isEqualTo(2);
        assertThat(teiSetting.data().indicators().size()).isEqualTo(0);
        assertThat(teiSetting.data().attributes().size()).isEqualTo(0);
    }

    @Test
    public void filter_by_short_name() {
        List<AnalyticsTeiSetting> teiSettings = d2.settingModule().analyticsSetting().teis()
                .byShortName().eq("H. evolution")
                .blockingGet();
        assertThat(teiSettings.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_program() {
        List<AnalyticsTeiSetting> teiSettings = d2.settingModule().analyticsSetting().teis()
                .byProgram().eq("IpHINAT79UW")
                .blockingGet();
        assertThat(teiSettings.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_program_stage() {
        List<AnalyticsTeiSetting> teiSettings = d2.settingModule().analyticsSetting().teis()
                .byProgramStage().eq("dBwrot7S420")
                .blockingGet();
        assertThat(teiSettings.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_period() {
        List<AnalyticsTeiSetting> teiSettings = d2.settingModule().analyticsSetting().teis()
                .byPeriod().eq(PeriodType.Monthly)
                .blockingGet();
        assertThat(teiSettings.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_type() {
        List<AnalyticsTeiSetting> teiSettings = d2.settingModule().analyticsSetting().teis()
                .byType().eq(ChartType.LINE)
                .blockingGet();
        assertThat(teiSettings.size()).isEqualTo(1);
    }
}