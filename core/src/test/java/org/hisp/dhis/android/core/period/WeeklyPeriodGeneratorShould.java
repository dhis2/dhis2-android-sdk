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
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class WeeklyPeriodGeneratorShould {

    @Test
    public void generate_weekly_periods_for_one_week() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 2, 8);
        Calendar calendar1 = (Calendar) calendar.clone();
        PeriodModel period = generateExpectedPeriod("2018W10", calendar1);

        List<PeriodModel> generatedPeriods = new WeeklyPeriodGenerator(calendar).generatePeriodsForLastWeeks(1);
        List<PeriodModel> expectedPeriods = Lists.newArrayList(period);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void generate_weekly_periods() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018,2,8);
        PeriodModel period1 = generateExpectedPeriod("2018W10", calendar);
        calendar.set(2018, 2, 15);
        PeriodModel period2 = generateExpectedPeriod("2018W11", calendar);

        List<PeriodModel> generatedPeriods = new WeeklyPeriodGenerator(calendar).generatePeriodsForLastWeeks(2);
        List<PeriodModel> expectedPeriods = Lists.newArrayList(period1, period2);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void generate_weekly_periods_for_changing_year() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016,11,31);
        PeriodModel period1 = generateExpectedPeriod("2016W52", calendar);
        calendar.set(2017, 0, 7);
        PeriodModel period2 = generateExpectedPeriod("2017W1", calendar);
        calendar.set(2017, 0, 14);
        PeriodModel period3 = generateExpectedPeriod("2017W2", calendar);

        List<PeriodModel> generatedPeriods = new WeeklyPeriodGenerator(calendar).generatePeriodsForLastWeeks(3);
        List<PeriodModel> expectedPeriods = Lists.newArrayList(period1, period2, period3);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void throw_exception_for_negative_weeks() throws Exception {
        try {
            new WeeklyPeriodGenerator(Calendar.getInstance()).generatePeriodsForLastWeeks(-12);
            fail("Exception was expected, but nothing was thrown.");
        } catch (RuntimeException e) {
            // No operation.
        }
    }

    @Test
    public void throw_exception_for_zero_weeks() throws Exception {
        try {
            new WeeklyPeriodGenerator(Calendar.getInstance()).generatePeriodsForLastWeeks(0);
            fail("Exception was expected, but nothing was thrown.");
        } catch (RuntimeException e) {
            // No operation.
        }
    }

    private PeriodModel generateExpectedPeriod(String id, Calendar calendar) {
        calendar.getTime();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Date endDate = calendar.getTime();

        return PeriodModel.builder()
                .periodId(id)
                .periodType(PeriodType.Weekly)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}