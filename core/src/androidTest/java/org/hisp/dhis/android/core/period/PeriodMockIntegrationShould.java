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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.period.internal.PeriodStore;
import org.hisp.dhis.android.core.period.internal.PeriodStoreImpl;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class PeriodMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void get_period_passing_period_type_and_a_date() throws ParseException {
        Period period = d2.periodModule().periodHelper().blockingGetPeriodForPeriodTypeAndDate(
                PeriodType.BiWeekly, BaseIdentifiableObject.DATE_FORMAT.parse("2019-07-08T12:24:25.319"));
        assertThat(period.periodId()).isEqualTo("2019BiW14");
    }

    @Test
    public void create_period_in_database_if_not_exist() throws ParseException {
        PeriodStore periodStore = PeriodStoreImpl.create(databaseAdapter);

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse("2010-12-24T12:24:25.319");

        Period periodInDb = periodStore.selectPeriodByTypeAndDate(PeriodType.Monthly, date);
        assertThat(periodInDb).isNull();

        Period period = d2.periodModule().periodHelper().blockingGetPeriodForPeriodTypeAndDate(
                PeriodType.Monthly, date);
        assertThat(period).isNotNull();
        assertThat(period.periodId()).isEqualTo("201012");

        periodInDb = periodStore.selectPeriodByTypeAndDate(PeriodType.Monthly, date);
        assertThat(periodInDb).isNotNull();

        periodStore.deleteWhere(period);
    }

    @Test
    public void get_periods() {
        assertThat(d2.periodModule().periods().blockingCount()).isEqualTo(206);
    }

    @Test
    public void ensure_future_periods_are_downloaded() {
        int MONTHLY_PERIODS = 11; // Copy from ParentPeriodGeneratorImpl to keep it private
        assertThat(d2.periodModule().periods().byPeriodType().eq(PeriodType.Monthly).blockingCount())
                .isEqualTo(MONTHLY_PERIODS + 4);
    }
}