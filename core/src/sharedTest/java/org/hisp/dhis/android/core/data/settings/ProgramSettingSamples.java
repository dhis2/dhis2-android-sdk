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

package org.hisp.dhis.android.core.data.settings;

import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils;
import org.hisp.dhis.android.core.settings.DownloadPeriod;
import org.hisp.dhis.android.core.settings.EnrollmentScope;
import org.hisp.dhis.android.core.settings.LimitScope;
import org.hisp.dhis.android.core.settings.ProgramSetting;

public class ProgramSettingSamples {

    public static ProgramSetting getProgramSetting() {
        return ProgramSetting.builder()
                .id(1L)
                .uid("IpHINAT79UW")
                .name("Child Programme")
                .lastUpdated(FillPropertiesTestUtils.LAST_UPDATED)
                .teiDownload(40)
                .teiDBTrimming(20)
                .eventsDownload(30)
                .eventsDBTrimming(20)
                .updateDownload(DownloadPeriod.ANY)
                .updateDBTrimming(DownloadPeriod.LAST_3_MONTHS)
                .settingDownload(LimitScope.PER_ORG_UNIT)
                .settingDBTrimming(LimitScope.GLOBAL)
                .enrollmentDownload(EnrollmentScope.ONLY_ACTIVE)
                .enrollmentDBTrimming(EnrollmentScope.ALL)
                .eventDateDownload(DownloadPeriod.ANY)
                .eventDateDBTrimming(DownloadPeriod.LAST_12_MONTHS)
                .enrollmentDateDownload(DownloadPeriod.LAST_3_MONTHS)
                .enrollmentDateDBTrimming(DownloadPeriod.LAST_12_MONTHS)
                .build();
    }
}