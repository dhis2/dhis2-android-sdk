/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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
package org.hisp.dhis.android.core.period;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class DailyPeriodGeneratorShould {

    @Test
    public void generate_daily_periods_for_one_day() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 1, 1);
        PeriodModel period = generateExpectedPeriod("20180201", calendar);

        List<PeriodModel> generatedPeriods = new DailyPeriodGenerator(calendar).generatePeriodsForLastDays(1);
        List<PeriodModel> expectedPeriods = Lists.newArrayList(period);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void generate_daily_periods() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018,2,4);
        PeriodModel period1 = generateExpectedPeriod("20180304", calendar);
        calendar.set(2018, 2, 5);
        PeriodModel period2 = generateExpectedPeriod("20180305", calendar);

        List<PeriodModel> generatedPeriods = new DailyPeriodGenerator(calendar).generatePeriodsForLastDays(2);
        List<PeriodModel> expectedPeriods = Lists.newArrayList(period1, period2);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void generate_daily_periods_for_changing_year() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017,11,31);
        PeriodModel period1 = generateExpectedPeriod("20171231", calendar);
        calendar.set(2018, 0, 1);
        PeriodModel period2 = generateExpectedPeriod("20180101", calendar);
        calendar.set(2018, 0, 2);
        PeriodModel period3 = generateExpectedPeriod("20180102", calendar);

        List<PeriodModel> generatedPeriods = new DailyPeriodGenerator(calendar).generatePeriodsForLastDays(3);
        List<PeriodModel> expectedPeriods = Lists.newArrayList(period1, period2, period3);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void throw_exception_for_negative_days() throws Exception {
        try {
            new DailyPeriodGenerator(Calendar.getInstance()).generatePeriodsForLastDays(-12);
            fail("Exception was expected, but nothing was thrown.");
        } catch (RuntimeException e) {
            // No operation.
        }
    }

    @Test
    public void throw_exception_for_zero_days() throws Exception {
        try {
            new DailyPeriodGenerator(Calendar.getInstance()).generatePeriodsForLastDays(0);
            fail("Exception was expected, but nothing was thrown.");
        } catch (RuntimeException e) {
            // No operation.
        }
    }

    private PeriodModel generateExpectedPeriod(String id, Calendar calendar) {
        return PeriodModel.builder()
                .periodId(id)
                .periodType(PeriodType.Daily)
                .startDate(calendar.getTime())
                .endDate(calendar.getTime())
                .build();
    }
}