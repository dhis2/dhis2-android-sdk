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

package org.hisp.dhis.android.testapp.maintenance;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.utils.integration.BaseIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.period.PeriodType;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class D2ErrorCollectionRepositoryMockIntegrationShould extends BaseIntegrationTestFullDispatcher {

    @Test
    public void filter_d2_error_by_resource_type() {
        List<D2Error> d2Errors = d2.maintenanceModule()
                .d2Errors.byResourceType().eq("Program").get();
        assertThat(d2Errors.size(), is(1));
    }

    @Test
    public void filter_d2_error_by_uid() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors
                .byUid().like("test_uid").get();
        assertThat(d2Errors.size(), is(1));
    }

    @Test
    public void filter_d2_error_by_url() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors
                .byUrl().like("http://dhis2.org/api/programs/uid").get();
        assertThat(d2Errors.size(), is(1));
    }

    @Test
    public void filter_d2_error_by_d2_error_code() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors
                .byD2ErrorCode().eq(D2ErrorCode.DIFFERENT_SERVER_OFFLINE).get();
        assertThat(d2Errors.size(), is(1));
    }

    @Test
    public void filter_d2_error_by_d2_error_component() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors
                .byD2ErrorComponent().eq(D2ErrorComponent.Server).get();
        assertThat(d2Errors.size(), is(1));
    }

    @Test
    public void filter_d2_error_by_error_description() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors
                .byErrorDescription().eq("Error processing response").get();
        assertThat(d2Errors.size(), is(1));
    }

    @Test
    public void filter_d2_error_by_http_error_code() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors
                .byHttpErrorCode().eq(402).get();
        assertThat(d2Errors.size(), is(1));
    }

    @Test
    public void filter_d2_error_by_created() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors
                .byCreated().inPeriods(Lists.newArrayList(
                        d2.periodModule().periodHelper.getPeriod(PeriodType.Monthly, new Date()))).get();
        assertThat(d2Errors.size(), is(2));
    }
}