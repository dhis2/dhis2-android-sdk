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

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class WeeklyPeriodGeneratorShould {

    @Test
    public void generate_weekly_periods_for_one_week() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 2, 8);
        PeriodModel period = generateExpectedPeriod("2018W10", calendar, Calendar.MONDAY, PeriodType.Weekly);

        List<PeriodModel> generatedPeriods = WeeklyPeriodGeneratorFactory.weekly(calendar)
                .generateLastPeriods(1);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_weekly_periods() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018,2,8);
        PeriodModel period1 = generateExpectedPeriod("2018W10", calendar, Calendar.MONDAY, PeriodType.Weekly);
        calendar.set(2018, 2, 15);
        PeriodModel period2 = generateExpectedPeriod("2018W11", calendar, Calendar.MONDAY, PeriodType.Weekly);

        List<PeriodModel> generatedPeriods = WeeklyPeriodGeneratorFactory.weekly(calendar)
                .generateLastPeriods(2);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period1, period2));
    }

    @Test
    public void generate_weekly_periods_for_changing_year() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016,11,31);
        PeriodModel period1 = generateExpectedPeriod("2016W52", calendar, Calendar.MONDAY, PeriodType.Weekly);
        calendar.set(2017, 0, 7);
        PeriodModel period2 = generateExpectedPeriod("2017W1", calendar, Calendar.MONDAY, PeriodType.Weekly);
        calendar.set(2017, 0, 14);
        PeriodModel period3 = generateExpectedPeriod("2017W2", calendar, Calendar.MONDAY, PeriodType.Weekly);

        List<PeriodModel> generatedPeriods = WeeklyPeriodGeneratorFactory.weekly(calendar)
                .generateLastPeriods(3);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period1, period2, period3));
    }

    @Test
    public void generate_the_first_week_including_january_4() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 0, 4);

        List<PeriodModel> generatedPeriods = WeeklyPeriodGeneratorFactory
                .weekly(calendar).generateLastPeriods(1);
        List<PeriodModel> generatedWedPeriods = WeeklyPeriodGeneratorFactory
                .wednesday(calendar).generateLastPeriods(1);
        List<PeriodModel> generatedThuPeriods = WeeklyPeriodGeneratorFactory
                .thursday(calendar).generateLastPeriods(1);
        List<PeriodModel> generatedSatPeriods = WeeklyPeriodGeneratorFactory
                .saturday(calendar).generateLastPeriods(1);
        List<PeriodModel> generatedSunPeriods = WeeklyPeriodGeneratorFactory
                .sunday(calendar).generateLastPeriods(1);

        PeriodModel period = generateExpectedPeriod("2018W1", calendar,
                Calendar.MONDAY, PeriodType.Weekly);
        PeriodModel periodWednesday = generateExpectedPeriod("2018WedW1", calendar,
                Calendar.WEDNESDAY, PeriodType.WeeklyWednesday);
        PeriodModel periodThursday = generateExpectedPeriod("2018ThuW1", calendar,
                Calendar.THURSDAY, PeriodType.WeeklyThursday);
        PeriodModel periodSaturday = generateExpectedPeriod("2018SatW1", calendar,
                Calendar.SATURDAY, PeriodType.WeeklySaturday);
        PeriodModel periodSunday = generateExpectedPeriod("2018SunW1", calendar,
                Calendar.SUNDAY, PeriodType.WeeklySunday);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
        assertThat(generatedWedPeriods).isEqualTo(Lists.newArrayList(periodWednesday));
        assertThat(generatedThuPeriods).isEqualTo(Lists.newArrayList(periodThursday));
        assertThat(generatedSatPeriods).isEqualTo(Lists.newArrayList(periodSaturday));
        assertThat(generatedSunPeriods).isEqualTo(Lists.newArrayList(periodSunday));
    }

    private PeriodModel generateExpectedPeriod(String id, Calendar cal, int weekStartDay,
                                               PeriodType periodType) {
        Calendar calendar = (Calendar) cal.clone();
        AbstractPeriodGenerator.setCalendarToStartTimeOfADay(calendar);
        calendar.getTime();
        calendar.setFirstDayOfWeek(weekStartDay);
        calendar.setMinimalDaysInFirstWeek(4);
        calendar.set(Calendar.DAY_OF_WEEK, weekStartDay);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        Date endDate = calendar.getTime();

        return PeriodModel.builder()
                .periodId(id)
                .periodType(periodType)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}