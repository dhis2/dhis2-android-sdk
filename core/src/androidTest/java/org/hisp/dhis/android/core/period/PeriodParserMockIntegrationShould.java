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

package org.hisp.dhis.android.core.period;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class PeriodParserMockIntegrationShould extends BaseMockIntegrationTestEmptyDispatcher {

    private final List<String> PERIOD_ID_LIST = Lists.newArrayList(
            "20200315", "2019W40", "2020W1", "2020W10", "2020W53",
            "2020WedW5", "2020ThuW6", "2020SatW7", "2020SunW8",
            "2020BiW1", "2019BiW15", "2020BiW25",
            "202003","202012", "202001B", "2020Q1","2020Q4",
            "2020S1", "2020AprilS1", "2020NovS1", "2020NovS2", "2020",
            "2020April", "2020July", "2020Oct", "2020Nov");

    @AfterClass
    public static void tearDown() {
        d2.databaseAdapter().delete(PeriodTableInfo.TABLE_INFO.name());
    }

    @Test
    public void get_period_passing_period_id() {
        for (String periodId : PERIOD_ID_LIST) {
            Period period = d2.periodModule().periodHelper().blockingGetPeriodForPeriodId(periodId);
            assertThat(period.periodId()).isEqualTo(periodId);
        }
    }
}