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

package org.hisp.dhis.android.core.settings;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProgramSettingsShould extends BaseObjectShould implements ObjectShould {

    public ProgramSettingsShould() {
        super("settings/program_settings.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ProgramSettings programSettings = objectMapper.readValue(jsonStream, ProgramSettings.class);

        ProgramSetting global = programSettings.globalSettings();
        assertThat(global.uid()).isNull();
        assertThat(global.name()).isNull();
        assertThat(global.lastUpdated()).isEqualTo(BaseIdentifiableObject.parseDate("2020-02-01T20:02:46.145Z"));
        assertThat(global.teiDownload()).isEqualTo(500);
        assertThat(global.teiDBTrimming()).isEqualTo(500);
        assertThat(global.eventsDownload()).isEqualTo(1000);
        assertThat(global.eventsDBTrimming()).isEqualTo(1000);
        assertThat(global.updateDownload()).isEqualByComparingTo(DownloadPeriod.ANY);
        assertThat(global.updateDBTrimming()).isEqualByComparingTo(DownloadPeriod.ANY);
        assertThat(global.settingDownload()).isEqualByComparingTo(LimitScope.PER_ORG_UNIT);
        assertThat(global.settingDBTrimming()).isEqualByComparingTo(LimitScope.GLOBAL);
        assertThat(global.enrollmentDownload()).isEqualByComparingTo(EnrollmentScope.ALL);
        assertThat(global.enrollmentDBTrimming()).isEqualByComparingTo(EnrollmentScope.ALL);
        assertThat(global.eventDateDownload()).isEqualByComparingTo(DownloadPeriod.ANY);
        assertThat(global.eventDateDBTrimming()).isEqualByComparingTo(DownloadPeriod.ANY);
        assertThat(global.enrollmentDateDownload()).isEqualByComparingTo(DownloadPeriod.ANY);
        assertThat(global.enrollmentDateDBTrimming()).isEqualByComparingTo(DownloadPeriod.ANY);

        ProgramSetting childProgramme = programSettings.specificSettings().get("IpHINAT79UW");
        assertThat(childProgramme).isNotNull();
        assertThat(childProgramme.uid()).isEqualTo("IpHINAT79UW");
        assertThat(childProgramme.name()).isEqualTo("Child Programme");
        assertThat(childProgramme.lastUpdated()).isEqualTo(BaseIdentifiableObject.parseDate("2020-02-01T19:55:32.002Z"));
        assertThat(childProgramme.teiDownload()).isEqualTo(40);
        assertThat(childProgramme.teiDBTrimming()).isEqualTo(20);
        assertThat(childProgramme.eventsDownload()).isEqualTo(30);
        assertThat(childProgramme.eventsDBTrimming()).isEqualTo(20);
        assertThat(childProgramme.updateDownload()).isEqualByComparingTo(DownloadPeriod.ANY);
        assertThat(childProgramme.updateDBTrimming()).isEqualByComparingTo(DownloadPeriod.LAST_3_MONTHS);
        assertThat(childProgramme.settingDownload()).isEqualByComparingTo(LimitScope.ALL_ORG_UNITS);
        assertThat(childProgramme.settingDBTrimming()).isEqualByComparingTo(LimitScope.PER_ORG_UNIT);
        assertThat(childProgramme.enrollmentDownload()).isEqualByComparingTo(EnrollmentScope.ALL);
        assertThat(childProgramme.enrollmentDBTrimming()).isEqualByComparingTo(EnrollmentScope.ONLY_ACTIVE);
        assertThat(childProgramme.eventDateDownload()).isEqualByComparingTo(DownloadPeriod.ANY);
        assertThat(childProgramme.eventDateDBTrimming()).isEqualByComparingTo(DownloadPeriod.LAST_MONTH);
        assertThat(childProgramme.enrollmentDateDownload()).isEqualByComparingTo(DownloadPeriod.ANY);
        assertThat(childProgramme.enrollmentDateDBTrimming()).isEqualByComparingTo(DownloadPeriod.LAST_MONTH);
    }
}
