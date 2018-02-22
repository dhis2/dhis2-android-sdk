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
        PeriodModel period = generateExpectedPeriod("2018W10", calendar, Calendar.MONDAY);

        List<PeriodModel> generatedPeriods = new WeeklyPeriodGenerator(
                calendar, PeriodType.Weekly, Calendar.MONDAY, "W").generateLastPeriods(1);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_weekly_periods() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018,2,8);
        PeriodModel period1 = generateExpectedPeriod("2018W10", calendar, Calendar.MONDAY);
        calendar.set(2018, 2, 15);
        PeriodModel period2 = generateExpectedPeriod("2018W11", calendar, Calendar.MONDAY);

        List<PeriodModel> generatedPeriods = new WeeklyPeriodGenerator(
                calendar, PeriodType.Weekly, Calendar.MONDAY, "W").generateLastPeriods(2);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period1, period2));
    }

    @Test
    public void generate_weekly_periods_for_changing_year() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016,11,31);
        PeriodModel period1 = generateExpectedPeriod("2016W52", calendar, Calendar.MONDAY);
        calendar.set(2017, 0, 7);
        PeriodModel period2 = generateExpectedPeriod("2017W1", calendar, Calendar.MONDAY);
        calendar.set(2017, 0, 14);
        PeriodModel period3 = generateExpectedPeriod("2017W2", calendar, Calendar.MONDAY);

        List<PeriodModel> generatedPeriods = new WeeklyPeriodGenerator(
                calendar, PeriodType.Weekly, Calendar.MONDAY, "W").generateLastPeriods(3);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period1, period2, period3));
    }

    @Test
    public void generate_the_first_week_including_january_4() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 0, 4);

        List<PeriodModel> generatedPeriods = new WeeklyPeriodGenerator(
                calendar, PeriodType.Weekly, Calendar.MONDAY, "W").generateLastPeriods(1);
        List<PeriodModel> generatedWedPeriods = new WeeklyPeriodGenerator(
                calendar, PeriodType.Weekly, Calendar.WEDNESDAY, "WedW").generateLastPeriods(1);
        List<PeriodModel> generatedThuPeriods = new WeeklyPeriodGenerator(
                calendar, PeriodType.Weekly, Calendar.THURSDAY, "ThuW").generateLastPeriods(1);
        List<PeriodModel> generatedSatPeriods = new WeeklyPeriodGenerator(
                calendar, PeriodType.Weekly, Calendar.SATURDAY, "SatW").generateLastPeriods(1);
        List<PeriodModel> generatedSunPeriods = new WeeklyPeriodGenerator(
                calendar, PeriodType.Weekly, Calendar.SUNDAY, "SunW").generateLastPeriods(1);

        PeriodModel period = generateExpectedPeriod("2018W1", calendar, Calendar.MONDAY);
        PeriodModel periodWednesday = generateExpectedPeriod("2018WedW1", calendar, Calendar.WEDNESDAY);
        PeriodModel periodThursday = generateExpectedPeriod("2018ThuW1", calendar, Calendar.THURSDAY);
        PeriodModel periodSaturday = generateExpectedPeriod("2018SatW1", calendar, Calendar.SATURDAY);
        PeriodModel periodSunday = generateExpectedPeriod("2018SunW1", calendar, Calendar.SUNDAY);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
        assertThat(generatedWedPeriods).isEqualTo(Lists.newArrayList(periodWednesday));
        assertThat(generatedThuPeriods).isEqualTo(Lists.newArrayList(periodThursday));
        assertThat(generatedSatPeriods).isEqualTo(Lists.newArrayList(periodSaturday));
        assertThat(generatedSunPeriods).isEqualTo(Lists.newArrayList(periodSunday));
    }

    @Test
    public void throw_exception_for_negative_weeks() throws Exception {
        try {
            new WeeklyPeriodGenerator(
                    Calendar.getInstance(), PeriodType.Weekly, Calendar.MONDAY, "W")
                    .generateLastPeriods(-12);
            fail("Exception was expected, but nothing was thrown.");
        } catch (RuntimeException e) {
            // No operation.
        }
    }

    @Test
    public void throw_exception_for_zero_weeks() throws Exception {
        try {
            new WeeklyPeriodGenerator(
                    Calendar.getInstance(), PeriodType.Weekly, Calendar.MONDAY, "W")
                    .generateLastPeriods(0);
            fail("Exception was expected, but nothing was thrown.");
        } catch (RuntimeException e) {
            // No operation.
        }
    }

    private PeriodModel generateExpectedPeriod(String id, Calendar cal, int weekStartDay) {
        Calendar calendar = (Calendar) cal.clone();
        AbstractPeriodGenerator.setCalendarToStartTimeOfADay(calendar);
        calendar.getTime();
        calendar.setFirstDayOfWeek(weekStartDay);
        calendar.setMinimalDaysInFirstWeek(4);
        calendar.set(Calendar.DAY_OF_WEEK, weekStartDay);
        Date startDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_WEEK, weekStartDay + 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar.getTime();

        return PeriodModel.builder()
                .periodId(id)
                .periodType(PeriodType.Weekly)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}