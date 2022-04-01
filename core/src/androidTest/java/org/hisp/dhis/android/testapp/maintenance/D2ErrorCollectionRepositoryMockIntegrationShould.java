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

package org.hisp.dhis.android.testapp.maintenance;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class D2ErrorCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void filter_d2_error_by_url() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors()
                .byUrl().like("http://dhis2.org/api/programs/uid").blockingGet();
        assertThat(d2Errors.size()).isEqualTo(1);
    }

    @Test
    public void filter_d2_error_by_d2_error_code() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors()
                .byD2ErrorCode().eq(D2ErrorCode.BAD_CREDENTIALS).blockingGet();
        assertThat(d2Errors.size()).isEqualTo(1);
    }

    @Test
    public void filter_d2_error_by_d2_error_component() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors()
                .byD2ErrorComponent().eq(D2ErrorComponent.Server).blockingGet();
        assertThat(d2Errors.size()).isEqualTo(1);
    }

    @Test
    public void filter_d2_error_by_error_description() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors()
                .byErrorDescription().eq("Error processing response").blockingGet();
        assertThat(d2Errors.size()).isEqualTo(1);
    }

    @Test
    public void filter_d2_error_by_http_error_code() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors()
                .byHttpErrorCode().eq(402).blockingGet();
        assertThat(d2Errors.size()).isEqualTo(1);
    }

    @Test
    public void filter_d2_error_by_created() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startDate = cal.getTime();

        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date endDate = cal.getTime();

        Period todayPeriod = Period.builder()
                .periodType(PeriodType.Daily)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors()
                .byCreated().inPeriods(Lists.newArrayList(todayPeriod)).blockingGet();
        assertThat(d2Errors.size()).isEqualTo(2);
    }
}